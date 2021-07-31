package com.example.painter.models

import android.util.Size

data class CanvasSize(
        var width: Int = 0,
        var height: Int = 0
)

{
    fun getSize(): Size {
        return Size(width, height)
    }
}
