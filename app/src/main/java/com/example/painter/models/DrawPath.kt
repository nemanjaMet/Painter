package com.example.painter.models

import android.graphics.Paint
import android.graphics.Path

data class DrawPath(
    var path: Path,
    var paint: Paint,
    var isDeletePath: Boolean = false // fleg koji nam oznava da li je path koriscen za brisanje
)