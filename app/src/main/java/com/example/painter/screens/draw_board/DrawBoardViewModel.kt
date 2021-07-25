package com.example.painter.screens.draw_board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.painter.constants.Constants

class DrawBoardViewModel: ViewModel() {

    var penColorLiveData = MutableLiveData(Constants.DEFAULT_PEN_COLOR)
    var penSizePercentLiveData = MutableLiveData(Constants.DEFAULT_PEN_SIZE_PERCENT)

    fun setPenColor(color: Int) {
        penColorLiveData.value = color
    }

    fun getPenColor(): Int {
        return penColorLiveData.value ?: Constants.DEFAULT_PEN_COLOR
    }

    fun setPenSizePercent(percent: Float) {
        penSizePercentLiveData.value = percent
    }


}