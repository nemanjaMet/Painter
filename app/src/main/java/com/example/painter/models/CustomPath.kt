package com.example.painter.models

import android.graphics.Path
import android.graphics.PointF
import android.util.Log
import com.example.painter.constants.CustomPathMode

data class CustomPath (
        private var points: MutableList<PointF> = mutableListOf(),
        private var drawModes: MutableList<CustomPathMode> = mutableListOf()
)

{
    fun add(pointF: PointF, mode: CustomPathMode) {
        points.add(pointF)
        drawModes.add(mode)
    }

    fun getPoint(index: Int): PointF {
        return points[index]
    }

    fun getMode(index: Int): CustomPathMode {
        return drawModes[index]
    }

    fun addPoints(points: MutableList<PointF>) {
        this.points.addAll(points)
    }

    fun addDrawModes(drawModes: MutableList<CustomPathMode>) {
        this.drawModes.addAll(drawModes)
    }

    fun reset() {
        points.clear()
        drawModes.clear()
    }

    fun copy(): CustomPath {
        val customPathCopy = CustomPath()
        customPathCopy.addPoints(points)
        customPathCopy.addDrawModes(drawModes)

        return customPathCopy
    }

    fun isEmpty(): Boolean {
        return points.isEmpty()
    }

    fun remove(index: Int) {
        if (!points.isNullOrEmpty() && index < points.size)
            points.removeAt(index)
        if (!drawModes.isNullOrEmpty() && index < drawModes.size)
            drawModes.removeAt(index)
    }

    fun getSize(): Int {
        return points.size
    }

    fun getPoints(): MutableList<PointF> {
        return points
    }

    fun getPath(): Path {

        val path = Path()

//        for (i in 0 until points.size) {
//
//            val point = points[i]
//
//            when (drawModes[i]) {
//                CustomPathMode.MOVE_TO -> {
//                    path.moveTo(point.x, point.y)
//                    Log.d("getPath", "$i -> path.MoveTo(${point.x}, ${point.y})")
//                }
//                CustomPathMode.LINE_TO -> {
//                    path.lineTo(point.x, point.y)
//                    Log.d("getPath", "$i -> path.LineTo(${point.x}, ${point.y})")
//                }
//                else -> {
//
//                }
//            }
//
//        }

        for ((i, p) in points.withIndex()) {
            when (drawModes[i]) {
                CustomPathMode.MOVE_TO -> {
                    path.moveTo(p.x, p.y)
                    Log.d("getPath", "$i -> path.MoveTo(${p.x}, ${p.y})")
                }
                CustomPathMode.LINE_TO -> {
                    path.lineTo(p.x, p.y)
                    Log.d("getPath", "$i -> path.LineTo(${p.x}, ${p.y})")
                }
                else -> {

                }
            }
        }


        return path
    }

}