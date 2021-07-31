package com.example.painter.helpers

import android.graphics.*
import android.util.Log
import android.util.Size
import com.example.painter.constants.Constants
import com.example.painter.models.CanvasSize
import com.example.painter.models.DrawPath
import com.example.painter.models.SavedDrawing
import com.google.gson.Gson

class PainterManager {

        companion object {

            private const val TAG = "PainterManager"

            fun getDrawingsFromJson(json: String): MutableList<SavedDrawing> {

                var listOfDrawings = mutableListOf<SavedDrawing>()

                try {
                    if (json.isNotEmpty())
                        listOfDrawings = Gson().fromJson(json, Array<SavedDrawing>::class.java).toMutableList()
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message, ex)
                }

                return listOfDrawings
            }

            fun createBitmap(size: Size, drawing: MutableList<DrawPath>): Bitmap {

                val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                //bitmap.eraseColor(Constants.DEFAULT_BOARD_COLOR)

                val canvas = Canvas(bitmap)
                canvas.drawColor(Constants.DEFAULT_BOARD_COLOR)

                val paint = Paint()
                paint.isAntiAlias = true
                paint.style = Paint.Style.STROKE
                paint.strokeJoin = Paint.Join.ROUND
                paint.strokeCap = Paint.Cap.ROUND

                for (drawPath in drawing) {

                    // ako je ovo path za brisanje onda setujemo boju table za paint
                    if (drawPath.isDeletePath)
                        drawPath.paint.color = Constants.DEFAULT_BOARD_COLOR

                    drawPath.setPaintFromCustomPaint(paint)

                    canvas.drawPath(drawPath.getDrawPath(), paint)
                }

                //canvas.drawBitmap(this, 0f, 0f, null)

                return bitmap
            }

            private fun scaleBoard(canvas: Canvas?, newCanvasSize: CanvasSize, oldCanvasSize: CanvasSize) {
                val matrix = Matrix()

                val wRatio = newCanvasSize.width.toFloat() / oldCanvasSize.width
                val hRatio = newCanvasSize.height.toFloat() / oldCanvasSize.height

                //Log.d("drawBoard", "wRatio: $wRatio, hRatio: $hRatio")

                matrix.setScale(wRatio, hRatio)
                canvas?.setMatrix(matrix)
            }

            fun createBitmapFromPoints(size: Size, drawing: MutableList<DrawPath>): Bitmap {

                val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)

                val canvas = Canvas(bitmap)
                canvas.drawColor(Constants.DEFAULT_BOARD_COLOR)

                val paint = Paint()
                paint.isAntiAlias = true
                paint.style = Paint.Style.STROKE
                paint.strokeJoin = Paint.Join.ROUND
                paint.strokeCap = Paint.Cap.ROUND

                for (drawPath in drawing) {

                    // ako je ovo path za brisanje onda setujemo boju table za paint
                    if (drawPath.isDeletePath)
                        drawPath.paint.color = Constants.DEFAULT_BOARD_COLOR

                    drawPath.setPaintFromCustomPaint(paint)

                    canvas.drawPath(drawPath.getDrawPath(), paint)
                }

                //canvas.drawBitmap(this, 0f, 0f, null)

                return bitmap
            }

            fun createBitmap(savedDrawing: SavedDrawing): Bitmap {

                val size = savedDrawing.canvasSize.getSize()

                val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                //bitmap.eraseColor(Constants.DEFAULT_BOARD_COLOR)

                val canvas = Canvas(bitmap)
                canvas.drawColor(Constants.DEFAULT_BOARD_COLOR)

                val paint = Paint()
                paint.isAntiAlias = true
                paint.style = Paint.Style.STROKE
                paint.strokeJoin = Paint.Join.ROUND
                paint.strokeCap = Paint.Cap.ROUND

                for (drawing in savedDrawing.drawings) {

                    // ako je ovo path za brisanje onda setujemo boju table za paint
                    if (drawing.isDeletePath)
                        paint.color = Constants.DEFAULT_BOARD_COLOR
                    else
                        drawing.paint.setPaint(paint)

                    canvas.drawPath(drawing.path.getPath(), paint)
                }

                //canvas.drawBitmap(this, 0f, 0f, null)

                return bitmap
            }

            fun parseCanvasDimensions(w: String, h: String): CanvasSize {

                val canvasSize = CanvasSize(0,0)

                try {

                    val width = w.toInt()
                    val height = h.toInt()

                    if (width >= Constants.CanvasDimensions.MIN_WIDTH && width <= Constants.CanvasDimensions.MAX_WIDTH)
                        canvasSize.width = width

                    if (height >= Constants.CanvasDimensions.MIN_HEIGHT && height <= Constants.CanvasDimensions.MAX_HEIGHT)
                        canvasSize.height = height

                } catch (ex: Exception) {
                    Log.e(TAG, ex.message, ex)
                }

                return canvasSize

            }

        }

}