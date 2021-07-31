package com.example.painter.helpers

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.painter.R
import com.example.painter.constants.Constants

object PermissionManager {


    private var dialog: AlertDialog? = null
    private var listener: RequestPermissionListener? = null
    private var permission = ""
    private var permissionCode = -1
    private var messageExplanation: String = ""
    private var messageDenied: String = ""

    interface RequestPermissionListener {
        fun onRequestPermissionsResultManager(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {}
        fun onPermissionAllowed(permission: String, permissionCode: Int) {}
        fun onPermissionDenied(permission: String, permissionCode: Int) {}
        fun onNeverAskAgainPermissionShowed(permission: String, permissionCode: Int) {}
    }

    fun setPermissionListener(listener: RequestPermissionListener) {
        this.listener = listener
    }

    fun removePermissionListener() {
        this.listener = null
    }

    fun showPermissionExplanationDialog(fragment: Fragment, permission: String, permissionCode: Int, messageExplanation: String, requestPermission: () -> (Unit)) {
        val builder = AlertDialog.Builder(fragment.requireContext()) // , R.style.CustomDialogTheme
        builder.setTitle(fragment.requireContext().resources.getString(R.string.permission_denied))
        builder.setMessage(messageExplanation)
        builder.setPositiveButton(fragment.requireContext().resources.getString(R.string.positive_btn)) { _, _ ->
            //fragment.requestPermissions(arrayOf(permission), permissionCode)

           requestPermission()
        }


        builder.setNegativeButton(fragment.requireContext().resources.getString(R.string.negative_btn)) { _, _ ->
            listener?.onPermissionDenied(permission, permissionCode)
        }

        dialog = builder.show()
    }

    fun showPermissionDeniedDialog(fragment: Fragment, permission: String, permissionCode: Int, messageDenied: String, startForResult: ActivityResultLauncher<Intent>) {
        val builder = AlertDialog.Builder(fragment.requireContext()) // , R.style.CustomDialogTheme
        builder.setTitle(fragment.requireContext().resources.getString(R.string.permission_denied))
        builder.setMessage(messageDenied)
        builder.setPositiveButton(fragment.requireContext().resources.getString(R.string.go_to_settings)) { _, _ ->

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
            intent.data = uri

            startForResult.launch(intent)
        }

        builder.setNegativeButton(fragment.requireContext().getText(R.string.cancel_button)) { _, _ ->
            listener?.onPermissionDenied(permission, permissionCode)
        }

        dialog = builder.show()
    }

    fun onPause() {
        dialog?.dismiss()
        dialog = null
    }



}