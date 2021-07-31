package com.example.painter.models

import android.graphics.Color
import android.graphics.Paint

data class CustomPaint (
        var strokeWidth: Float = 0f,
        var color: Int = Color.BLACK,
        var alpha: Int = 0xFF
)

{
    fun setPaint(paint: Paint) {

        paint.strokeWidth = strokeWidth
        paint.color = color
        paint.alpha = alpha

    }

    fun savePaint(paint: Paint) {
        strokeWidth = paint.strokeWidth
        color = paint.color
        alpha = paint.alpha
    }
}