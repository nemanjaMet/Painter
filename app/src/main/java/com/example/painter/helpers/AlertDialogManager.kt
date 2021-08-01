package com.example.painter.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.InputType
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.painter.R

/**
 *  Pomocna klasa za pozivanje alert dialoga
 */
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

            // Set up the input
            val input = EditText(context)

            input.hint = hint
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton(context.getString(R.string.positive_btn)) { _, _ ->
                val name = input.text.toString()

                listener?.onAlertDialogConfirm(name, requestCode)
            }
            builder.setNegativeButton(context.getString(R.string.cancel_btn)) { dialog, _ -> dialog.cancel() }

            builder.show()
        }

        fun showPermissionExplanationDialog(fragment: Fragment, messageExplanation: String, requestPermission: () -> (Unit)) {
            val builder = android.app.AlertDialog.Builder(fragment.requireContext()) // , R.style.CustomDialogTheme
            builder.setTitle(fragment.requireContext().resources.getString(R.string.permission_denied))
            builder.setMessage(messageExplanation)
            builder.setPositiveButton(fragment.requireContext().resources.getString(R.string.positive_btn)) { _, _ ->
                requestPermission()
            }

            builder.setNegativeButton(fragment.requireContext().resources.getString(R.string.negative_btn)) { _, _ ->

            }

            builder.show()
        }

        fun showPermissionDeniedDialog(fragment: Fragment, messageDenied: String, startForResult: ActivityResultLauncher<Intent>) {
            val builder = android.app.AlertDialog.Builder(fragment.requireContext()) // , R.style.CustomDialogTheme
            builder.setTitle(fragment.requireContext().resources.getString(R.string.permission_denied))
            builder.setMessage(messageDenied)
            builder.setPositiveButton(fragment.requireContext().resources.getString(R.string.go_to_settings)) { _, _ ->

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
                intent.data = uri

                startForResult.launch(intent)
            }

            builder.setNegativeButton(fragment.requireContext().getText(R.string.cancel_button)) { _, _ ->

            }

            builder.show()
        }

    }

}