package com.example.painter.helpers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.Size
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class HelperManager {

    companion object {

        /**
         *  Funkcija preko koje dobijamo datum kao string
         */
        fun getCurrentDateAsString(): String {
            val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return dateTimeFormat.format(Date())
        }

//        // skaliranje bitmape
//        fun getScaledBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
//
//            val scaledBitmap: Bitmap = Bitmap.createBitmap(width, height, bitmap.config).copy(Bitmap.Config.ARGB_8888, true)
//
//            val canvas2 = Canvas(scaledBitmap)
//            val cameraScaleFactor = min(height.toFloat() / bitmap.height.toFloat(), height.toFloat() / bitmap.width.toFloat())
//
//            canvas2.translate(width.toFloat() / 2 - bitmap.width.toFloat() / 2, height / 2f - bitmap.height / 2f)
//            canvas2.scale(cameraScaleFactor, cameraScaleFactor, bitmap.width.toFloat() / 2, bitmap.height / 2f)
//            canvas2.drawBitmap(bitmap, 0f, 0f, null)
//
//            return scaledBitmap
//        }
//
//        fun getScaledHeight(originalSize: Size, newWidth: Int): Int {
//            val newHeight = (originalSize.height / originalSize.width.toFloat()) * newWidth
//
//            return newHeight.toInt()
//        }

        fun getScaledBitmap(bitmap: Bitmap, width: Int, height: Int = ((bitmap.height.toFloat() / bitmap.width) * width).toInt()): Bitmap {

            val scaledBitmap: Bitmap = Bitmap.createBitmap(width, height, bitmap.config)
                    .copy(Bitmap.Config.ARGB_8888, true)

            val canvas2 = Canvas(scaledBitmap)
            val cameraScaleFactor = min(
                    height.toFloat() / bitmap.height.toFloat(),
                    height.toFloat() / bitmap.width.toFloat()
            )

            canvas2.translate(
                    width.toFloat() / 2 - bitmap.width.toFloat() / 2,
                    height / 2f - bitmap.height / 2f
            )
            canvas2.scale(
                    cameraScaleFactor,
                    cameraScaleFactor,
                    bitmap.width.toFloat() / 2,
                    bitmap.height / 2f
            )

            val paint = Paint()
            paint.isAntiAlias = true
            paint.isFilterBitmap = true

            canvas2.drawBitmap(bitmap, 0f, 0f, paint)

            return scaledBitmap
        }

        fun isNewApi(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }

    }

}