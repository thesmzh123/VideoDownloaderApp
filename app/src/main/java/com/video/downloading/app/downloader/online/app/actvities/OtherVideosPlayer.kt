@file:Suppress("DEPRECATION")

package com.video.downloading.app.downloader.online.app.actvities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.AudioEventListener
import com.video.downloading.app.downloader.online.app.utils.PlayerEventListener
import com.video.downloading.app.downloader.online.app.utils.VideoEventListener
import com.video.downloading.app.downloader.online.app.utils.ZoomableExoPlayerView

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class OtherVideosPlayer : Activity(), Player.EventListener {
    private var textureView: TextureView? = null
    private var hlsVideoUri: String? = null
    private lateinit var simpleExoPlayerView: ZoomableExoPlayerView
    private lateinit var player: SimpleExoPlayer
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        try {

            val intent = intent
            hlsVideoUri = intent.getStringExtra("videoUrl")


            // 3. Create the player
            player = ExoPlayerFactory.newSimpleInstance(this)
            player.setVideoTextureView(textureView)

            simpleExoPlayerView = findViewById(R.id.player_view)

            simpleExoPlayerView.player = player
            simpleExoPlayerView.keepScreenOn = true
            // Add Controls listeners..
            val playerEventListener = PlayerEventListener()
            player.addListener(playerEventListener)

            val audioEventListener = AudioEventListener()
            player.setAudioDebugListener(audioEventListener)

            val videoEventListener = VideoEventListener()
            player.setVideoDebugListener(videoEventListener)
            // End
            textureView = simpleExoPlayerView.videoSurfaceView as TextureView

            val defaultBandwidthMeter = DefaultBandwidthMeter()
            val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(
                    this@OtherVideosPlayer,
                    Util.getUserAgent(
                        this@OtherVideosPlayer,
                        "Webcams"
                    ), defaultBandwidthMeter
                )

            val uri = Uri.parse(hlsVideoUri)
//            val mainHandler = Handler()
            /*    val mediaSource: MediaSource = HlsMediaSource(
                    uri,
                    dataSourceFactory, mainHandler, null
                )*/
            val mediaSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(DefaultExtractorsFactory())
                .createMediaSource(uri)
            player.prepare(mediaSource)
            player.addListener(this)
            player.prepare(mediaSource)
            simpleExoPlayerView.requestFocus()
            player.playWhenReady = true

            textureView!!.isEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPlayerStateChanged(
        playWhenReady: Boolean,
        playbackState: Int
    ) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                //You can use progress dialog to show user that video is preparing or buffering so please wait
                progressBar = findViewById(R.id.progressBar)
                progressBar.visibility = View.VISIBLE
//                captutreScreen!!.visibility = View.GONE

            }
            Player.STATE_IDLE -> {
            }
            Player.STATE_READY -> {
                // dismiss your dialog here because our video is ready to play now
                progressBar = findViewById(R.id.progressBar)
                progressBar.visibility = View.GONE
//                captutreScreen!!.visibility = View.VISIBLE
            }
            Player.STATE_ENDED -> {
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        val adb =
            MaterialAlertDialogBuilder(this@OtherVideosPlayer)
        adb.setTitle("Could not able to play video")
        adb.setMessage("It seems that something is going wrong.\nPlease try alternate player by clicking Ok.")
        adb.setCancelable(false)
        adb.setPositiveButton("OK") { dialog, which ->
            val intent = Intent(this@OtherVideosPlayer, AlternateVideoPlayerActivity::class.java)
            intent.putExtra("path", hlsVideoUri)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        val ad = adb.create()
        ad.show()
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
        simpleExoPlayerView.keepScreenOn = false
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayerView.keepScreenOn = false
        player.release()

    }

}