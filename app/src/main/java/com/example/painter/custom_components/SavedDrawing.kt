package com.example.painter.custom_components

import android.util.Size
import com.example.painter.models.DrawPath

data class SavedDrawing (
    var id: Long = 0L,
    var title: String = "",
    var drawings: MutableList<DrawPath> = mutableListOf(),
    var date: String = "",
    var canvasSize: Size = Size(0,0)
)