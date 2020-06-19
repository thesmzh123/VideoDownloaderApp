package com.video.downloading.app.downloader.online.app.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants
import com.video.downloading.app.downloader.online.app.utils.Constants.DOWNLOAD_PATH
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import com.video.downloading.app.downloader.online.app.utils.DatabaseHelper
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*
import kotlinx.android.synthetic.main.twitter_guide_layout.view.*
import java.io.File

@Suppress("DEPRECATION")
open class BaseFragment : Fragment() {
    var root: View? = null
    private var dialog: AlertDialog? = null
    var databaseHelper: DatabaseHelper? = null
    private val downloadedList = ArrayList<String>()
    lateinit var interstitial: InterstitialAd

    //TODO: banner
    fun adView(adView: AdView) {
//        adView.visibility = View.GONE
        try {
            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.adListener = object : AdListener() {

                    override fun onAdLoaded() {
                        adView.visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(error: Int) {
                        adView.visibility = View.GONE
                    }

                }
            } else {
                adView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: load interstial
    fun loadInterstial() {
        try {

            Log.d(TAGI, "load ads")
            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                interstitial = InterstitialAd(requireActivity())
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
    fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder().build()
        interstitial.loadAd(adRequest)
    }

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

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
            requireActivity()
        )
        builder.setCancelable(false)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }

    fun startDownload(link: String?, name: String?) {
        if (Build.VERSION.SDK_INT >= 29) {
            downloadVideoInQ(link, name)
        } else {
            downloadVideoInOther(link, name)
        }
    }

    private fun downloadVideoInOther(link: String?, name: String?) {
        try {
            val request =
                DownloadManager.Request(Uri.parse(link))
            request.allowScanningByMediaScanner()
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val file = File(DOWNLOAD_PATH)
            if (!file.exists()) {
                file.mkdirs()
            }
            val path =
                Uri.withAppendedPath(Uri.fromFile(file), "$name.mp4")
//            if (Build.VERSION.SDK_INT >= 29) {
//                request.setDestinationInExternalPublicDir(DOWNLOAD_PATH,"$name.mp4")
//                request.setDestinationUri(Uri.parse("file://" + file.absoluteFile + "/" + "$name.mp4"));
            /* } else {

             }*/
            request.setDestinationUri(path)
            requireActivity()
            val dm = requireActivity()
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val urldownloadFragmentList: ArrayList<String> = vIdeoList()
            if (urldownloadFragmentList.contains(link)) {
                showToast(getString(R.string.video_is_already_downloading))
            } else {
                urldownloadFragmentList.add(link.toString())
                dm.enqueue(request)
                showToast(
                    "Downloading video in the background. Check the " +
                            "notification for progress"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadVideoInQ(link: String?, name: String?) {
        try {
            val direct = File(requireActivity().getExternalFilesDir(null), "/VideoDownloaderApp1")

            if (!direct.exists()) {
                direct.mkdirs()
            }

            val mgr =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadUri = Uri.parse(link)
            val request = DownloadManager.Request(
                downloadUri
            )

            request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or
                        DownloadManager.Request.NETWORK_MOBILE
            )
                .setAllowedOverRoaming(false).setTitle(name) //Download Manager Title
                .setDescription("Downloading...")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/VideoDownloaderApp1/$name.mp4"  //Your User define(Non Standard Directory name)/File Name
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            showToast(
                "Downloading video in the background. Check the " +
                        "notification for progress"
            )
            mgr.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun vIdeoList(): ArrayList<String> {
        return downloadedList
    }

    fun guideDialog(isTwitter: Boolean) {
        val factory = LayoutInflater.from(requireActivity())
        @SuppressLint("InflateParams") val deleteDialogView: View =
            factory.inflate(R.layout.twitter_guide_layout, null)
        val deleteDialog: AlertDialog = MaterialAlertDialogBuilder(requireActivity()).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)
        if (isTwitter) {
            deleteDialogView.gif1.visibility = View.VISIBLE
        }
        deleteDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.ok)
        ) { dialog, which -> // here you can add functions
            if (isTwitter) {
                SharedPrefUtils.saveData(requireActivity(), "isTwitter", true)
            } else {
                SharedPrefUtils.saveData(requireActivity(), "isFacebook", true)
            }
        }

        deleteDialog.show()
    }
}