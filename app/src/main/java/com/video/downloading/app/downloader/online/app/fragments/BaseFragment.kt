package com.video.downloading.app.downloader.online.app.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants
import com.video.downloading.app.downloader.online.app.utils.DatabaseHelper
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*
import java.io.File

@Suppress("DEPRECATION")
open class BaseFragment : Fragment() {
    var root: View? = null
    private var dialog: AlertDialog? = null
    var databaseHelper: DatabaseHelper? = null
    private val downloadedList = ArrayList<String>()

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    //TODO: show dialog
    fun showDialog(message: String) {
        dialog = setProgressDialog(message)
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
    private fun setProgressDialog(message: String): AlertDialog {

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

    fun startDownload(link: String?, name: String?) {
        try {
            val request =
                DownloadManager.Request(Uri.parse(link))
            request.allowScanningByMediaScanner()
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val file = File(Constants.DOWNLOAD_PATH)
            if (!file.exists()) {
                file.mkdirs()
            }
            val path =
                Uri.withAppendedPath(Uri.fromFile(file), "$name.mp4")
            request.setDestinationUri(path)
            requireActivity()
            val dm = requireActivity()
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val urldownloadFragmentList: ArrayList<String> = vIdeoList()
            if (urldownloadFragmentList.contains(link)) {
                showToast(getString(R.string.video_is_already_downloading))
            } else {
                urldownloadFragmentList.add(link.toString())
                dm.enqueue(request)
                showToast(
                    "Downloading video in the background. Check the " +
                            "notification for progress"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun vIdeoList(): ArrayList<String> {
        return downloadedList
    }

}