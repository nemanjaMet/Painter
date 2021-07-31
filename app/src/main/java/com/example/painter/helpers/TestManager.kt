package com.example.painter.helpers

import android.graphics.PointF
import com.example.painter.constants.Constants
import com.example.painter.constants.CustomPathMode
import com.example.painter.models.CanvasSize
import com.example.painter.models.CustomPath
import com.example.painter.models.DrawPath
import com.example.painter.models.SavedDrawing
import kotlin.random.Random

class TestManager {

    companion object {

        fun createDrawPath(canvasSize: CanvasSize, maxNumberOfPoints: Long = 1000L): DrawPath {
            val drawPath = DrawPath()

            val customPath = CustomPath()

            val numberOfPoints = Random.nextLong(10, maxNumberOfPoints)

            for (i in 0 until numberOfPoints) {

                val x = Random.nextInt(0, canvasSize.width).toFloat()
                val y = Random.nextInt(0, canvasSize.height).toFloat()

                val pointF = PointF(x, y)
                val drawMode = if (i == 0L || i == numberOfPoints - 1)
                    CustomPathMode.MOVE_TO
                else
                    CustomPathMode.LINE_TO

                customPath.add(pointF, drawMode)
            }

            drawPath.customPath = customPath

            drawPath.customPaint.strokeWidth = 0.01f * canvasSize.width

            return drawPath
        }

        fun createSavedDrawing(canvasSize: CanvasSize, maxNumberOfPathsSize: Long = 50, maxNumberOfPointsInPath: Long = 1000L, id: Long = -1): SavedDrawing {

            val savedDrawing = SavedDrawing()

            savedDrawing.id = id
            savedDrawing.title = if (id == -1L) "" else "Title $id"
            savedDrawing.canvasSize = canvasSize
            savedDrawing.date = HelperManager.getCurrentDateAsString()

            val numberOfPaths = Random.nextLong(5, maxNumberOfPathsSize)

            for (i in 0 until numberOfPaths) {
                //savedDrawing.drawings.add(createDrawPath(canvasSize, maxNumberOfPointsInPath))
            }

            return savedDrawing
        }

    }

}