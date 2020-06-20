package com.video.downloading.app.downloader.online.app.utils

import android.annotation.SuppressLint
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Suppress("UNUSED_ANONYMOUS_PARAMETER", "PrivatePropertyName")
class HttpsTrustManager : X509TrustManager {
    private var trustManagers: Array<TrustManager>? = null
    private val _AcceptedIssuers =
        arrayOf<X509Certificate>()

    @SuppressLint("TrustAllX509TrustManager")
    @Throws(CertificateException::class)
    override fun checkClientTrusted(
        x509Certificates: Array<X509Certificate?>?, s: String?
    ) {
    }

    @SuppressLint("TrustAllX509TrustManager")
    @Throws(CertificateException::class)
    override fun checkServerTrusted(
        x509Certificates: Array<X509Certificate?>?, s: String?
    ) {
    }

/*
    fun isClientTrusted(chain: Array<X509Certificate?>?): Boolean {
        return true
    }

    fun isServerTrusted(chain: Array<X509Certificate?>?): Boolean {
        return true
    }
*/

    override fun getAcceptedIssuers(): Array<X509Certificate>? {
        return _AcceptedIssuers
    }

    fun allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }
        var context: SSLContext? = null
        if (trustManagers == null) {
            trustManagers = arrayOf(HttpsTrustManager())
        }
        try {
            context = SSLContext.getInstance("TLS")
            context.init(null, trustManagers, SecureRandom())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context?.socketFactory)
    }
}