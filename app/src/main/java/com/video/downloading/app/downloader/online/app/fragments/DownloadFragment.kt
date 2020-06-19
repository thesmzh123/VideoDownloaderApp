package com.video.downloading.app.downloader.online.app.fragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.adapters.DownloadFileAdapter
import com.video.downloading.app.downloader.online.app.models.DownloadFile
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import com.video.downloading.app.downloader.online.app.utils.WebFace.DOWNLOAD_DIRECTORY
import kotlinx.android.synthetic.main.fragment_bookmark.view.*
import java.io.File

@Suppress("DEPRECATION")
class DownloadFragment : BaseFragment() {
    private var downloadFileList: ArrayList<DownloadFile>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_download, container, false)
        downloadFileList = ArrayList()
        try {
            val root1 = Environment.getExternalStorageDirectory()
            val path = root1.absolutePath + "/" + DOWNLOAD_DIRECTORY + "/"
            Log.d(TAGI, "Path: $path")
            val directory = File(path)
            val files = directory.listFiles()!!
            Log.d(TAGI, "Size: " + files.size)

            for (file in files) {
                Log.d(TAGI, "FileName:" + file.name)
                val fileName = file.name
                val recordingUri =
                    root1.absolutePath + "/" + DOWNLOAD_DIRECTORY + "/" + fileName
                downloadFileList!!.add(DownloadFile(recordingUri, fileName))
            }
            //creating our adapter
            val adapter = DownloadFileAdapter(requireActivity(), downloadFileList!!,this@DownloadFragment)

            //now adding the adapter to recyclerview
            root!!.recyclerView.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
        checkEmptyState()
        return root!!
    }

     fun checkEmptyState() {
        if (downloadFileList!!.isEmpty()) {
            root!!.recyclerView.visibility = View.GONE
            root!!.emptyView.visibility = View.VISIBLE
        } else {
            root!!.recyclerView.visibility = View.VISIBLE
            root!!.emptyView.visibility = View.GONE
        }
    }
}