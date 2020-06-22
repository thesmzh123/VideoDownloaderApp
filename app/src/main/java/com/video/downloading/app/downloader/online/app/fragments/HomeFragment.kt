package com.video.downloading.app.downloader.online.app.fragments

import android.annotation.SuppressLint
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.charizardtech.social.media.downloader.app.utils.VideoContentSearch
import com.find.lost.app.phone.utils.InternetConnection
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.video.downloading.app.downloader.online.app.utils.PermissionsUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.htetznaing.lowcostvideo.LowCostVideo
import com.htetznaing.lowcostvideo.LowCostVideo.OnTaskCompleted
import com.htetznaing.lowcostvideo.Model.XModel
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.actvities.VideoPlayActivity
import com.video.downloading.app.downloader.online.app.adapters.WebsiteAdapter
import com.video.downloading.app.downloader.online.app.models.DailymotionLink
import com.video.downloading.app.downloader.online.app.models.VideoDownload
import com.video.downloading.app.downloader.online.app.models.Websites
import com.video.downloading.app.downloader.online.app.utils.*
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory
import kotlin.collections.ArrayList


@Suppress("DEPRECATION", "NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "SENSELESS_COMPARISON")
class HomeFragment : BaseFragment() {
    private val searchUrl = "https://www.google.com/search?q=%s"
    private var urlSearch: TextInputEditText? = null
    private var webview: WebView? = null
    private var websiteList: ArrayList<Websites>? = null
    private var videoDownloadList: ArrayList<VideoDownload>? = null

    private var spanCount = 3 // 3 columns
    private var linked: String? = null
    private var viemoLink: String? = null
    private var twitterLink: String? = null
    private var twitterLink1: String? = null

    private var spacing = 30 // 50px

    private var includeEdge = false
    private lateinit var alertDialogBuilderUserInput: AlertDialog.Builder
    private lateinit var alertDialog2: AlertDialog
    private var defaultSSLSF: SSLSocketFactory? = null
    private var anim: Animation? = null
    private var videoContentSearch: VideoContentSearch? = null
    private var isStopThread: Boolean = false
    private var xGetter: LowCostVideo? = null
    private var rnds: String? = null

    @SuppressLint("StaticFieldLeak", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        websiteList = ArrayList()
        databaseHelper = DatabaseHelper(requireActivity())
        isStopThread = true
        videoDownloadList = ArrayList()
        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory()

//        rnds = (0..100).random()
        val sdf = SimpleDateFormat("dd_M_yyyy hh_mm_ss")
        val currentDate = sdf.format(Date())
        rnds = "Dated_$currentDate"

        urlSearch = root!!.findViewById(R.id.urlSearch)
        webview = root!!.findViewById(R.id.webView)
        createWebView()
        val initialUrl = getUrlFromIntent(requireActivity().intent)
        if (!TextUtils.isEmpty(initialUrl)) {
            loadUrl(initialUrl.toString())
            webview!!.requestFocus()
            changeView()
            hideKeyboard()
        } else if (requireArguments().getString("webAddress") != null) {
            loadUrl(requireArguments().getString("webAddress").toString())
            loadUrl(requireArguments().getString("webAddress").toString())
            webview!!.requestFocus()
            changeView()
            hideKeyboard()
        }
        urlSearch!!.setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (TextUtils.isEmpty(urlSearch!!.text)) {
                    showToast(getString(R.string.something_to_search))

                } else {
                    loadUrl(urlSearch!!.text.toString())
                    webview!!.requestFocus()
                    changeView()
                    hideKeyboard()
                }
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }
        }

        root!!.searchOp.setOnClickListener {
            if (TextUtils.isEmpty(urlSearch!!.text)) {
                showToast(getString(R.string.something_to_search))

            } else {
                loadUrl(urlSearch!!.text.toString())
                webview!!.requestFocus()
                changeView()
                hideKeyboard()
            }
        }
        val websiteAdapter = WebsiteAdapter(requireActivity(), websiteList())
        root!!.recyclerView.layoutManager = GridLayoutManager(activity, 3)
        val itemDecoration =
            SpacesItemDecoration(spanCount, spacing, includeEdge)
        root!!.recyclerView.addItemDecoration(itemDecoration)
        root!!.recyclerView.adapter = websiteAdapter
        websiteAdapter.notifyDataSetChanged()

        root!!.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                requireActivity(),
                root!!.recyclerView,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val websites = websiteList!![position]
                        if (InternetConnection().checkConnection(requireActivity())) {
                            loadAllWebsites(websites)
                        } else {
                            showToast(getString(R.string.no_internet))
                        }
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        Log.d(TAGI, "onLongClick")

                    }
                })
        )

        root!!.menuOp.setOnClickListener {
            addBrowserMenu(it)
        }

        root!!.fab.setOnClickListener {
            if (InternetConnection().checkConnection(requireActivity())) {
                when {
                    videoDownloadList.isNullOrEmpty() -> {
                        root!!.fab.clearAnimation()
                        showNoResourceDialog()
                    }
                    webview!!.url.contains("https://vimeo.com/") -> {
                        showDialog(getString(R.string.generate_download_link))
                        viemoLink = ViemoDownloadLink().getVideoLink(webview!!.url)
                        hideDialog()
                        downloadVideo()
                        Log.d(TAGI, "onClick: " + videoDownloadList!!.size)
                        root!!.fab.clearAnimation()
                    }
                    webview!!.url.contains("https://www.dailymotion.com/") -> {
                        val pasteLinkFragment = PasteLinkFragment()
                        DailyMotionDownloadLink(
                            webview!!.url,
                            requireActivity(),
                            this@HomeFragment,
                            true,
                            pasteLinkFragment
                        ).execute()

                        Log.d(TAGI, "onClick: " + videoDownloadList!!.size)
                        root!!.fab.clearAnimation()
                    }
                    webview!!.url.contains("https://mobile.twitter.com/") -> {
                        val split: Array<String> =
                            twitterLink!!.split("\\?".toRegex()).toTypedArray()
                        showDialog(getString(R.string.generate_download_link))
                        xGetter!!.find(split[0])
                    }
                    webview!!.url.contains("https://m.youtube.com/") -> {
                        root!!.fab.clearAnimation()
                        youtubeRestrictionDialog()

                    }

                    else -> {

                        downloadVideo()
                        Log.d(TAGI, "onClick: " + videoDownloadList!!.size)
                        root!!.fab.clearAnimation()
                    }
                }
            } else {
                showToast(getString(R.string.no_internet))
            }
        }
        root!!.fab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorBtn))

        anim = AlphaAnimation(0.0f, 1.0f)
        (anim as AlphaAnimation).duration =
            1000 //You can manage the blinking time with this parameter

        (anim as AlphaAnimation).startOffset = 20
        (anim as AlphaAnimation).repeatMode = Animation.REVERSE
        (anim as AlphaAnimation).repeatCount = Animation.INFINITE

        CookieSyncManager.createInstance(requireActivity())
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieSyncManager.getInstance().startSync()

        xGetter = LowCostVideo(requireActivity())
        xGetter!!.onFinish(object : OnTaskCompleted {
            override fun onTaskCompleted(
                vidURL: ArrayList<XModel>,
                multiple_quality: Boolean
            ) {
                hideDialog()
                if (multiple_quality) {
                    twitterLink1 = vidURL[0].url
                    downloadVideo()
                }
            }

            override fun onError() {
                //Error
                hideDialog()
            }
        })
        return root!!
    }

    private fun youtubeRestrictionDialog() {
        val layoutInflaterAndroid = LayoutInflater.from(activity)
        val view1 = layoutInflaterAndroid.inflate(R.layout.youtube_restriction_layout, null)
        alertDialogBuilderUserInput =
            MaterialAlertDialogBuilder(requireActivity())
        alertDialogBuilderUserInput.setView(view1)
        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton(getString(R.string.got_it)) { dialogBox, id ->
                root!!.fab.clearAnimation()
                dialogBox.dismiss()

            }
        alertDialog2 = alertDialogBuilderUserInput.create()
        alertDialog2.show()

        alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                root!!.fab.clearAnimation()
                alertDialog2.dismiss()
            }
    }


    fun downloadVideo() {
        var name: String? = null
        for (video in videoDownloadList!!) {
            linked = video.link
            name = video.name
        }
        val finalName = name
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> if (Build.VERSION.SDK_INT >= 23) {
                        val permissionsUtils =
                            PermissionsUtils().getInstance(requireActivity())
                        if (permissionsUtils != null) {
                            if (permissionsUtils.isAllPermissionAvailable()) {
                                when {
                                    webview!!.url.contains("https://vimeo.com/") -> {
                                        startDownload(viemoLink, finalName)
                                    }
                                    webview!!.url.contains("https://mobile.twitter.com") -> {
                                        startDownload(twitterLink1, "Twitter_$rnds")
                                    }
                                    webview!!.url.contains("https://www.dailymotion.com/") -> {
                                        val dailyMotionDownloadLink = DailymotionLink.getInstance()

                                        startDownload(
                                            dailyMotionDownloadLink.downloadUrl,
                                            finalName
                                        )
                                        dailyMotionDownloadLink.downloadUrl = ""
                                    }
                                    else -> {
                                        startDownload(linked, finalName)
                                    }
                                }
//                                downloadStart()
                                Log.d(TAGI, "permission accepted")
                            } else {
                                permissionsUtils.setActivity(requireActivity())
                                permissionsUtils.requestPermissionsIfDenied()
                            }
                        }
                    } else {
                        when {
                            webview!!.url.contains("https://vimeo.com/") -> {
                                startDownload(viemoLink, finalName)
                            }
                            webview!!.url.contains("https://mobile.twitter.com") -> {
                                startDownload(twitterLink1, "Twitter_$rnds")
                            }
                            webview!!.url.contains("https://www.dailymotion.com/") -> {
                                val dailyMotionDownloadLink = DailymotionLink.getInstance()

                                startDownload(
                                    dailyMotionDownloadLink.downloadUrl,
                                    finalName
                                )
                                dailyMotionDownloadLink.downloadUrl = ""
                            }
                            else -> {
                                startDownload(linked, finalName)
                            }
                        }
//                        downloadStart()
                    }

                    DialogInterface.BUTTON_NEGATIVE ->                     //No button clicked
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


    private fun showNoResourceDialog() {

        val layoutInflaterAndroid = LayoutInflater.from(activity)
        val view1 = layoutInflaterAndroid.inflate(R.layout.no_resource_layout, null)
        alertDialogBuilderUserInput =
            MaterialAlertDialogBuilder(requireActivity())
        alertDialogBuilderUserInput.setView(view1)
        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton(getString(R.string.got_it)) { dialogBox, id ->
                root!!.fab.clearAnimation()
                dialogBox.dismiss()

            }
        alertDialog2 = alertDialogBuilderUserInput.create()
        alertDialog2.show()

        alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                root!!.fab.clearAnimation()
                alertDialog2.dismiss()
            }
    }

    @SuppressLint("RestrictedApi")
    private fun addBrowserMenu(it: View) {
        val menuBuilder = MenuBuilder(requireActivity())
        val inflater = MenuInflater(requireActivity())
        inflater.inflate(R.menu.browser_menu, menuBuilder)
        val wrapper: Context = ContextThemeWrapper(context, R.style.PopupMenu)
        val optionsMenu = MenuPopupHelper(wrapper, menuBuilder, it)
        optionsMenu.setForceShowIcon(true)

        menuBuilder.setCallback(object : MenuBuilder.Callback {

            override fun onMenuItemSelected(
                menu: MenuBuilder?,
                item: MenuItem
            ): Boolean {
                return when (item.itemId) {
                    R.id.browse_refresh -> {
                        webview!!.reload()
                        return true
                    }
                    R.id.browse_share -> {
                        if (webview!!.url.isNullOrEmpty()) {
                            showToast(getString(R.string.no_url_share))
                        } else {
                            val sharingIntent = Intent(Intent.ACTION_SEND)
                            sharingIntent.type = "text/plain"
                            sharingIntent.putExtra(
                                Intent.EXTRA_SUBJECT,
                                webview!!.title
                            )
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, webview!!.url)
                            startActivity(
                                Intent.createChooser(
                                    sharingIntent,
                                    resources.getString(R.string.share_using)
                                )
                            )
                        }
                        return true
                    }
                    R.id.browse_copy -> {
                        if (webview!!.url.isNullOrEmpty()) {
                            showToast(getString(R.string.no_url_to_copy))
                        } else {
                            copyUrl(webview!!.url)
                        }
                        return true
                    }
                    R.id.browse_bookmarks -> {
                        findNavController().navigate(R.id.bookmarkFragment)
                        return true
                    }
                    R.id.browse_add_bookmark -> {
                        if (webview!!.url.isNullOrEmpty()) {
                            showToast(getString(R.string.no_url_to_book))
                        } else {
                            if (databaseHelper!!.checkBookmarkExist(webview!!.title)) {
                                showToast(getString(R.string.bookmark_already_exist))
                            } else {
                                databaseHelper!!.addbookmark(webview!!.title, webview!!.url)
                                showToast(getString(R.string.added_success))
                            }
                        }

                        return true
                    }
                    else -> false
                }
            }

            override fun onMenuModeChange(menu: MenuBuilder?) {
                Log.d(TAGI, "onMenuModeChange")
            }
        })
        // Display the menu
        optionsMenu.show()
    }

    private fun copyUrl(url: String?) {
        val clipboard = requireActivity()
            .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip =
            ClipData.newPlainText(webview!!.title, url)
        clipboard.setPrimaryClip(clip)
        showToast(getString(R.string.link_copied))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        // This callback will only be called when MyFragment is at least Started.

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    if (webview?.canGoBack()!!) {
                        webview?.goBack()
                    } else if (!root!!.recyclerView.isVisible) {
                        isStopThread = false
                        videoDownloadList!!.clear()
//                        Thread.currentThread().interrupt()
/*                        videoContentSearch!!.stopRunning()
                        webview!!.stopLoading()
                        root!!.fab.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorBtn)))
                        root!!.recyclerView.visibility = View.VISIBLE
                        webview!!.visibility = View.INVISIBLE
                        root!!.fab.visibility = View.GONE
                        root!!.progressBar.visibility = View.INVISIBLE
                        */
                        urlSearch?.text = Editable.Factory.getInstance().newEditable("")
                        findNavController().navigate(R.id.navigation_home)
                    } else {
                        exit()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    private fun exit() {
        val yesNoDialog =
            MaterialAlertDialogBuilder(
                requireActivity()
            )
        //yes or no alert box
        yesNoDialog.setMessage(getString(R.string.do_you_want_exit)).setCancelable(false)
            .setNegativeButton(
                getString(R.string.rate_us)
            ) { dialog: DialogInterface?, which: Int ->
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
                        )
                    )
            }
            .setPositiveButton(
                getString(R.string.exit)
            ) { dialogInterface: DialogInterface?, i: Int -> requireActivity().finishAffinity() }
            .setNeutralButton(
                getString(R.string.cancel)
            ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
        val dialog = yesNoDialog.create()
        dialog.show()
    }

    private fun loadAllWebsites(websites: Websites) {
        when (websites.text) {
            getString(R.string.facebook) -> {
                loadUrl("https://www.facebook.com")
                changeView()
            }
            getString(R.string.dailymotion) -> {
                loadUrl("https://www.dailymotion.com")
                changeView()
            }
            getString(R.string.instagram) -> {
                loadUrl("https://www.instagram.com")
                changeView()
            }
            getString(R.string.soundcloud) -> {
                loadUrl("https://www.soundcloud.com")
                changeView()
            }
            getString(R.string.vimeo) -> {
                loadUrl("https://www.vimeo.com")
                changeView()
            }
            getString(R.string.twitter) -> {
                loadUrl("https://www.twitter.com")
                changeView()
            }
            getString(R.string.vevo) -> {
                loadUrl("https://www.vevo.com")
                changeView()
            }
            getString(R.string.metcafe) -> {
                loadUrl("https://www.metcafe.com")
                changeView()
            }
            getString(R.string.twitch) -> {
                loadUrl("https://www.twitch.com")
                changeView()
            }
        }
    }

    private fun changeView() {
        webview!!.visibility = View.VISIBLE
        root!!.fab.visibility = View.VISIBLE
        root!!.recyclerView.visibility = View.INVISIBLE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView() {
        val progressBar: ProgressBar = root!!.progressBar
        val settings = webview!!.settings
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.allowUniversalAccessFromFileURLs = true
//        webview!!.addJavascriptInterface(this, "mJava")

        settings.javaScriptEnabled = true
        settings.pluginState = WebSettings.PluginState.ON
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCacheEnabled(true)
        settings.domStorageEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        webview!!.addJavascriptInterface(this, "mJava")
        webview!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.progress = newProgress
                }
            }

        }
        webview!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                progressBar.progress = 0
                progressBar.visibility = View.VISIBLE
                urlSearch!!.setText(url)
                urlSearch!!.setSelection(0)
                view.requestFocus()

            }

            override fun onPageFinished(view: WebView, url: String) {
                // Don't use the argument url here since navigation to that URL might have been
                // cancelled due to SSL error

                if (view.url.contains("https://mobile.twitter.com")) {
                    if (!SharedPrefUtils.getBooleanData(requireActivity(), "isTwitter")) {
                        guideDialog(true)
                    }
                }
                if (view.url.contains("https://m.facebook.com/")) {
                    root!!.fab.visibility = View.GONE
                    val handler = Handler()
                    handler.postDelayed({
                        webview!!.loadUrl(JavascriptNotation.value)
                    }, 3000)
                    if (!SharedPrefUtils.getBooleanData(requireActivity(), "isFacebook")) {
                        guideDialog(false)
                    }
                }
            }


            override fun onLoadResource(view: WebView, url: String) {
                try {
                    val page = view.url
                    Log.d(TAGI, "url1: $page")
                    val title = view.title
                    if (page.contains("https://m.facebook.com/")) {
                        root!!.fab.visibility = View.GONE
                        val handler = Handler()
                        handler.postDelayed({
                            webview!!.loadUrl(JavascriptNotation.valueResource)
                            webview!!.loadUrl(JavascriptNotation.getValue)
                        }, 3000)
                    } else {
                        if (isStopThread) {
                            videoContentSearch =
                                object : VideoContentSearch(requireActivity(), url, page, title) {
                                    override fun onStartInspectingURL() {
                                        Handler(Looper.getMainLooper()).post {
                                            Log.d(TAGI, "onStartInspectingURL")
                                        }
                                    }

                                    override fun onFinishedInspectingURL(finishedAll: Boolean) {
                                        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSF)
                                        if (finishedAll) {
                                            Handler(Looper.getMainLooper()).post {
                                                Log.d(TAGI, "onFinishedInspectingURL")
                                            }
                                        }
                                    }

                                    override fun onVideoFound(
                                        size: String?,
                                        type: String?,
                                        link: String?,
                                        name: String?,
                                        page: String?,
                                        chunked: Boolean,
                                        website: String?
                                    ) {
                                        try {
                                            requireActivity()
                                                .runOnUiThread {

                                                    if (page != null) {
                                                        when {
                                                            page.contains("https://mobile.twitter.com") -> {

                                                                val activity = requireActivity()
                                                                if (activity != null && isAdded) {
                                                                    val clipBoard =
                                                                        requireActivity().getSystemService(
                                                                            CLIPBOARD_SERVICE
                                                                        ) as ClipboardManager
                                                                    clipBoard.addPrimaryClipChangedListener {
                                                                        val clipData =
                                                                            clipBoard.primaryClip
                                                                        val item =
                                                                            clipData!!.getItemAt(0)
                                                                        val text =
                                                                            item.text.toString()
                                                                        Log.d(
                                                                            TAGI,
                                                                            "twitter: $text"
                                                                        )
                                                                        if (text.contains("https://twitter.com/"))
                                                                            twitterLink = text
                                                                        requireActivity()
                                                                            .runOnUiThread {
                                                                                root!!.fab.backgroundTintList =
                                                                                    ColorStateList.valueOf(
                                                                                        resources.getColor(
                                                                                            R.color.colorButton
                                                                                        )
                                                                                    )
                                                                                root!!.fab.clearAnimation()
                                                                                root!!.fab.startAnimation(
                                                                                    anim
                                                                                )
                                                                            }
                                                                        // Access your context here using YourActivityName.this
                                                                    }
                                                                }
                                                            }
                                                            page.contains("https://m.youtube.com/") -> {
                                                                root!!.fab.backgroundTintList =
                                                                    ColorStateList.valueOf(
                                                                        resources.getColor(
                                                                            R.color.colorButton
                                                                        )
                                                                    )
                                                                root!!.fab.clearAnimation()
                                                            }
                                                            else -> {
                                                                root!!.fab.backgroundTintList =
                                                                    ColorStateList.valueOf(
                                                                        resources.getColor(
                                                                            R.color.colorButton
                                                                        )
                                                                    )
                                                                root!!.fab.clearAnimation()
                                                                root!!.fab.startAnimation(anim)
                                                            }
                                                        }
                                                    }
                                                }
                                            videoDownloadList!!.clear()
                                            Log.d(
                                                TAGI,
                                                "onVideoFound: $size,$type,$link,$name,$page,$chunked,$website"
                                            )
                                            videoDownloadList!!.add(
                                                VideoDownload(
                                                    size.toString(),
                                                    type.toString(),
                                                    link.toString(),
                                                    name.toString(),
                                                    page.toString(),
                                                    chunked,
                                                    website.toString()
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }

                                }
                            (videoContentSearch as VideoContentSearch).start()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val sslErrors = arrayOf(
                "Not yet valid",
                "Expired",
                "Hostname mismatch",
                "Untrusted CA",
                "Invalid date",
                "Unknown error"
            )

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                val primaryError = error.primaryError
                val errorStr =
                    if (primaryError >= 0 && primaryError < sslErrors.size) sslErrors[primaryError] else "Unknown error $primaryError"
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Insecure connection")
                    .setMessage(
                        String.format(
                            "Error: %s\nURL: %s\n\nCertificate:\n%s",
                            errorStr,
                            error.url,
                            certificateToStr(error.certificate)
                        )
                    )
                    .setPositiveButton(
                        getString(R.string.proceed)
                    ) { dialog: DialogInterface?, which: Int -> handler.proceed() }
                    .setNegativeButton(
                        getString(R.string.cancel)
                    ) { dialog: DialogInterface?, which: Int -> handler.cancel() }
                    .show()
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private fun certificateToStr(certificate: SslCertificate?): String? {
        if (certificate == null) {
            return null
        }
        var s = ""
        val issuedTo = certificate.issuedTo
        if (issuedTo != null) {
            s += """
                Issued to: ${issuedTo.dName}
                
                """.trimIndent()
        }
        val issuedBy = certificate.issuedBy
        if (issuedBy != null) {
            s += """
                Issued by: ${issuedBy.dName}
                
                """.trimIndent()
        }
        val issueDate = certificate.validNotBeforeDate
        if (issueDate != null) {
            s += String.format("Issued on: %tF %tT %tz\n", issueDate, issueDate, issueDate)
        }
        val expiryDate = certificate.validNotAfterDate
        if (expiryDate != null) {
            s += String.format(
                "Expires on: %tF %tT %tz\n",
                expiryDate,
                expiryDate,
                expiryDate
            )
        }
        return s
    }

    private fun loadUrl(url: String) {
        isStopThread = true
        var url = url
        url = url.trim { it <= ' ' }
        if (url.isEmpty()) {
            url = "about:blank"
        }
        url =
            if (url.startsWith("about:") || url.startsWith("javascript:") || url.startsWith("file:") || url.startsWith(
                    "data:"
                ) ||
                url.indexOf(' ') == -1 && Patterns.WEB_URL.matcher(url).matches()
            ) {
                val indexOfHash = url.indexOf('#')
                val guess = URLUtil.guessUrl(url)
                if (indexOfHash != -1 && guess.indexOf('#') == -1) {
                    // Hash exists in original URL but no hash in guessed URL
                    guess + url.substring(indexOfHash)
                } else {
                    guess
                }
            } else {
                URLUtil.composeSearchUrl(url, searchUrl, "%s")
            }
        webview?.loadUrl(url)
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm =
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)!!
        imm.hideSoftInputFromWindow(urlSearch!!.windowToken, 0)
    }

    private fun getUrlFromIntent(intent: Intent): String? {
        return if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            intent.dataString
        } else if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else if (Intent.ACTION_WEB_SEARCH == intent.action && intent.getStringExtra("query") != null) {
            intent.getStringExtra("query")
        } else {
            ""
        }
    }

    private fun websiteList(): ArrayList<Websites> {
        websiteList?.add(Websites(R.drawable.fb, getString(R.string.facebook)))
        websiteList?.add(Websites(R.drawable.instagram, getString(R.string.instagram)))
//        websiteList?.add(Websites(R.drawable.soundcloud, getString(R.string.soundcloud)))
        websiteList?.add(Websites(R.drawable.vimeo, getString(R.string.vimeo)))
        websiteList?.add(Websites(R.drawable.dailymotion, getString(R.string.dailymotion)))
        websiteList?.add(Websites(R.drawable.twitter, getString(R.string.twitter)))
//        websiteList?.add(Websites(R.drawable.vevo, getString(R.string.vevo)))
//        websiteList?.add(Websites(R.drawable.metcafe, getString(R.string.metcafe)))
        websiteList?.add(Websites(R.drawable.twitch, getString(R.string.twitch)))
        return websiteList!!
    }

    @JavascriptInterface
    fun getData(pathvideo: String) {
        var finalurl: String = pathvideo
        finalurl = finalurl.replace("%3A".toRegex(), ":")
        finalurl = finalurl.replace("%2F".toRegex(), "/")
        finalurl = finalurl.replace("%3F".toRegex(), "?")
        finalurl = finalurl.replace("%3D".toRegex(), "=")
        finalurl = finalurl.replace("%26".toRegex(), "&")
        val finalUrl = finalurl

        val alertDialog =
            MaterialAlertDialogBuilder(requireActivity())
        alertDialog.setTitle(getString(R.string.download))
        alertDialog.setMessage(getString(R.string.download_this_video))
        alertDialog.setPositiveButton(
            getString(R.string.yes)
        ) { dialog: DialogInterface?, which: Int ->


            webview!!.post {
                if (InternetConnection().checkConnection(requireActivity())) {
                    startDownload(finalUrl, "Facebook_$rnds")
                } else {
                    showToast(getString(R.string.no_internet))
                }
            }
            dialog?.dismiss()
        }

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(
            getString(R.string.watch)
        ) { dialog: DialogInterface?, which: Int ->
            if (InternetConnection().checkConnection(requireActivity())) {
                val intent = Intent(activity, VideoPlayActivity::class.java)
                intent.putExtra("videofilename", finalurl)
                startActivity(intent)
            } else {
                showToast(getString(R.string.no_internet))
            }
        }
        alertDialog.setNeutralButton(
            getString(R.string.copy_link)
        ) { dialog: DialogInterface?, which: Int ->
            // User pressed Cancel button. Write Logic Here
            requireActivity().runOnUiThread {
                copyUrl(finalurl)
            }
            dialog?.dismiss()
        }
        alertDialog.show()

    }

}