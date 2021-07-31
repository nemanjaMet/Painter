package com.example.painter.helpers

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.painter.constants.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileManager {

    companion object {

        private const val TAG = "FileManager"

        fun getFileSavedImageList(context: Context): File {
            return File(context.filesDir, (Constants.SAVED_DRAWINGS_FILE_NAME))
        }

        fun loadSavedImagesList(file: File): String {

            var json = ""

            try {
                //val file = File(context.filesDir, (Constants.SAVED_DRAWINGS_FILE_NAME))

                if (file.exists()) {
                    json = String(file.readBytes(), Charsets.UTF_8)
                }

            } catch (ex: Exception) {
                Log.e(TAG, ex.message, ex)
            }

            return json

        }

        fun saveImageList(file: File, json: String): Boolean {

            try {
                //val file = File(context.filesDir, (Constants.SAVED_DRAWINGS_FILE_NAME))

                if (!file.exists()) {
                    file.createNewFile()
                }

                val fos = FileOutputStream(file)
                fos.write(json.toByteArray())
                fos.close()

                return true
            } catch (ex: Exception) {
                Log.e(TAG, ex.message, ex)
                return false
            }

        }

        fun deleteFile(context: Context, paths: List<String?>) {
            if (paths.isNotEmpty()) {
                paths.forEach { path ->
                    try {

                        path?.let {
                            val file = File(path)

                            file.delete()
                            if (file.exists()) {
                                try {
                                    file.canonicalFile.delete()
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                                if (file.exists()) {
                                    context.deleteFile(file.name)
                                }
                            }
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }
            }
        }

        fun exportImage(context: Context, bitmap: Bitmap): String {

            try {
                val albumName = ""

                val filename = "Painter_${System.currentTimeMillis()}.png"
                val write: (OutputStream) -> Boolean = {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                // NEW API
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$albumName")
                    }

                    var fileUri = ""

                    context.contentResolver.let {
                        it.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                            it.openOutputStream(uri)?.let(write)
                            // prikazujemo sliku u galeriji
                            it.update(uri, contentValues, null, null)
                            fileUri = uri.toString()
                        }

                    }

                    return fileUri
                } else { // OLD API
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + albumName

                    val file = File(imagesDir)
                    if (!file.exists()) {
                        file.mkdir()
                    }

                    val image = File(imagesDir, filename)
                    write(FileOutputStream(image))

                    // prikazivanje slike u galeriji
                    MediaScannerConnection.scanFile(context, arrayOf(image.toString()), arrayOf(image.name), null)

                    return Uri.fromFile(file).toString()
                }
            } catch (ex: Exception) {
                Log.e(TAG, ex.message, ex)
            }

            return ""
        }

        fun saveThumbnail(context: Context, bitmap: Bitmap): String {

            try {
                val albumName = "thumbnails"

                val filename = "${System.currentTimeMillis()}.png"
                val write: (OutputStream) -> Boolean = {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                val imagesDir = context.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE).toString() + File.separator + albumName

                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdir()
                }

                val image = File(imagesDir, filename)
                write(FileOutputStream(image))

                return image.path
            } catch (ex: Exception) {
                Log.e(TAG, ex.message, ex)
            }

           return ""

        }


    }

}