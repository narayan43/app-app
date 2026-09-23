package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.model.AppItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class InstalledAppsRepository(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val packageManager: PackageManager = context.packageManager

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val iconCache = mutableMapOf<String, ImageBitmap>()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Package event received: ${intent?.action}")
            loadApps()
        }
    }

    init {
        registerPackageReceiver()
        loadApps()
    }

    private fun registerPackageReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(packageReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(packageReceiver, filter)
        }
    }

    fun unregister() {
        try {
            context.unregisterReceiver(packageReceiver)
        } catch (_: Exception) {}
    }

    fun loadApps() {
        scope.launch {
            _isLoading.value = true
            val apps = withContext(Dispatchers.IO) {
                queryAndProcessApps()
            }
            _installedApps.value = apps
            _isLoading.value = false
        }
    }

    private fun queryAndProcessApps(): List<AppItem> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    mainIntent,
                    PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                packageManager.queryIntentActivities(mainIntent, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying launcher intents", e)
            emptyList()
        }

        val myPackageName = context.packageName

        val list = mutableListOf<AppItem>()
        for (info in resolveInfos) {
            val pkg = info.activityInfo?.packageName ?: continue
            val activityName = info.activityInfo?.name ?: ""

            // Don't show the launcher itself in its own app drawer
            if (pkg == myPackageName) continue

            val label = try {
                info.loadLabel(packageManager).toString().trim().ifBlank {
                    info.activityInfo?.name?.substringAfterLast('.') ?: pkg
                }
            } catch (_: Exception) {
                pkg
            }

            val firstLetter = label.firstOrNull()?.uppercaseChar() ?: '#'
            val normalizedChar = if (firstLetter in 'A'..'Z') firstLetter else '#'

            // Load and cache icon
            val icon = getOrLoadIcon(pkg, info)

            val installTime = try {
                val pkgInfo = packageManager.getPackageInfo(pkg, 0)
                pkgInfo.firstInstallTime
            } catch (_: Exception) {
                0L
            }

            list.add(
                AppItem(
                    packageName = pkg,
                    activityName = activityName,
                    label = label,
                    icon = icon,
                    firstChar = normalizedChar,
                    installTime = installTime
                )
            )
        }

        // Sort alphabetically by label ignoring case
        return list.sortedWith(
            compareBy<AppItem> {
                if (it.firstChar == '#') "zzzz" else it.firstChar.toString()
            }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
        )
    }

    private fun getOrLoadIcon(packageName: String, info: ResolveInfo): ImageBitmap? {
        iconCache[packageName]?.let { return it }

        val drawable: Drawable? = try {
            info.loadIcon(packageManager)
        } catch (_: Exception) {
            try {
                packageManager.getApplicationIcon(packageName)
            } catch (_: Exception) {
                null
            }
        }

        val bitmap = drawableToBitmap(drawable) ?: return null
        val imageBitmap = bitmap.asImageBitmap()
        iconCache[packageName] = imageBitmap
        return imageBitmap
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        val size = 96
        return try {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error converting drawable to bitmap", e)
            null
        }
    }

    fun launchApp(app: AppItem) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            } else {
                Toast.makeText(context, "Cannot launch ${app.label}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error launching app ${app.packageName}", e)
            Toast.makeText(context, "Failed to launch ${app.label}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening app settings", e)
        }
    }

    fun requestUninstall(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching uninstall", e)
        }
    }

    companion object {
        private const val TAG = "InstalledAppsRepo"
    }
}
