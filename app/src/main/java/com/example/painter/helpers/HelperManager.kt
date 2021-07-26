package com.example.painter.helpers

import java.text.SimpleDateFormat
import java.util.*

class HelperManager {

    companion object {

        /**
         *  Funkcija preko koje dobijamo datum kao string
         */
        fun getCurrentDateAsString(): String {
            val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return dateTimeFormat.format(Date())
        }

    }

}