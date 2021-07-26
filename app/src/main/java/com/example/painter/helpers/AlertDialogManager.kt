package com.example.painter.helpers

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.painter.R

class AlertDialogManager {

    companion object {

        const val GET_SAVED_IMAGE_NAME = 1

        interface AlertDialogListener {
            fun onAlertDialogConfirm(value: String, requestCode: Int)
        }

        /**
         * @param title title za dialog
         * @param hint hint koji se pojavljuje u input polju
         * @param listener interface preko kog dobijamo rezultat iz input polja
         * @param requestCode code koji saljemo da identifikujemo alertdialog (ukoliko nam je potrebno)
         */
        fun openAlertDialogWithInputField(context: Context, title: String, hint: String, listener: AlertDialogListener? = null, requestCode: Int = -1) {

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(title)

            //builder.setMessage(hint)

            // Set up the input
            val input = EditText(context)

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.hint = hint
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton(context.getString(R.string.positive_btn)) { _, _ ->

                // Here you get get input text from the Edit text
                val name = input.text.toString()

                listener?.onAlertDialogConfirm(name, requestCode)
            }
            builder.setNegativeButton(context.getString(R.string.cancel_btn)) { dialog, _ -> dialog.cancel() }

            builder.show()
        }

    }

}