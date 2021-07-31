package com.example.painter.shared_view_models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.painter.constants.ProgressMode
import com.example.painter.models.SavedDrawing
import com.example.painter.helpers.FileManager
import com.example.painter.helpers.HelperManager
import com.example.painter.helpers.PainterManager
import com.example.painter.helpers.TestManager
import com.example.painter.models.CanvasSize
import com.example.painter.models.Drawing
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    //private var listOfDrawings = mutableListOf<SavedDrawing>()
    private var _listOfDrawings = MutableLiveData(mutableListOf<SavedDrawing>())
    val listOfDrawings: LiveData<MutableList<SavedDrawing>> get() = _listOfDrawings
    private var _exportImageMode = MutableLiveData(ProgressMode.NONE)
    val exportImageMode: LiveData<ProgressMode> get() = _exportImageMode
    private var _savingProgressMode =  MutableLiveData(ProgressMode.NONE)
    val savingProgressMode: LiveData<ProgressMode> get() = _savingProgressMode

    fun loadDrawings(context: Context) {

        //listOfDrawings.clear()

        val test = Gson().toJson(PointF(1f,2f))

        viewModelScope.launch(Dispatchers.IO) {
            val json = FileManager.loadSavedImagesList(FileManager.getFileSavedImageList(context))
            //listOfDrawings = PainterManager.getDrawingsFromJson(json)

            val drawings = PainterManager.getDrawingsFromJson(json)
            //drawings.forEach { it.drawings.forEach { cp -> cp.getDrawPath() } }
            //drawings.forEach { it.drawings.forEach { cp -> cp.setPathFromCustomPath() } }

            _listOfDrawings.postValue(drawings)
            Log.d("MainViewModel", "loaded")
        }

        //deleteSavedFile(context)
    }

    private fun deleteSavedFile(context: Context) {
        FileManager.deleteFile(context, mutableListOf(FileManager.getFileSavedImageList(context).path))
    }

    fun saveDrawings(context: Context) {

        listOfDrawings.value?.let { list ->
            viewModelScope.launch(Dispatchers.IO) {
                val json = Gson().toJson(list)
                FileManager.saveImageList(FileManager.getFileSavedImageList(context), json)
                Log.d("MainViewModel", "saved")
            }
        }

    }

    fun addNewDrawing(drawing: MutableList<Drawing>, title: String, canvasSize: CanvasSize, thumbnailPath: String = "") {

        val savedDrawing = SavedDrawing()
        savedDrawing.drawings.addAll(drawing)

        savedDrawing.id = getId()
        savedDrawing.title = title //"Title ${savedDrawing.id}"
        savedDrawing.canvasSize = canvasSize //Size(0,0)
        savedDrawing.date = HelperManager.getCurrentDateAsString()
        savedDrawing.thumbnailPath = thumbnailPath

        //val testPath = drawing[0].path.

        //listOfDrawings.add(savedDrawing)

        _listOfDrawings.value?.add(savedDrawing)
    }

    fun saveNewDrawing(context: Context, drawing: MutableList<Drawing>, title: String, canvasSize: CanvasSize, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            _savingProgressMode.postValue(ProgressMode.IN_PROGRESS)

            var path = ""

            bitmap?.let {
                path = FileManager.saveThumbnail(context, HelperManager.getScaledBitmap(bitmap, 150))
            }

            addNewDrawing(drawing, title, canvasSize, path)

            listOfDrawings.value?.let { list ->
                val json = Gson().toJson(list)
                FileManager.saveImageList(FileManager.getFileSavedImageList(context), json)

                Log.d("MainViewModel", "saved")
            }

            _savingProgressMode.postValue(ProgressMode.FINISHED)
        }
    }

//    fun getSavedDrawings(): MutableList<SavedDrawing> {
//        return listOfDrawings
//    }

    private fun getId(): Long {

        _listOfDrawings.value?.let {

            if (it.isEmpty())
                return 0L

            return it.last().id + 1L
        }

        return 0L
    }

    fun createSavedDrawings() {

        val canvasSize = CanvasSize(800, 751)
        val numberOfSavedDrawings = 50
        val numberOfPaths = 50L //50L
        val numberOfPoints = 100L //1000L

        viewModelScope.launch(Dispatchers.IO) {

            val listOfSavedDrawings = mutableListOf<SavedDrawing>()

            Log.d("createSavedDrawings", "started")

            for (i in 0 until numberOfSavedDrawings) {
                listOfSavedDrawings.add(TestManager.createSavedDrawing(canvasSize, numberOfPaths, numberOfPoints, i.toLong()))
            }

            //listOfSavedDrawings.forEach { it.drawings.forEach { cp -> cp.setPathFromCustomPath() } }

            Log.d("createSavedDrawings", "finished")

            _listOfDrawings.value?.clear()
            _listOfDrawings.postValue(listOfSavedDrawings)
        }

    }

    fun createThumbnail(context: Context, bitmap: Bitmap) {

        viewModelScope.launch(Dispatchers.IO) {

            val path = FileManager.saveThumbnail(context, HelperManager.getScaledBitmap(bitmap, 100))

            Log.d("createThumbnail", "path: $path")
        }

    }

    fun exportImage(context: Context, position: Int) {

        listOfDrawings.value?.let {  list ->

            if (position >= list.size)
                return

            val savedDrawing = list[position]

            viewModelScope.launch(Dispatchers.IO) {
                _exportImageMode.postValue(ProgressMode.IN_PROGRESS)

                val exportBitmap = PainterManager.createBitmap(savedDrawing)
                val fileUri = FileManager.exportImage(context, exportBitmap)

                Log.d("exportImage", fileUri)

                val finishedProgressMode = ProgressMode.FINISHED
                finishedProgressMode.value = fileUri
                Log.d("exportImage", finishedProgressMode.value)
                _exportImageMode.postValue(finishedProgressMode)
            }


        }

    }

    fun resetExportImageMode() {
        _exportImageMode.value = ProgressMode.NONE
    }

    fun resetSavingProgressMode() {
        _savingProgressMode.value = ProgressMode.NONE
    }

}