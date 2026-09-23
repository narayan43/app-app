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
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error registering package receiver", e)
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
            // Phase 1: Fast query without icons to paint the UI immediately and avoid any OOM or launch timeouts
            val appsWithoutIcons = withContext(Dispatchers.IO) {
                queryFastApps()
            }

            if (appsWithoutIcons.isNotEmpty()) {
                _installedApps.value = appsWithoutIcons
                _isLoading.value = false
            }

            // Phase 2: Asynchronously load icons in background without blocking the first frame
            val appsWithIcons = withContext(Dispatchers.IO) {
                loadIconsForApps(appsWithoutIcons)
            }

            if (appsWithIcons.isNotEmpty()) {
                _installedApps.value = appsWithIcons
            }
            _isLoading.value = false
        }
    }

    private fun queryFastApps(): List<AppItem> {
        val list = mutableListOf<AppItem>()
        val myPackageName = context.packageName

        try {
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
            } catch (e: Throwable) {
                Log.e(TAG, "Error querying launcher intents", e)
                emptyList()
            }

            for (info in resolveInfos) {
                try {
                    val pkg = info.activityInfo?.packageName ?: continue
                    val activityName = info.activityInfo?.name ?: ""

                    if (pkg == myPackageName) continue

                    val label = try {
                        info.loadLabel(packageManager).toString().trim().ifBlank {
                            info.activityInfo?.name?.substringAfterLast('.') ?: pkg
                        }
                    } catch (_: Throwable) {
                        pkg
                    }

                    val firstLetter = label.firstOrNull()?.uppercaseChar() ?: '#'
                    val normalizedChar = if (firstLetter in 'A'..'Z') firstLetter else '#'

                    list.add(
                        AppItem(
                            packageName = pkg,
                            activityName = activityName,
                            label = label,
                            icon = iconCache[pkg], // Use cached icon if already loaded
                            firstChar = normalizedChar,
                            installTime = 0L
                        )
                    )
                } catch (e: Throwable) {
                    Log.e(TAG, "Error processing app info item", e)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "General failure in queryFastApps", e)
        }

        // Fallback: If ColorOS / OEM queryIntentActivities returned nothing, try getInstalledApplications
        if (list.isEmpty()) {
            try {
                val installedApps = packageManager.getInstalledApplications(0)
                for (appInfo in installedApps) {
                    try {
                        val pkg = appInfo.packageName
                        if (pkg == myPackageName) continue
                        val launchIntent = packageManager.getLaunchIntentForPackage(pkg) ?: continue
                        val activityName = launchIntent.component?.className ?: ""
                        val label = try {
                            appInfo.loadLabel(packageManager).toString().trim().ifBlank { pkg }
                        } catch (_: Throwable) {
                            pkg
                        }
                        val firstLetter = label.firstOrNull()?.uppercaseChar() ?: '#'
                        val normalizedChar = if (firstLetter in 'A'..'Z') firstLetter else '#'

                        list.add(
                            AppItem(
                                packageName = pkg,
                                activityName = activityName,
                                label = label,
                                icon = iconCache[pkg],
                                firstChar = normalizedChar,
                                installTime = 0L
                            )
                        )
                    } catch (_: Throwable) {}
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Fallback query failed", e)
            }
        }

        // Guaranteed fallback so launcher never has an empty list and remains alive
        if (list.isEmpty()) {
            list.add(
                AppItem(
                    packageName = "com.android.settings",
                    activityName = "com.android.settings.Settings",
                    label = "Settings",
                    icon = null,
                    firstChar = 'S',
                    installTime = 0L
                )
            )
        }

        return try {
            list.sortedWith(
                compareBy<AppItem> {
                    if (it.firstChar == '#') "zzzz" else it.firstChar.toString()
                }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
            )
        } catch (_: Throwable) {
            list
        }
    }

    private fun loadIconsForApps(apps: List<AppItem>): List<AppItem> {
        val updatedList = mutableListOf<AppItem>()
        for (app in apps) {
            try {
                val icon = if (app.icon != null) {
                    app.icon
                } else {
                    getOrLoadIcon(app.packageName)
                }
                updatedList.add(app.copy(icon = icon))
            } catch (e: Throwable) {
                updatedList.add(app)
            }
        }
        return updatedList
    }

    private fun getOrLoadIcon(packageName: String): ImageBitmap? {
        iconCache[packageName]?.let { return it }

        val drawable: Drawable? = try {
            packageManager.getApplicationIcon(packageName)
        } catch (_: Throwable) {
            null
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
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth.coerceIn(48, 192) else size
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight.coerceIn(48, 192) else size
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Throwable) {
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
