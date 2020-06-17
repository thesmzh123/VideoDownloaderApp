@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.video.downloading.app.downloader.online.app.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.actvities.OtherVideosPlayer
import com.video.downloading.app.downloader.online.app.fragments.DownloadFragment
import com.video.downloading.app.downloader.online.app.models.DownloadFile
import kotlinx.android.synthetic.main.video_download_layout.view.*
import java.io.File
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.log10
import kotlin.math.pow

class DownloadFileAdapter(
    val context: Context,
    private val downloadFileList: ArrayList<DownloadFile>,
    private val downloadFragment: DownloadFragment
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
            val intent = Intent(context, OtherVideosPlayer::class.java)
            intent.putExtra("videoUrl", downloadFile.filePath)
            context.startActivity(intent)
        }
        holder.itemView.menu_op.setOnClickListener {
            showPopUp(it, position)
        }

    }

    private fun showPopUp(view: View, position: Int) {
        val popupMenu = PopupMenu(context, view)
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.video_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.deleteop -> {
                    deleteFile(position)
                    return@setOnMenuItemClickListener true
                }
                R.id.shareop -> {
                    shareFile(position)
                    return@setOnMenuItemClickListener true
                }
                R.id.detailsop -> {
                    detailOfFile(position)
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
    }

    private fun deleteFile(position: Int) {
        val alertDialogBuilder =
            MaterialAlertDialogBuilder(context)
        alertDialogBuilder.setMessage(context.getString(R.string.delmsg)).setCancelable(true)
            .setPositiveButton(
                context.getString(R.string.yes)
            ) { dialog: DialogInterface?, which: Int ->
                File(downloadFileList[position].filePath).delete()
                Toast.makeText(
                    context,
                    context.getString(R.string.file_deleted),
                    Toast.LENGTH_SHORT
                ).show()
                downloadFileList.removeAt(position)
                notifyItemChanged(position)
                notifyItemRangeRemoved(0, downloadFileList.size)
                downloadFragment.checkEmptyState()

            }.setNegativeButton(
                context.getString(R.string.no)
            ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    //TODO: share a file
    private fun shareFile(position: Int) {
        val sharingIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Share using " + context.getString(R.string.app_name) + " android application"
        )
        sharingIntent.type = "video/*"
        val files = ArrayList<Uri>()
        val recording = downloadFileList[position]
        val filePath: String = recording.filePath
        val file = File(filePath)
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context, context.packageName +
                        ".provider", file
            )
        } else {
            Uri.fromFile(file)
        }
        files.add(uri)
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(sharingIntent, "Share video using"))
    }

    @SuppressLint("SetTextI18n")
    private fun detailOfFile(position: Int) {
        val recording= downloadFileList[position]
        val path: String = recording.filePath
        val file = File(path)
        val fileName = file.name
        val dateString =
            DateFormat.format("MM/dd/yyyy", Date(file.lastModified()))
                .toString()
        val timeString =
            DateFormat.format("hh:mm:ss", Date(file.lastModified()))
                .toString()
        val layoutInflaterAndroid = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view1: View =
            layoutInflaterAndroid.inflate(R.layout.detail_layout, null)
        val alertDialogBuilderUserInput =
            MaterialAlertDialogBuilder(context)
        alertDialogBuilderUserInput.setView(view1)
        val textName: MaterialTextView
        val textSize: MaterialTextView
        val textdate: MaterialTextView
        val textpath: MaterialTextView
        textName = view1.findViewById(R.id.textName)
        textSize = view1.findViewById(R.id.textSize)
        textdate = view1.findViewById(R.id.textdate)
        textpath = view1.findViewById(R.id.textpath)
        textdate.text = "$dateString $timeString"
        textName.text = fileName
        textpath.text = path
        textSize.text = totalSize
        alertDialogBuilderUserInput
            .setCancelable(true)
            .setPositiveButton(
                "Ok"
            ) { dialogBox: DialogInterface, id: Int -> dialogBox.cancel() }
        val alertDialog2 = alertDialogBuilderUserInput.create()
        alertDialog2.show()
        alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener { v: View? -> alertDialog2.dismiss() }
        alertDialogBuilderUserInput.setOnCancelListener { dialogInterface: DialogInterface? -> alertDialog2.dismiss() }
    }
}