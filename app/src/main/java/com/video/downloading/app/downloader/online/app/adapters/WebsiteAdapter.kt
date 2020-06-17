package com.video.downloading.app.downloader.online.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.models.Websites
import kotlinx.android.synthetic.main.home_recyclerview_layout.view.*
import java.security.AccessControlContext

class WebsiteAdapter(val context: Context, val websiteList: ArrayList<Websites>) :
    RecyclerView.Adapter<WebsiteAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recyclerview_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return websiteList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val websites = websiteList[position]
        holder.itemView.webIconText.text = websites.text
        Glide.with(context).load(websites.icon).into(holder.itemView.webIcon)
    }
}