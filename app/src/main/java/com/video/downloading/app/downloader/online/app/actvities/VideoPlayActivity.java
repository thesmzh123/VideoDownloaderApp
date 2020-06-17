package com.video.downloading.app.downloader.online.app.actvities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;


import com.video.downloading.app.downloader.online.app.R;
import com.video.downloading.app.downloader.online.app.utils.VideoControllerView;

import java.util.Objects;

import static com.video.downloading.app.downloader.online.app.utils.Constants.TAGI;

public class VideoPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {
    static MediaPlayer player;
    private static boolean mFullScreen = true;
    SurfaceView videoSurface;
    VideoControllerView controller;
    String filepath;
    String fromStreaming;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        videoSurface = findViewById(R.id.videoSurface);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        filepath = Objects.requireNonNull(extras).getString("videofilename");
        fromStreaming = extras.getString("fromStreaming");
        player = new MediaPlayer();
        controller = new VideoControllerView(this);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        try {
            if (fromStreaming == null) {
                player.setDataSource(filepath);
            } else {
                Uri video = Uri.parse(filepath);
                player.setDataSource(getApplicationContext(),video);
            }
            progressDialog.show();
            player.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        controller.setMediaPlayer(this);
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoSurface.setLayoutParams(lp);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        progressDialog.dismiss();
        player.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAGI,"surfaceCreated: ");
        player.setDisplay(surfaceHolder);
        player.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,int i,int i1,int i2) {
        Log.d(TAGI,"surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAGI,"surfaceDestroyed: ");
    }

    @Override
    public void start() {
        Log.d(TAGI,"start: ");
        player.start();
    }

    @Override
    public void pause() {
        Log.d(TAGI,"pause: ");
        player.pause();
    }

    @Override
    public int getDuration() {
        if (player != null) {
            Log.d(TAGI,"getDuration: ");
            return player.getDuration();
        } else {
            Log.d(TAGI,"getDuration: 0");
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (player != null) {
            Log.d(TAGI,"getCurrentPosition: ");
            return player.getCurrentPosition();
        } else {
            Log.d(TAGI,"getCurrentPosition: 0");
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        Log.d(TAGI,"seekTo: " + pos);
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        if (player != null) {
           // progressDialog.dismiss();
            Log.d(TAGI,"isPlaying: 1 ");
            return player.isPlaying();
        } else {
        //    progressDialog.dismiss();
            Log.d(TAGI,"isPlaying: 2");
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
//        progressDialog.show();
        Log.d(TAGI,"getBufferPercentage: ");
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        Log.d(TAGI,"canSeekBackward: ");
        return true;
    }

    @Override
    public boolean canSeekForward() {
        Log.d(TAGI,"canSeekForward: ");
        return true;
    }

    @Override
    public void stop() {
        Log.d(TAGI,"stop: ");
        player.stop();
        player.release();
    }

    @Override
    public boolean isFullScreen() {
        if (mFullScreen) {
            Log.d(TAGI,"--set icon full screen--");
            return false;
        } else {
            Log.d(TAGI,"--set icon small full screen--");
            return true;
        }
    }

    public void setFullScreen(boolean fullScreen) {
        fullScreen = false;

        if (mFullScreen) {
            Log.d(TAGI,"-----------Set full screen SCREEN_ORIENTATION_LANDSCAPE------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = width;
            params.height = height;
            params.setMargins(0,0,0,0);
            //set icon is full screen
            mFullScreen = fullScreen;
        } else {
            Log.d(TAGI,"-----------Set small screen SCREEN_ORIENTATION_PORTRAIT------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final FrameLayout mFrame = findViewById(R.id.videoSurfaceContainer);
            // int height = displaymetrics.heightPixels;
            int height = mFrame.getHeight();//get height Frame Container video
            int width = displaymetrics.widthPixels;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = width;
            params.height = height;
            params.setMargins(0,0,0,0);
            //set icon is small screen
            mFullScreen = true;
        }
    }

    @Override
    public void toggleFullScreen() {
        Log.d(TAGI,"-----------------click toggleFullScreen-----------");
        setFullScreen(isFullScreen());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        Log.d(TAGI,"onTouchEvent: ");
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            //Things to Do
            Log.d(TAGI,"onKeyDown: ");
            if (player != null) {
                player.stop();
                player = null;
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
