package com.video.downloading.app.downloader.online.app.actvities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)


    }

    override fun onResume() {
        super.onResume()
        Log.d(TAGI, "on R")
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            startNewActivtySplash(MainActivity())
        }, 3000)
    }

    override fun onBackPressed() {
        this@SplashScreenActivity.finish()

    }
    //TODO: start activity splash

    private fun startNewActivtySplash(activity: Activity) {
        startActivity(Intent(applicationContext, activity.javaClass))
        this@SplashScreenActivity.finish()
    }

}
