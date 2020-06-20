package com.video.downloading.app.downloader.online.app.actvities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        val animation = AnimationUtils.loadAnimation(this, R.anim.transition)
        val animationText = AnimationUtils.loadAnimation(this, R.anim.transition_text)
        // assigning animations to the widgets
        splashImage.startAnimation(animation)
        splashText.startAnimation(animation)
        splashText2.startAnimation(animationText)
        loadInterstial()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAGI, "on R")
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            if (!SharedPrefUtils.getBooleanData(this, "isFirst")) {
                startNewActivty(MainActivity())
            } else {
                startNewActivtyAds(MainActivity())
            }
        }, 3000)
    }

    override fun onBackPressed() {
        finish()

    }


}
