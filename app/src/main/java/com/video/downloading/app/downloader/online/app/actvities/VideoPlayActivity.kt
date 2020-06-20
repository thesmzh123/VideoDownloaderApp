package com.video.downloading.app.downloader.online.app.actvities

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import com.video.downloading.app.downloader.online.app.utils.VideoControllerView

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER", "DEPRECATION")
class VideoPlayActivity : BaseActivity(), SurfaceHolder.Callback,
    OnPreparedListener, VideoControllerView.MediaPlayerControl {
    private var videoSurface: SurfaceView? = null
    private var controller: VideoControllerView? = null
    private var filepath: String? = null
    private var fromStreaming: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        videoSurface = findViewById(R.id.videoSurface)

        val videoHolder = videoSurface!!.holder
        videoHolder.addCallback(this)
        val i = intent
        val extras = i.extras
        filepath = extras!!.getString("videofilename")
        fromStreaming = extras.getString("fromStreaming")
        player = MediaPlayer()
        controller = VideoControllerView(this)
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
        try {
            if (fromStreaming == null) {
                player!!.setDataSource(filepath)
            } else {
                val video = Uri.parse(filepath)
                player!!.setDataSource(applicationContext, video)
            }
            showDialog("Loading..")
            player!!.setOnPreparedListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        controller!!.setMediaPlayer(this)
        val videoWidth = player!!.videoWidth
        val videoHeight = player!!.videoHeight
        val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
        val screenWidth = windowManager.defaultDisplay.width
        val screenHeight = windowManager.defaultDisplay.height
        val screenProportion =
            screenWidth.toFloat() / screenHeight.toFloat()
        val lp = videoSurface!!.layoutParams
        if (videoProportion > screenProportion) {
            lp.width = screenWidth
            lp.height = (screenWidth.toFloat() / videoProportion).toInt()
        } else {
            lp.width = (videoProportion * screenHeight.toFloat()).toInt()
            lp.height = screenHeight
        }
        videoSurface!!.layoutParams = lp
        controller!!.setAnchorView(findViewById<View>(R.id.videoSurfaceContainer) as FrameLayout)
        hideDialog()
        player!!.start()
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        Log.d(TAGI, "surfaceCreated: ")
        player!!.setDisplay(surfaceHolder)
        player!!.prepareAsync()
    }

    override fun surfaceChanged(
        surfaceHolder: SurfaceHolder,
        i: Int,
        i1: Int,
        i2: Int
    ) {
        Log.d(TAGI, "surfaceChanged: ")
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        Log.d(TAGI, "surfaceDestroyed: ")
    }

    override fun start() {
        Log.d(TAGI, "start: ")
        player!!.start()
    }

    override fun pause() {
        Log.d(TAGI, "pause: ")
        player!!.pause()
    }

    override fun getDuration(): Int {
        return if (player != null) {
            Log.d(TAGI, "getDuration: ")
            player!!.duration
        } else {
            Log.d(TAGI, "getDuration: 0")
            0
        }
    }

    override fun getCurrentPosition(): Int {
        return if (player != null) {
            Log.d(TAGI, "getCurrentPosition: ")
            player!!.currentPosition
        } else {
            Log.d(TAGI, "getCurrentPosition: 0")
            0
        }
    }

    override fun seekTo(pos: Int) {
        Log.d(TAGI, "seekTo: $pos")
        player!!.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return if (player != null) {
            // progressDialog.dismiss();
            Log.d(TAGI, "isPlaying: 1 ")
            player!!.isPlaying
        } else {
            //    progressDialog.dismiss();
            Log.d(TAGI, "isPlaying: 2")
            false
        }
    }

    override fun getBufferPercentage(): Int {
//        progressDialog.show();
        Log.d(TAGI, "getBufferPercentage: ")
        return 0
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        Log.d(TAGI, "canSeekBackward: ")
        return true
    }

    override fun canSeekForward(): Boolean {
        Log.d(TAGI, "canSeekForward: ")
        return true
    }

    override fun stop() {
        Log.d(TAGI, "stop: ")
        player!!.stop()
        player!!.release()
    }

    override fun isFullScreen(): Boolean {
        return if (mFullScreen) {
            Log.d(TAGI, "--set icon full screen--")
            false
        } else {
            Log.d(TAGI, "--set icon small full screen--")
            true
        }
    }

    fun setFullScreen(fullScreen: Boolean) {
        var fullScreen1 = fullScreen
        fullScreen1 = false
        if (mFullScreen) {
            Log.d(
                TAGI,
                "-----------Set full screen SCREEN_ORIENTATION_LANDSCAPE------------"
            )
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val displaymetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displaymetrics)
            val height = displaymetrics.heightPixels
            val width = displaymetrics.widthPixels
            val params =
                videoSurface!!.layoutParams as FrameLayout.LayoutParams
            params.width = width
            params.height = height
            params.setMargins(0, 0, 0, 0)
            //set icon is full screen
            mFullScreen = fullScreen1
        } else {
            Log.d(
                TAGI,
                "-----------Set small screen SCREEN_ORIENTATION_PORTRAIT------------"
            )
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val displaymetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displaymetrics)
            val mFrame = findViewById<FrameLayout>(R.id.videoSurfaceContainer)
            // int height = displaymetrics.heightPixels;
            val height = mFrame.height //get height Frame Container video
            val width = displaymetrics.widthPixels
            val params =
                videoSurface!!.layoutParams as FrameLayout.LayoutParams
            params.width = width
            params.height = height
            params.setMargins(0, 0, 0, 0)
            //set icon is small screen
            mFullScreen = true
        }
    }

    override fun toggleFullScreen() {
        Log.d(TAGI, "-----------------click toggleFullScreen-----------")
        isFullScreen = isFullScreen
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        controller!!.show()
        Log.d(TAGI, "onTouchEvent: ")
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //Back key pressed
            //Things to Do
            Log.d(TAGI, "onKeyDown: ")
            if (player != null) {
                player!!.stop()
                player = null
            }
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        var player: MediaPlayer? = null
        private var mFullScreen = true
    }
}