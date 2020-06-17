package com.video.downloading.app.downloader.online.app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.actvities.OtherVideosPlayer
import com.video.downloading.app.downloader.online.app.models.DownloadFile
import kotlinx.android.synthetic.main.video_download_layout.view.*
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow

class DownloadFileAdapter(
    val context: Context,
    private val downloadFileList: ArrayList<DownloadFile>
) :
    RecyclerView.Adapter<DownloadFileAdapter.MyViewHolder>() {
    private val units =
        arrayOf("B", "KB", "MB", "GB", "TB")
    private var totalSize: String? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_download_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return downloadFileList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val downloadFile = downloadFileList[position]
        val file = File(downloadFile.filePath)


        //file size android

        //file size android
        val fileSize = file.length().toString().toLong()
        val digitGroup =
            (log10(fileSize.toDouble()) / log10(1024.0)).toInt()
        totalSize = (DecimalFormat("#,##0.#")
            .format(fileSize / 1024.0.pow(digitGroup.toDouble()))
                + " " + units[digitGroup])

        //code for video duration

        //code for video duration
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(
            context,
            Uri.fromFile(file.absoluteFile)
        )


        val time =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)


        @SuppressLint("DefaultLocale") val hms = String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time.toLong()),
            TimeUnit.MILLISECONDS.toMinutes(time.toLong()) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(time.toLong())
            ),
            TimeUnit.MILLISECONDS.toSeconds(time.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(time.toLong())
            )
        )

        retriever.release()
        Glide.with(context).asBitmap()
            .load(downloadFile.filePath)
            .into(holder.itemView.video_icon)

        holder.itemView.videoduration.text = hms
        holder.itemView.row_title.text = downloadFile.fileName
        holder.itemView.row_title.isSelected = true
        holder.itemView.videosizee.text = totalSize

        holder.itemView.mainLayout.setOnClickListener {
            val intent= Intent(context,OtherVideosPlayer::class.java)
            intent.putExtra("videoUrl",downloadFile.filePath)
            context.startActivity(intent)
        }

    }
}