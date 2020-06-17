package com.video.downloading.app.downloader.online.app.models

data class VideoDownload(
    val size: String, val type: String, val link: String,
    val name: String, val page: String, val chunked: Boolean, val website: String
)