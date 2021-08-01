package com.example.painter.models

/**
 *  Model za cuvanje jednog path-a odnosno poteza,
 *  kao i cuvanje podesavanja za paint za taj potez
 */
data class Drawing (
        var paint: CustomPaint = CustomPaint(),
        var isDeletePath: Boolean = false, // fleg koji nam oznava da li je path koriscen za brisanje
        var path: CustomPath = CustomPath()
        )

