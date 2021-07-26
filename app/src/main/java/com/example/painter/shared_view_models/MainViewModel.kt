package com.example.painter.shared_view_models

import android.util.Size
import androidx.lifecycle.ViewModel
import com.example.painter.custom_components.SavedDrawing
import com.example.painter.helpers.HelperManager
import com.example.painter.models.DrawPath
import com.google.gson.Gson

class MainViewModel: ViewModel() {

    private var listOfDrawings = mutableListOf<SavedDrawing>()

    fun loadDrawings() {

    }

    fun saveDrawings() {
        val json = Gson().toJson(listOfDrawings)

    }

    fun addNewDrawing(drawing: MutableList<DrawPath>, title: String, canvasSize: Size) {

        val savedDrawing = SavedDrawing()
        savedDrawing.drawings.addAll(drawing)

        savedDrawing.id = getId()
        savedDrawing.title = title //"Title ${savedDrawing.id}"
        savedDrawing.canvasSize = canvasSize //Size(0,0)
        savedDrawing.date = HelperManager.getCurrentDateAsString()

        listOfDrawings.add(savedDrawing)
    }

    fun getSavedDrawings(): MutableList<SavedDrawing> {
        return listOfDrawings
    }

    private fun getId(): Long {

        if (listOfDrawings.isEmpty())
            return 0L

        return listOfDrawings.last().id + 1L
    }

}