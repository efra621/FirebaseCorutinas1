package com.gratum.firebasecorutinas1.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object StoragePermission {

    private const val PERMISSION_CODE = 0
    private const val PERMISION = Manifest.permission.READ_EXTERNAL_STORAGE

    private var isPermissionExplain = false

    fun hasPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            PERMISION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermission(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(PERMISION),
            PERMISSION_CODE
        )
    }

    fun explainPermission(context: Context) {
        val builder = AlertDialog.Builder(context)

        with(builder)
        {
            setTitle("Permisos")
            setMessage("Se necesitan estos permisos para continuar ")
            setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                launchPermissionSettings(context as Activity)
            })
            setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, id ->
                // User cancelled the dialog
                dialog.cancel()
            })
            //setNeutralButton("Maybe", neutralButtonClick)
            show()
        }
    }

    fun shouldShowRequestPermissionRationale(context: Context): Boolean {
        if (!isPermissionExplain) {
            isPermissionExplain = true
            return true
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            PERMISION
        )
    }

    fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}

