package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.fluid.FluidPathGenerator
import com.example.fluid.FluidState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Flow Launcher", appName)
  }

  @Test
  fun `verify alphabet count and ordering`() {
    assertEquals(27, FluidState.ALPHABET.size)
    assertEquals('A', FluidState.ALPHABET.first())
    assertEquals('#', FluidState.ALPHABET.last())
  }

  @Test
  fun `generate fluid path when active`() {
    val path = FluidPathGenerator.createFluidPath(
      isRightSide = true,
      railEdgeX = 1080f,
      apexY = 500f,
      velocityY = 100f,
      expansion = 1f,
      intensity = 1f,
      wobble = 0f,
      screenHeight = 2400f
    )
    assertNotNull(path)
  }
}
