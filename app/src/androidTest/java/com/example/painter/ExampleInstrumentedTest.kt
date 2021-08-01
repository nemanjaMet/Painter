package com.example.painter

import android.graphics.Bitmap
import android.util.Size
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.painter.helpers.HelperManager

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.painter", appContext.packageName)
    }

    @Test
    fun getScaledBitmap_ReturnsTrue() {

        val currentBitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888)
        val scaledBitmap = HelperManager.getScaledBitmap(currentBitmap, 500)

        val scaledBitmapSize = Size(scaledBitmap.width, scaledBitmap.height)

        val expectedSizeOfBitmap = Size(500, 889)

        assert(scaledBitmapSize == expectedSizeOfBitmap)
    }
}