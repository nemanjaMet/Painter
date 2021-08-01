package com.example.painter.constants

/**
 *  Mode za progress. Da znamo da li je u toku neki progres ili da li je zavrsen
  */
enum class ProgressMode(var value: String) {
    NONE(""),
    IN_PROGRESS(""),
    FINISHED("")
}