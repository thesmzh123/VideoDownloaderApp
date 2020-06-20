package com.video.downloading.app.downloader.online.app.actvities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*

open class BaseActivity : AppCompatActivity() {
    private var dialog: AlertDialog? = null
    private lateinit var interstitial: InterstitialAd

    //TODO: show dialog
    fun showDialog(message: String) {
        dialog = setProgressDialog(message)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    //TODO: hide dialog
    fun hideDialog() {
        if (dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun setProgressDialog(message: String): AlertDialog {

        val builder = MaterialAlertDialogBuilder(
            this@BaseActivity
        )
        builder.setCancelable(false)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }

    //TODO: load interstial
    fun loadInterstial() {
        try {
            Log.d(TAGI, "load ads")
            if (!SharedPrefUtils.getBooleanData(this@BaseActivity, "hideAds")) {
                interstitial = InterstitialAd(this)
                interstitial.adUnitId = getString(R.string.interstitial)
                try {
                    if (!interstitial.isLoading && !interstitial.isLoaded) {
                        val adRequest = AdRequest.Builder().build()
                        interstitial.loadAd(adRequest)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.d(TAGI, "error: " + ex.message)
                }

                requestNewInterstitial()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: requestNewInterstitial
    private fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder().build()
        interstitial.loadAd(adRequest)
    }

    //TODO: start activity
    fun startNewActivty(activity: Activity) {
        startActivity(Intent(this@BaseActivity, activity.javaClass))
        finish()
    }

    //TODO: start activity  as ads
    fun startNewActivtyAds(activity: Activity) {
        if (!SharedPrefUtils.getBooleanData(this@BaseActivity, "hideAds")) {

            if (interstitial.isLoaded) {
                if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                        Lifecycle.State.STARTED
                    )
                ) {
                    interstitial.show()
                } else {
                    Log.d(TAGI, "App Is In Background Ad Is Not Going To Show")
                }
            } else {
                startActivity(Intent(this@BaseActivity, activity.javaClass))
                finish()

            }
            interstitial.adListener = object : AdListener() {
                override fun onAdClosed() {
                    requestNewInterstitial()
                    startActivity(Intent(this@BaseActivity, activity.javaClass))
                    finish()

                }
            }
        } else {
            startActivity(Intent(this@BaseActivity, activity.javaClass))
            finish()
        }
    }

}