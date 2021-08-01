package com.example.painter.helpers

import android.graphics.Bitmap
import android.util.Size
import com.google.common.truth.Truth
import org.junit.Test

class HelperManagerAndroidTest {
    @Test
    fun checkDimensionOfBitmap_getScaledBitmap() {

        val currentBitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888)

        val resultScaledBitmap = HelperManager.getScaledBitmap(currentBitmap, 500)

        val scaledBitmapSize = Size(resultScaledBitmap.width, resultScaledBitmap.height)

        val expectedSizeOfBitmap = Size(500, 888)

        Truth.assertThat(scaledBitmapSize).isEqualTo(expectedSizeOfBitmap)
    }
}