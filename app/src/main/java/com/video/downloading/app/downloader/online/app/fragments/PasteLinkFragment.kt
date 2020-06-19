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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ashudevs.facebookurlextractor.FacebookExtractor
import com.ashudevs.facebookurlextractor.FacebookFile
import com.find.lost.app.phone.utils.InternetConnection
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.google.android.gms.ads.AdListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.htetznaing.lowcostvideo.LowCostVideo
import com.htetznaing.lowcostvideo.Model.XModel
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.INSTA_LINK
import com.video.downloading.app.downloader.online.app.utils.Constants.QUERY
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import com.video.downloading.app.downloader.online.app.utils.DailyMotionDownloadLink
import com.video.downloading.app.downloader.online.app.utils.ViemoDownloadLink
import kotlinx.android.synthetic.main.banner.view.*
import kotlinx.android.synthetic.main.fragment_paste_link.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PasteLinkFragment : BaseFragment() {
    private var urlText: TextInputEditText? = null
    private var xGetter: LowCostVideo? = null
    private var rnds: String? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_paste_link, container, false)
        urlText = root!!.videoUrl
//        rnds = (0..100).random()
        val sdf = SimpleDateFormat("dd_M_yyyy hh_mm_ss")
        val currentDate = sdf.format(Date())
        rnds = "Dated_$currentDate"

        root!!.downloadBtn.setOnClickListener {
            if (urlText!!.text!!.isEmpty()) {
                showToast(getString(R.string.fill_the_field))
            } else if (InternetConnection().checkConnection(requireActivity())) {
                if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                    if (interstitial.isLoaded) {
                        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                            interstitial.show()
                        } else {
                            Log.d(TAGI, "App Is In Background Ad Is Not Going To Show")

                        }
                    } else {
                        fetchDownloadLink()
                    }
                    interstitial.adListener = object : AdListener() {
                        override fun onAdClosed() {
                            requestNewInterstitial()
                            fetchDownloadLink()
                        }
                    }
                } else {
                    fetchDownloadLink()
                }

            } else {
                showToast(getString(R.string.no_internet))
            }
            urlText!!.text?.clear()
        }

        xGetter = LowCostVideo(requireActivity())
        xGetter!!.onFinish(object : LowCostVideo.OnTaskCompleted {
            override fun onTaskCompleted(
                vidURL: ArrayList<XModel>,
                multiple_quality: Boolean
            ) {
                hideDialog()
                if (multiple_quality) {

                    downloadVideo(rnds.toString(), vidURL[0].url)
                }
            }

            override fun onError() {
                //Error
                hideDialog()
            }
        })

        loadInterstial()
        adView(root!!.adView)
        return root!!
    }

    private fun fetchDownloadLink() {
        if (!URLUtil.isValidUrl(urlText!!.text.toString())) {
            showToast(getString(R.string.valid_url))
        } else if (urlText!!.text.toString().contains("https://video.f")) {
            val handler = Handler()
            showDialog(getString(R.string.generate_download_link))
            handler.postDelayed({
                hideDialog()
                downloadVideo("Facebook_$rnds", urlText!!.text.toString())
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
            downloadVideo("Vimeo_$rnds", viemoLink.toString())
        } else if (urlText!!.text.toString().contains("https://twitter.com")) {
            if (urlText!!.text.toString().contains("\\?")) {
                val split: Array<String> =
                    urlText!!.text.toString().split("\\?".toRegex()).toTypedArray()
                showDialog(getString(R.string.generate_download_link))
                xGetter!!.find(split[0])
            } else {
                showDialog(getString(R.string.generate_download_link))
                xGetter!!.find(urlText!!.text.toString())
            }

        } else if (urlText!!.text.toString().contains("https://www.instagram.com")) {
            instagramLink()
        } else {
            showDialog(getString(R.string.generate_download_link))
            xGetter!!.find(urlText!!.text.toString())
        }
    }

    private fun instagramLink() {
        showDialog(getString(R.string.generate_download_link))
        val splitter: Array<String> =
            urlText!!.text.toString().split("/".toRegex()).toTypedArray()
        val pathvideo = INSTA_LINK + splitter[4] + "/" + QUERY
        val requestQueue =
            Volley.newRequestQueue(requireActivity())
        val stringRequest =
            StringRequest(
                Request.Method.GET, pathvideo,
                Response.Listener { response: String? ->
                    Log.d(TAGI, response!!)
                    try {
                        val jsonObject2 = JSONObject(response)
                        Log.d(
                            TAGI,
                            "onResponse: " + jsonObject2.getString("graphql")
                        )
                        val respone1 = jsonObject2.getString("graphql")
                        val `object` = JSONObject(respone1)
                        parseJson(`object`)
                        hideDialog()
                    } catch (e: Exception) {
                        hideDialog()
                        e.printStackTrace()
                        Log.d(TAGI, "onResponse: " + e.message)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideDialog()
                    error.printStackTrace()
                    Log.d(TAGI, "onErrorResponse: " + error.message)
                }
            )


        val socketTimeout = 10000
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = policy
        requestQueue.add(stringRequest)
    }

    @Throws(JSONException::class)
    private fun parseJson(data: JSONObject?) {
        try {
            if (data != null) {
                val it = data.keys()
                while (it.hasNext()) {
                    val key = it.next()
                    try {
                        when {
                            data[key] is JSONArray -> {
                                val arry = data.getJSONArray(key)
                                val size = arry.length()
                                for (i in 0 until size) {
                                    parseJson(arry.getJSONObject(i))
                                }
                            }
                            data[key] is JSONObject -> {
                                parseJson(data.getJSONObject(key))
                            }
                            else -> {
                                println("" + key + " : " + data.optString(key))


                            }
                        }
                    } catch (e: Throwable) {
                        println("" + key + " : " + data.optString(key))
                        e.printStackTrace()
                    }
                }
                Log.d(TAGI, "parseJson: " + data.getString("video_url"))
                downloadVideo("Instagram_$rnds", data.getString("video_url"))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun downloadVideo(name: String, url: String) {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {

                        if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                            hideDialog()
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
                                startDownload(url, name)
                            }
                            interstitial.adListener = object : AdListener() {
                                override fun onAdClosed() {
                                    requestNewInterstitial()
                                    startDownload(url, name)
                                }
                            }
                        } else {
                            startDownload(url, name)

                        }

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

                downloadVideo("Facebook-$rnds", FbFile.url)
            }

            override fun onExtractionFail(Error: String) {
                //Fail
                hideDialog()
                Log.d(TAGI, "fb error: $Error")
            }
        }.Extractor(activity, urlText!!.text.toString())
    }
}