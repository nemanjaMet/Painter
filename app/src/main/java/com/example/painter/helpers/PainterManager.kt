package com.example.painter.helpers

import android.graphics.*
import android.util.Log
import com.example.painter.constants.Constants
import com.example.painter.models.CanvasSize
import com.example.painter.models.SavedDrawing
import com.google.gson.Gson

/**
 *  pomocna klasa za izvrsavanje nekih stvari vezane za aplikaciju (parsovanje json-a, kreiranje bitmape, parsovanje velicine za canvas,...)
 */
class PainterManager {

        companion object {

            private const val TAG = "PainterManager"

            /**
             *  f-ja koja parsuje json u listu sacuvanih crteza
             */
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

            /**
             *  funkcija koja kreira crtez preko liste sacuvanih path-ova
             */
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

                return bitmap
            }

            /**
             *  pomocna funkcija za parsovanje velicine za canvas
             */
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