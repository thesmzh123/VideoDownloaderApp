package com.video.downloading.app.downloader.online.app.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.fragment.findNavController
import com.find.lost.app.phone.utils.InternetConnection
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.google.android.gms.ads.AdListener
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
        val adapter = BookmarkAdapter(requireActivity(),bookmarkList!!)

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
                        if (InternetConnection().checkConnection(requireActivity())) {

                            if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                                if (interstitial.isLoaded) {
                                    if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                        interstitial.show()
                                    } else {
                                        Log.d(Constants.TAGI, "App Is In Background Ad Is Not Going To Show")

                                    }
                                } else {
                                    val bundle = bundleOf("webAddress" to websites.address)
                                    findNavController().navigate(R.id.navigation_home, bundle)

                                }
                                interstitial.adListener = object : AdListener() {
                                    override fun onAdClosed() {
                                        requestNewInterstitial()
                                        val bundle = bundleOf("webAddress" to websites.address)
                                        findNavController().navigate(R.id.navigation_home, bundle)

                                    }
                                }
                            } else {
                                val bundle = bundleOf("webAddress" to websites.address)
                                findNavController().navigate(R.id.navigation_home, bundle)

                            }
                        } else {
                            showToast(getString(R.string.no_internet))
                        }

                    }

                    override fun onLongClick(view: View?, position: Int) {
                        Log.d(Constants.TAGI, "onLongClick")
                        if (!SharedPrefUtils.getBooleanData(requireActivity(), "hideAds")) {
                            if (interstitial.isLoaded) {
                                if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
                                        Lifecycle.State.STARTED)) {
                                    interstitial.show()
                                } else {
                                    Log.d(Constants.TAGI, "App Is In Background Ad Is Not Going To Show")

                                }
                            } else {
                                deleteBookmark(position, adapter)

                            }
                            interstitial.adListener = object : AdListener() {
                                override fun onAdClosed() {
                                    requestNewInterstitial()
                                    deleteBookmark(position, adapter)

                                }
                            }
                        } else {
                            deleteBookmark(position, adapter)

                        }

                    }
                })
        )
        loadInterstial()
        return root!!
    }

    private fun deleteBookmark(position: Int, adapter: BookmarkAdapter) {
        val yesNoDialog =
            MaterialAlertDialogBuilder(
                requireActivity()
            )
        //yes or no alert box
        yesNoDialog.setMessage(getString(R.string.delete_this_bookmark))
            .setCancelable(false)
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
            }
            .setPositiveButton(
                getString(R.string.yes)
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