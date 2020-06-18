package com.video.downloading.app.downloader.online.app.actvities

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import com.video.downloading.app.downloader.online.app.R
import java.io.File

class AlternateVideoPlayerActivity : BaseActivity() {
    private var filePath: String? = null
    private var fileName: String? = null

    private var videoView: VideoView? = null
    private var mediaController: MediaController? = null
    private var uri: Uri? = null
    var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_alternate_video_player)
        try {
            filePath = intent.getStringExtra("path")
            videoView = findViewById(R.id.VideoView)
            file = File(filePath!!)
            fileName = file!!.name
            if (file!!.exists()) {
                mediaController = MediaController(this)
                videoView!!.setMediaController(mediaController)
                mediaController!!.setAnchorView(videoView)
                videoView!!.setVideoURI(uri)
                videoView!!.setVideoPath(file!!.absolutePath)
                videoView!!.requestFocus()
                videoView!!.start()
            }
            val file = File(filePath!!)
            uri = Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        finish()
    }
}