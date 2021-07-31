package com.example.painter.models

data class Drawing (
        var points: MutableList<Float> = mutableListOf(),
        var paint: CustomPaint = CustomPaint(),
        var isDeletePath: Boolean = false, // fleg koji nam oznava da li je path koriscen za brisanje
        var path: CustomPath = CustomPath()
        )
{


}
