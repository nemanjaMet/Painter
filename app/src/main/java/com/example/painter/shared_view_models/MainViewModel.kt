package com.example.painter.shared_view_models

import android.content.Context
import android.graphics.Bitmap
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
import com.example.painter.models.CanvasSize
import com.example.painter.models.Drawing
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private var _listOfDrawings = MutableLiveData(mutableListOf<SavedDrawing>())
    val listOfDrawings: LiveData<MutableList<SavedDrawing>> get() = _listOfDrawings
    private var _exportImageMode = MutableLiveData(ProgressMode.NONE)
    val exportImageMode: LiveData<ProgressMode> get() = _exportImageMode
    private var _savingProgressMode =  MutableLiveData(ProgressMode.NONE)
    val savingProgressMode: LiveData<ProgressMode> get() = _savingProgressMode

    /**
     *  ucitavamo sacuvane crteze
     */
    fun loadDrawings(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            // dobijamo json iz fajla
            val json = FileManager.loadSavedImagesList(FileManager.getFileSavedImageList(context))

            // parsujemo json
            val drawings = PainterManager.getDrawingsFromJson(json)

            _listOfDrawings.postValue(drawings)
            Log.d("MainViewModel", "loaded")
        }

        //deleteSavedFile(context)
    }

    // f-ja za brisanje fajla
    private fun deleteSavedFile(context: Context) {
        FileManager.deleteFile(context, mutableListOf(FileManager.getFileSavedImageList(context).path))
    }

    /**
     *  pravimo novi savedDrawing objekat za cuvanje crteza i dodajemo ga u listu sacuvanih crteza
     */
    private fun addNewDrawing(drawing: MutableList<Drawing>, title: String, canvasSize: CanvasSize, thumbnailPath: String = "") {

        val savedDrawing = SavedDrawing()
        savedDrawing.drawings.addAll(drawing)

        savedDrawing.id = getId()
        savedDrawing.title = title
        savedDrawing.canvasSize = canvasSize
        savedDrawing.date = HelperManager.getCurrentDateAsString()
        savedDrawing.thumbnailPath = thumbnailPath

        _listOfDrawings.value?.add(savedDrawing)
    }

    /**
     *  cuvamo novi crtez i kompletnu listu
     */
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


    /**
     *  ne toliko bitna f-ja za dobijanje id-a sacuvanog crteza
     */
    private fun getId(): Long {

        _listOfDrawings.value?.let {

            if (it.isEmpty())
                return 0L

            return it.last().id + 1L
        }

        return 0L
    }

    /**
     *  eksportujemo sliku (kreiramo bitmapu a zatim je cuvamo u folder downloads)
     */
    fun exportImage(context: Context, position: Int) {

        listOfDrawings.value?.let {  list ->

            if (position >= list.size)
                return

            val savedDrawing = list[position]

            viewModelScope.launch(Dispatchers.IO) {
                _exportImageMode.postValue(ProgressMode.IN_PROGRESS)

                // kreiramo bitmapu
                val exportBitmap = PainterManager.createBitmap(savedDrawing)
                // cuvamo u download folder
                val fileUri = FileManager.exportImage(context, exportBitmap)

                Log.d("exportImage", fileUri)

                val finishedProgressMode = ProgressMode.FINISHED
                finishedProgressMode.value = fileUri

                Log.d("exportImage", finishedProgressMode.value)

                _exportImageMode.postValue(finishedProgressMode)
            }


        }

    }

    /**
     *  resetujemo mode export image na neutralno stanje
     */
    fun resetExportImageMode() {
        _exportImageMode.value = ProgressMode.NONE
    }

    /**
     *  resetujemo mode saving progress na neutralno stanje
     */
    fun resetSavingProgressMode() {
        _savingProgressMode.value = ProgressMode.NONE
    }

}