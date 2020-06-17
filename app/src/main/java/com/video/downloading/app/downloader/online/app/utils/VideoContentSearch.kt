package com.charizardtech.social.media.downloader.app.utils

import android.content.Context
import android.util.Log
import com.video.downloading.app.downloader.online.app.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.util.*

abstract class VideoContentSearch(
    activity: Context?,
    url: String,
    page: String,
    title: String
) : Thread() {
    private val TAG = "Test"
    private var context: Context? = null
    private var url: String? = null
    private var page: String? = null
    private var title: String? = null
    private var numLinksInspected = 0
    private var running = true


    init {
        this.context = activity
        this.url = url
        this.page = page
        this.title = title
        numLinksInspected = 0
    }


    abstract fun onStartInspectingURL()

    abstract fun onFinishedInspectingURL(finishedAll: Boolean)

    abstract fun onVideoFound(
        size: String?, type: String?, link: String?, name: String?,
        page: String?, chunked: Boolean, website: String?
    )

    override fun run() {
//        running = true
            val urlLowerCase = url!!.toLowerCase(Locale.getDefault())
            val filters =
                context!!.resources.getStringArray(R.array.videourl_filters)
            var urlMightBeVideo = false
            for (filter in filters) {
                if (urlLowerCase.contains(filter!!)) {
                    urlMightBeVideo = true
                    break
                }
            }
            if (urlMightBeVideo) {
                numLinksInspected++
                onStartInspectingURL()
                Log.i(TAG, "retreiving headers from $url")
                var uCon: URLConnection? = null
                try {
                    uCon = URL(url).openConnection()
                    uCon.connect()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (uCon != null) {
                    var contentType = uCon.getHeaderField("content-type")
                    if (contentType != null) {
                        contentType = contentType.toLowerCase(Locale.getDefault())
                        if (contentType.contains("video") || contentType.contains("audio")) {
                            addVideoToList(uCon, page!!, title, contentType)
                        } else if (contentType == "application/x-mpegurl" || contentType == "application/vnd.apple.mpegurl") {
                            addVideosToListFromM3U8(uCon, page!!, title, contentType)
                        } else Log.i(
                            TAG, "Not a video. Content type = " +
                                    contentType
                        )
                    } else {
                        Log.i(TAG, "no content type")
                    }
                } else Log.i(TAG, "no connection")
                numLinksInspected--
                var finishedAll = false
                if (numLinksInspected <= 0) {
                    finishedAll = true
                }
                onFinishedInspectingURL(finishedAll)

        }
    }



    open fun addVideoToList(
        uCon: URLConnection,
        page: String,
        title: String?,
        contentType: String
    ) {
        try {
            var size = uCon.getHeaderField("content-length")
            var link = uCon.getHeaderField("Location")
            if (link == null) {
                link = uCon.url.toString()
            }
            val host = URL(page).host
            var website: String? = null
            var chunked = false
            var name = "video"
            if (title != null) {
                name = if (contentType.contains("audio")) {
                    "[AUDIO ONLY]$title"
                } else {
                    title
                }
            } else if (contentType.contains("audio")) {
                name = "audio"
            }
            if (host.contains("youtube.com") || URL(link).host.contains("googlevideo.com")) { //link  = link.replaceAll("(range=)+(.*)+(&)",
// "");
                val r = link.lastIndexOf("&range")
                if (r > 0) {
                    link = link.substring(0, r)
                }
                val ytCon: URLConnection
                ytCon = URL(link).openConnection()
                ytCon.connect()
                size = ytCon.getHeaderField("content-length")
                if (host.contains("youtube.com")) {
                    val embededURL = URL(
                        "http://www.youtube.com/oembed?url=" + page +
                                "&format=json"
                    )
                    try { //name = new JSONObject(IOUtils.toString(embededURL)).getString("title");
                        val jSonString: String
                        val `in` = embededURL.openStream()
                        val inReader = InputStreamReader(
                            `in`, Charset
                                .defaultCharset()
                        )
                        val sb = StringBuilder()
                        val buffer = CharArray(1024)
                        var read: Int
                        while (inReader.read(buffer).also { read = it } != -1) {
                            sb.append(buffer, 0, read)
                        }
                        jSonString = sb.toString()
                        name = JSONObject(jSonString).getString("title")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (contentType.contains("video")) {
                        name = "[VIDEO ONLY]$name"
                    } else if (contentType.contains("audio")) {
                        name = "[AUDIO ONLY]$name"
                    }
                }
            } else if (host.contains("dailymotion.com")) {
                chunked = true
                website = "dailymotion.com"
                link = link.replace("(frag\\()+(\\d+)+(\\))".toRegex(), "FRAGMENT")
                size = null
            } else if (host.contains("vimeo.com") && link.endsWith("m4s")) {
                chunked = true
                website = "vimeo.com"
                link = link.replace("(segment-)+(\\d+)".toRegex(), "SEGMENT")
                size = null
            }
            val type: String
            type = when (contentType) {
                "video/mp4" -> "mp4"
                "video/webm" -> "webm"
                "video/mp2t" -> "ts"
                "audio/webm" -> "webm"
                else -> "mp4"
            }
            onVideoFound(size, type, link, name, page, chunked, website)
            val videoFound = "name:" + name + "\n" +
                    "link:" + link + "\n" +
                    "type:" + contentType + "\n" +
                    "size:" + size
            Log.i(TAG, videoFound)
        } catch (e: IOException) {
            Log.e(
                "Test", "Exception in adding video to " +
                        "list"
            )
        }
    }

    open fun addVideosToListFromM3U8(
        uCon: URLConnection,
        page: String,
        title: String?,
        contentType: String
    ) {
        try {
            val host: String
            host = URL(page).host
            if (host.contains("twitter.com") || host.contains("metacafe.com") || host.contains("myspace.com")) {
                val `in` = uCon.getInputStream()
                val inReader = InputStreamReader(`in`)
                val buffReader = BufferedReader(inReader)
                var line: String
                var prefix: String? = null
                var type: String? = null
                var name = "video"
                var website: String? = null
                if (title != null) {
                    name = title
                }
                if (host.contains("twitter.com")) {
                    prefix = "https://video.twimg.com"
                    type = "ts"
                    website = "twitter.com"
                } else if (host.contains("metacafe.com")) {
                    val link = uCon.url.toString()
                    prefix = link.substring(0, link.lastIndexOf("/") + 1)
                    website = "metacafe.com"
                    type = "mp4"
                } else if (host.contains("myspace.com")) {
                    val link = uCon.url.toString()
                    website = "myspace.com"
                    type = "ts"
                    onVideoFound(null, type, link, name, page, true, website)
                    val videoFound = "name:" + name + "\n" +
                            "link:" + link + "\n" +
                            "type:" + contentType + "\n" +
                            "size: null"
                    Log.i(TAG, videoFound)
                    return
                }
                while (buffReader.readLine().also { line = it } != null) {
                    if (line.endsWith(".m3u8")) {
                        val link = prefix + line
                        onVideoFound(null, type, link, name, page, true, website)
                        val videoFound = "name:" + name + "\n" +
                                "link:" + link + "\n" +
                                "type:" + contentType + "\n" +
                                "size: null"
                        Log.i(TAG, videoFound)
                    }
                }
            } else {
                Log.i(
                    "Test", "Content type is " + contentType + " but site is not " +
                            "supported"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
