package com.example.painter.constants

import android.graphics.Color

class Constants {

    companion object {
        const val DEFAULT_PEN_COLOR = Color.BLACK // default-na boja za olovku
        const val DEFAULT_BOARD_COLOR = Color.WHITE // default-na boja za cetku
        const val DEFAULT_PEN_SIZE_PERCENT = 10f // default-na velicina za olovku i cetku u procentima
        const val SAVED_DRAWINGS_FILE_NAME = "painter_drawings.json" // naziv fajla gde cuvamo json sa podacima za sacuvane crteze
    }

    class Permission {

        object WriteExternalStorage {
            const val REQUEST_CODE = 101
            const val PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

    }

    // maksimalne/minimalne dimenzije za setovanje canvasa
    object CanvasDimensions {
        const val MAX_WIDTH = 4000
        const val MIN_WIDTH = 100
        const val MAX_HEIGHT = 4000
        const val MIN_HEIGHT = 100
    }

}