package com.video.downloading.app.downloader.online.app.utils

import android.view.View

interface ClickListener {
    fun onClick(view: View?, position: Int)

    fun onLongClick(view: View?, position: Int)
}