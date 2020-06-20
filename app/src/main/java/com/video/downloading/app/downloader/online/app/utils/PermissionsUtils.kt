@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.video.downloading.app.downloader.online.app.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import java.util.*

@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class PermissionsUtils {
    companion object {
        const val PERMISSION_READ_STORAGE = READ_EXTERNAL_STORAGE
        const val PERMISSION_WRITE_STORAGE = WRITE_EXTERNAL_STORAGE


        const val PERMISSION_REQUEST_CODE = 1

    }


    private var permissions: PermissionsUtils? = null
    private var activity: Activity? = null
    private var requiredPermissions: ArrayList<String>? = null
    private var ungrantedPermissions = ArrayList<String>()

    constructor(activity: Activity?) {
        this.activity = activity
    }

    constructor()


    @Synchronized
    fun getInstance(activity: Activity): PermissionsUtils? {
        if (permissions == null) {
            permissions = PermissionsUtils(activity)
        }
        return this.permissions
    }


    private fun initPermissions() {
        requiredPermissions = ArrayList()
        requiredPermissions!!.add(PERMISSION_READ_STORAGE)
        requiredPermissions!!.add(PERMISSION_WRITE_STORAGE)

        //Add all the required permission in the list
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    fun requestPermissionsIfDenied() {
        ungrantedPermissions = getUnGrantedPermissionsList()
        if (canShowPermissionRationaleDialog()) {
            showMessageOKCancel(activity!!.resources.getString(R.string.permission_message),
                DialogInterface.OnClickListener { dialog, which -> askPermissions() })
            return
        }
        askPermissions()
    }

    /* fun requestPermissionsIfDenied(permission: String) {
         if (canShowPermissionRationaleDialog(permission)) {
             showMessageOKCancel(activity!!.resources.getString(R.string.permission_message),
                 DialogInterface.OnClickListener { dialog, which -> askPermission(permission) })
             return
         }
         askPermission(permission)
     }*/

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    private fun canShowPermissionRationaleDialog(): Boolean {
        var shouldShowRationale = false
        for (permission in ungrantedPermissions) {
            val shouldShow =
                activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        permission
                    )
                }
            if (shouldShow!!) {
                shouldShowRationale = true
            }
        }
        return shouldShowRationale
    }
/*
    private fun canShowPermissionRationaleDialog(permission: String): Boolean {
        var shouldShowRationale = false
        val shouldShow =
            activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) }
        if (shouldShow!!) {
            shouldShowRationale = true
        }
        return shouldShowRationale
    }*/

    private fun askPermissions() {
        if (ungrantedPermissions.size > 0) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    ungrantedPermissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

/*    private fun askPermission(permission: String) {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf<String>(permission),
                PERMISSION_REQUEST_CODE
            )
        }
    }*/

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        MaterialAlertDialogBuilder(activity)
            .setMessage(message)
            .setPositiveButton(R.string.ok, okListener)
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, i ->
                Log.d("test", "ok")
                activity!!.finish()
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }


    fun isAllPermissionAvailable(): Boolean {
        var isAllPermissionAvailable = true
        initPermissions()
        for (permission in requiredPermissions!!) {
            if (activity?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        permission
                    )
                } !== PackageManager.PERMISSION_GRANTED
            ) {
                isAllPermissionAvailable = false
                break
            }
        }
        return isAllPermissionAvailable
    }

    private fun getUnGrantedPermissionsList(): ArrayList<String> {
        val list = ArrayList<String>()
        for (permission in requiredPermissions!!) {
            val result = activity?.let { ActivityCompat.checkSelfPermission(it, permission) }
            if (result != PackageManager.PERMISSION_GRANTED) {
                list.add(permission)
            }
        }
        return list
    }

}