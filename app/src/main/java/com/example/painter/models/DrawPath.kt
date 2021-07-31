package com.example.painter.models

import android.graphics.Paint
import android.graphics.Path
import android.util.Log

data class DrawPath(
        var path: Path = Path(),
        var paint: Paint = Paint(),
        var isDeletePath: Boolean = false, // fleg koji nam oznava da li je path koriscen za brisanje
        var customPath: CustomPath = CustomPath(),
        var customPaint: CustomPaint = CustomPaint()
)

{

    fun getDrawPath(): Path {

        if (path.isEmpty && !customPath.isEmpty())
            path = customPath.getPath()

        return path
    }

    fun setPathFromCustomPath() {
        if (path.isEmpty && !customPath.isEmpty())
            path = customPath.getPath()
        Log.d("setPathFromCustomPath", "isPath empty -> ${path.isEmpty}")
    }

    fun setPaintFromCustomPaint(paint: Paint) {

        paint.strokeWidth = customPaint.strokeWidth
        paint.alpha = customPaint.alpha
        paint.color = customPaint.color

    }

}
