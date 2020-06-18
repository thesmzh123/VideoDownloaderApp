package com.video.downloading.app.downloader.online.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.fragments.HomeFragment
import com.video.downloading.app.downloader.online.app.fragments.PasteLinkFragment
import com.video.downloading.app.downloader.online.app.models.DailymotionLink
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import kotlinx.android.synthetic.main.layout_loading_dialog.view.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class DailyMotionDownloadLink(
    stringUrl: String,
    val context: Context,
    private val homeFragment: HomeFragment,
    private val isHome: Boolean,
    private val pasteLinkFragment: PasteLinkFragment
) :
    AsyncTask<Any, Int, Any>() {
    private var dailyMotionDownloadLink: String? = null
    private var dailymotionLink: DailymotionLink? = null
    private var document: Document? = null
    private var jsonObject: JSONObject? = null
    private var downloadUrl: String? = null
    private var element: Elements? = null
    private var requestQueue: RequestQueue? = null
    private var dialog: AlertDialog? = null
    private var rnds: Int = 0


    init {
        this.dailyMotionDownloadLink = stringUrl
        HttpsTrustManager().allowAllSSL()
        requestQueue = Volley.newRequestQueue(context)
        dailymotionLink = DailymotionLink.getInstance()
        rnds = (0..100).random()

    }

    override fun onPreExecute() {
        super.onPreExecute()
        showDialog(context.getString(R.string.generate_download_link))
    }

    override fun doInBackground(vararg params: Any?): Any? {
        try {
            if (dailyMotionDownloadLink!!.contains("https://www.dailymotion.com/video/")) {
                val split: Array<String> =
                    dailyMotionDownloadLink?.split("/".toRegex())!!.toTypedArray()
                val sb = StringBuilder()
                sb.append("https://www.dailymotion.com/embed/video/")
                sb.append(split[4])
                sb.append("?autoplay=1")


                document = Jsoup.connect(sb.toString()).ignoreHttpErrors(true).get()

                element = document!!.getElementsByTag("script")

                try {
                    val split2: Array<String> = element.toString()
                        .split("var config =\\s".toRegex()).toTypedArray()

                    jsonObject = JSONObject(split2[1]).getJSONObject("metadata")
                    val jSONObject =
                        jsonObject!!.getJSONObject("qualities").optJSONArray("auto")!!
                            .getJSONObject(0)
                    val optString = jSONObject.optString("url")

                    Thread(Runnable {
                        try {

                            val getRequest =
                                StringRequest(
                                    Request.Method.GET, optString,
                                    Response.Listener { response ->
                                        // display response
                                        Log.d(TAGI, response)

                                        if (response.contains("dailymotion.com/sec")) {
                                            if (response.contains("PROGRESSIVE-URI")) {
                                                val split3 =
                                                    response!!.split(",".toRegex())
                                                        .toTypedArray()
                                                val wordList =
                                                    listOf(*split3)
                                                var urlBase: String? = null
                                                for (e in wordList) {
                                                    if (e.contains("PROGRESSIVE-URI")) {
                                                        Log.d(TAGI, e)
                                                        urlBase = e
                                                    }
                                                }

                                                if (split3[4].contains("720")) {
                                                    val split4: Array<String> =
                                                        urlBase!!.split("=")
                                                            .toTypedArray()
                                                    downloadUrl =
                                                        split4[1].substring(1)

                                                    dailymotionLink!!.downloadUrl = downloadUrl
                                                } else {
                                                    val split4: Array<String> =
                                                        urlBase!!.split("=")
                                                            .toTypedArray()

                                                    downloadUrl =
                                                        split4[1].substring(1)
                                                    dailymotionLink!!.downloadUrl = downloadUrl
                                                }

                                            }
                                        }
                                    },
                                    Response.ErrorListener { error ->
                                        Log.d(TAGI, "error: " + error.message)

                                    }
                                )
                            requestQueue!!.add(getRequest)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }).start()

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        hideDialog()
        try {
            if (isHome) {
                homeFragment.downloadVideo()
            } else {
                pasteLinkFragment.downloadVideo("Dailymotion_$rnds",dailymotionLink!!.downloadUrl)
                dailymotionLink!!.downloadUrl = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: show dialog
    private fun showDialog(message: String) {
        dialog = setProgressDialog(context, message)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    //TODO: hide dialog
    private fun hideDialog() {
        if (dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun setProgressDialog(context: Context, message: String): AlertDialog {

        val builder = MaterialAlertDialogBuilder(
            context
        )
        builder.setCancelable(false)
        val inflater = (context as AppCompatActivity).layoutInflater
        val view = inflater.inflate(R.layout.layout_loading_dialog, null)
        builder.setView(view)

        view.dialogText.text = message
        return builder.create()
    }
}