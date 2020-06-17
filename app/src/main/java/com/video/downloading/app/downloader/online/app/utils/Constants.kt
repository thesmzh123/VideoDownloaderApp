package com.video.downloading.app.downloader.online.app.utils

import android.os.Environment
import java.io.File

object Constants {
    const val TAGI = "Test"
    val DOWNLOAD_PATH = Environment.getExternalStorageDirectory()
        .toString() + File.separator + "VideoDownloaderApp1"

}