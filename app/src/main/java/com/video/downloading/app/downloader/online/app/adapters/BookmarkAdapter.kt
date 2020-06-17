package com.video.downloading.app.downloader.online.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.models.Bookmarks
import kotlinx.android.synthetic.main.bookmark_layout.view.*


class BookmarkAdapter(val bookmarksList: ArrayList<Bookmarks>) :
    RecyclerView.Adapter<BookmarkAdapter.MyViewHolder>() {
    private val mColorGenerator = ColorGenerator.DEFAULT
    private var mDrawableBuilder: TextDrawable? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return bookmarksList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val websites = bookmarksList[position]
        var letter: String? = null
        holder.itemView.recycle_title.text = websites.title
        if (!websites.title.isEmpty()) {
            letter = websites.title.substring(0, 1)
        }

        val color = mColorGenerator.randomColor

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
            .buildRound(letter, color)
        holder.itemView.thumbnail_image.setImageDrawable(mDrawableBuilder)
    }
}