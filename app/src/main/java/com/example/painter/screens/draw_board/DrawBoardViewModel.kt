package com.example.painter.screens.draw_board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.painter.constants.Constants
import com.example.painter.models.CanvasSize
import com.example.painter.models.Drawing

class DrawBoardViewModel: ViewModel() {

    private var _penColor = MutableLiveData(Constants.DEFAULT_PEN_COLOR)
    val penColor: LiveData<Int> get() = _penColor
    private var _penSizePercent = MutableLiveData(Constants.DEFAULT_PEN_SIZE_PERCENT)
    val penSizePercent: LiveData<Float> get() = _penSizePercent
    private var _canvasSize = MutableLiveData(CanvasSize())
    val canvasSize: LiveData<CanvasSize> get() = _canvasSize

    // lista poteza koju cuvamo zbog promene orijentacije
    var savedDrawing = mutableListOf<Drawing>()

    /**
     *  setujemo boju za olovku/cetkicu
     */
    fun setPenColor(color: Int) {
        _penColor.value = color
    }

    fun getPenColor(): Int {
        return penColor.value ?: Constants.DEFAULT_PEN_COLOR
    }

    /**
     *  setujemo velicinu za olovku/cetkicu
     */
    fun setPenSizePercent(percent: Float) {
        _penSizePercent.value = percent
    }

    /**
     *  setujemo velicinu za canvas
     */
    fun setCanvasSize(canvasSize: CanvasSize) {
        this._canvasSize.value = canvasSize
    }


}