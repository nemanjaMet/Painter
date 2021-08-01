package com.example.painter.models

/**
 * model za cuvanje jednog kompletnog crteza
 */
data class SavedDrawing (
    var id: Long = 0L, // id nam nije potreban, cisto onako je ubacen
    var title: String = "",
    var drawings: MutableList<Drawing> = mutableListOf(),
    var date: String = "",
    var canvasSize: CanvasSize = CanvasSize(0,0),
    var thumbnailPath: String = ""
)