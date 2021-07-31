package com.example.painter.constants

import android.graphics.Color

class Constants {

    companion object {
        const val DEFAULT_PEN_COLOR = Color.BLACK
        const val DEFAULT_BOARD_COLOR = Color.WHITE
        const val DEFAULT_PEN_SIZE_PERCENT = 10f
        const val SAVED_DRAWINGS_FILE_NAME = "painter_drawings.json"
    }

    class Permission {

        object WriteExternalStorage {
            const val REQUEST_CODE = 101
            const val PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

    }

    object CanvasDimensions {
        const val MAX_WIDTH = 3840
        const val MIN_WIDTH = 100
        const val MAX_HEIGHT = 2160
        const val MIN_HEIGHT = 100
    }

}