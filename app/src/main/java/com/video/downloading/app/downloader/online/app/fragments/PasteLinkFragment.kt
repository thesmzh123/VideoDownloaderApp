package com.video.downloading.app.downloader.online.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import com.google.android.material.textfield.TextInputEditText
import com.video.downloading.app.downloader.online.app.R
import kotlinx.android.synthetic.main.fragment_paste_link.view.*

class PasteLinkFragment : BaseFragment() {
    private var urlText: TextInputEditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_paste_link, container, false)
        urlText = root!!.videoUrl
        root!!.downloadBtn.setOnClickListener {
            if (urlText!!.text!!.isEmpty()) {
                showToast(getString(R.string.fill_the_field))
            } else if (!URLUtil.isValidUrl(urlText!!.text.toString())){
                showToast(getString(R.string.valid_url))
            }
        }
        return root!!
    }

}