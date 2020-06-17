package com.video.downloading.app.downloader.online.app.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.adapters.BookmarkAdapter
import com.video.downloading.app.downloader.online.app.models.Bookmarks
import com.video.downloading.app.downloader.online.app.utils.ClickListener
import com.video.downloading.app.downloader.online.app.utils.Constants
import com.video.downloading.app.downloader.online.app.utils.DatabaseHelper
import com.video.downloading.app.downloader.online.app.utils.RecyclerTouchListener
import kotlinx.android.synthetic.main.fragment_bookmark.view.*


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class BookmarkFragment : BaseFragment() {
    var bookmarkList: ArrayList<Bookmarks>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_bookmark, container, false)
        databaseHelper = DatabaseHelper(requireActivity())
        bookmarkList = databaseHelper!!.getAllBookmarks()
        //creating our adapter
        val adapter = BookmarkAdapter(bookmarkList!!)

        //now adding the adapter to recyclerview
        root!!.recyclerView.adapter = adapter
        checkEmptyState()
        root!!.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                requireActivity(),
                root!!.recyclerView,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val websites = bookmarkList!![position]
                        val bundle = bundleOf("webAddress" to websites.address)
                        findNavController().navigate(R.id.navigation_home, bundle)


                    }

                    override fun onLongClick(view: View?, position: Int) {
                        Log.d(Constants.TAGI, "onLongClick")
                        val yesNoDialog =
                            MaterialAlertDialogBuilder(
                                requireActivity()
                            )
                        //yes or no alert box
                        yesNoDialog.setMessage(context?.getString(R.string.delete_this_bookmark))
                            .setCancelable(false)
                            .setNegativeButton(
                                context?.getString(R.string.no)
                            ) { dialog: DialogInterface?, which: Int ->
                                dialog?.dismiss()
                            }
                            .setPositiveButton(
                                context?.getString(R.string.yes)
                            ) { dialogInterface: DialogInterface?, i: Int ->
                                try {
                                    val websites = bookmarkList!![position]
                                    databaseHelper!!.deletebookmark(websites.id)
                                    bookmarkList!!.removeAt(position)
                                    adapter.notifyItemChanged(position)
                                    adapter.notifyItemRangeRemoved(0, bookmarkList!!.size)
                                    checkEmptyState()
                                    dialogInterface!!.dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                        val dialog = yesNoDialog.create()
                        dialog.show()

                    }
                })
        )

        return root!!
    }

    private fun checkEmptyState() {
        if (bookmarkList!!.isEmpty()) {
            root!!.recyclerView.visibility = View.GONE
            root!!.emptyView.visibility = View.VISIBLE
        } else {
            root!!.recyclerView.visibility = View.VISIBLE
            root!!.emptyView.visibility = View.GONE
        }
    }

}