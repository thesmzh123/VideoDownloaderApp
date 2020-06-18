package com.video.downloading.app.downloader.online.app.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import com.ashudevs.facebookurlextractor.FacebookExtractor
import com.ashudevs.facebookurlextractor.FacebookFile
import com.find.lost.app.phone.utils.InternetConnection
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.htetznaing.lowcostvideo.LowCostVideo
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import com.video.downloading.app.downloader.online.app.utils.DailyMotionDownloadLink
import com.video.downloading.app.downloader.online.app.utils.ViemoDownloadLink
import kotlinx.android.synthetic.main.fragment_paste_link.view.*

class PasteLinkFragment : BaseFragment() {
    private var urlText: TextInputEditText? = null
    private var xGetter: LowCostVideo? = null
    private var rnds: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_paste_link, container, false)
        urlText = root!!.videoUrl
        rnds = (0..100).random()

        root!!.downloadBtn.setOnClickListener {
            if (urlText!!.text!!.isEmpty()) {
                showToast(getString(R.string.fill_the_field))
            } else if (InternetConnection().checkConnection(requireActivity())) {
                if (!URLUtil.isValidUrl(urlText!!.text.toString())) {
                    showToast(getString(R.string.valid_url))
                } else if (urlText!!.text.toString().contains("https://video.f")) {
                    val handler = Handler()
                    showDialog(getString(R.string.generate_download_link))
                    handler.postDelayed({
                        hideDialog()
                        downloadVideo("Facebook_" + rnds, urlText!!.text.toString())
                    }, 3000)
                } else if (urlText!!.text.toString().contains("https://www.facebook.com/")) {
                    extractFbDownloadLink()
                } else if (urlText!!.text.toString().contains("https://www.dailymotion.com/")) {
                    val homeFragment = HomeFragment()
                    val split: Array<String> =
                        urlText!!.text.toString().split("\\?".toRegex()).toTypedArray()
                    DailyMotionDownloadLink(
                        split[0],
                        requireActivity(),
                        homeFragment,
                        false,
                        this@PasteLinkFragment
                    ).execute()
                } else if (urlText!!.text.toString().contains("https://vimeo.com/")) {
                    showDialog(getString(R.string.generate_download_link))
                    val viemoLink = ViemoDownloadLink().getVideoLink(urlText!!.text.toString())
                    hideDialog()
                    downloadVideo("Vimeo_"+rnds, viemoLink.toString())
                }
            } else {
                showToast(getString(R.string.no_internet))
            }
            urlText!!.text?.clear()
        }
        return root!!
    }

    fun downloadVideo(name: String, url: String) {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        startDownload(url, name)
                    }


                    DialogInterface.BUTTON_NEGATIVE ->
                        //Yes button clicked
                        dialog.dismiss()
                }
            }

        val builder =
            MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle(name).setMessage(getString(R.string.download_this_video))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener).setCancelable(false)
            .show()

    }

    @SuppressLint("StaticFieldLeak")
    private fun extractFbDownloadLink() {
        showDialog(getString(R.string.generate_download_link))
        object : FacebookExtractor() {
            override fun onExtractionComplete(FbFile: FacebookFile) {
                Log.d(
                    TAGI, """
     onExtractionComplete: ${FbFile.quality}
     ${FbFile.url}
     ${FbFile.ext}
     ${FbFile.filename}
     ${FbFile.author}
     ${FbFile.size}
     ${FbFile.duration}
     
     """.trimIndent()
                )
                hideDialog()

                downloadVideo("Facebook-" + rnds, FbFile.url)
                //Complate
            }

            override fun onExtractionFail(Error: String) {
                //Fail
                hideDialog()
                Log.d(TAGI, "fb error: " + Error)
            }
        }.Extractor(activity, urlText!!.text.toString())
    }
}