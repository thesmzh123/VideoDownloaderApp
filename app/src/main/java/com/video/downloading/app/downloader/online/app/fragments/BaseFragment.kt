package com.video.downloading.app.downloader.online.app.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.DatabaseHelper
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*

open class BaseFragment : Fragment() {
    var root: View? = null
    private var dialog: AlertDialog? = null
    var databaseHelper: DatabaseHelper? = null

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    //TODO: show dialog
    fun showDialog(message: String) {
        dialog = setProgressDialog(requireActivity(), message)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    //TODO: hide dialog
    fun hideDialog() {
        if (dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun setProgressDialog(context: Context, message: String): AlertDialog {

        val builder = MaterialAlertDialogBuilder(
            requireActivity()
        )
        builder.setCancelable(false)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }

}