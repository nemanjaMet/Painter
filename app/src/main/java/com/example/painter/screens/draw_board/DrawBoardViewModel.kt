package com.example.painter.screens.draw_board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.painter.constants.Constants
import com.example.painter.models.CanvasSize

class DrawBoardViewModel: ViewModel() {

    var penColorLiveData = MutableLiveData(Constants.DEFAULT_PEN_COLOR)
    var penSizePercentLiveData = MutableLiveData(Constants.DEFAULT_PEN_SIZE_PERCENT)
    private var _canvasSize = MutableLiveData(CanvasSize())
    val canvasSize: LiveData<CanvasSize> get() = _canvasSize


    fun setPenColor(color: Int) {
        penColorLiveData.value = color
    }

    fun getPenColor(): Int {
        return penColorLiveData.value ?: Constants.DEFAULT_PEN_COLOR
    }

    fun setPenSizePercent(percent: Float) {
        penSizePercentLiveData.value = percent
    }

    fun setCanvasSize(canvasSize: CanvasSize) {
        this._canvasSize.value = canvasSize
    }


}