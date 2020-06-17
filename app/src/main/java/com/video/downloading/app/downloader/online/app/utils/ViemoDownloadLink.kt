@file:Suppress("DEPRECATION")

package com.video.downloading.app.downloader.online.app.utils

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONObject

class ViemoDownloadLink {
    fun getVideoLink(url: String): String? {
        var videoUrl: String? = null
        try {
            val info = getVimeoVideoInfo(url)
            if (info != null) {
                val mobile =
                    info.getJSONObject("request").getJSONObject("files").getJSONArray("progressive")
                val progressive = mobile.getJSONObject(0) // 前は0);
                videoUrl = progressive.getString("url")
            }
        } catch (e: Exception) {
            videoUrl = null
        }
        return videoUrl
    }

    private fun getVimeoVideoInfo(url: String): JSONObject? {
        var info: JSONObject?
        try {
            val pageContent = getRemoteContent(url)

            //vimeo.clip_page_config = から　window.can_preload　まで
            val divName = "clip_page_config ="
            var start = pageContent!!.indexOf(divName)
            start += divName.length + 1
            var end = pageContent.indexOf("};", start)
            end++
            val config = JSONObject(pageContent.substring(start, end))

            //その中のplayer->config_urlを取得
            val configUrl = config.getJSONObject("player").getString("config_url") ?: return null
            val configResponce = getRemoteContent(configUrl)

            //String infoResponce = getRemoteContent(infoUrl.replace("&amp;","&"));
            info = JSONObject(configResponce!!)
        } catch (e: Exception) {
            info = null
        }
        return info
    }

    private fun getRemoteContent(url: String): String? {
        val content: String?
        content = try {
            val httpClient: HttpClient = DefaultHttpClient()
            val httpGet = HttpGet(url)
            val httpResponse = httpClient.execute(httpGet)
            EntityUtils.toString(httpResponse.entity, "UTF-8")
        } catch (e: Exception) {
            null
        }
        return content
    }
}