package com.example.painter.models

data class SavedDrawing (
    var id: Long = 0L,
    var title: String = "",
    var drawings: MutableList<Drawing> = mutableListOf(),
    var date: String = "",
    var canvasSize: CanvasSize = CanvasSize(0,0),
    var thumbnailPath: String = ""
)