package com.cbi.app.trs.features.fragments.downloaded

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.cache.DownloadedMovieCache
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.eventbus.DownloadedEvent
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.movies.DownloadTracker
import com.cbi.app.trs.features.fragments.search.SearchResultAdapter
import kotlinx.android.synthetic.main.fragment_downloaded.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class DownloadedFragment : DarkBaseFragment(), OnItemClickListener, SearchResultAdapter.OnDeleteClickListener {
    override fun layoutId() = R.layout.fragment_downloaded

    @Inject
    lateinit var downloadedMovieCache: DownloadedMovieCache

    @Inject
    lateinit var downloadedAdapter: DownloadedAdapter

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var downloadTracker: DownloadTracker

    @Inject
    lateinit var networkHandler: NetworkHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkDownloadStatus()
        initView()

        var message = getString(R.string.downloaded_message_not_empty)
        if (downloadedMovieCache.get().list.isNullOrEmpty()){
            message = getString(R.string.downloaded_message_empty)
        }

        if (!networkHandler.isConnected){
            DialogAlert().setTitle(getString(R.string.message)).setMessage(message)
                    .setTitlePositive("OK").onPositive {

                    }.show(activity)
        }
    }

    private fun checkDownloadStatus() {
        for (movie in downloadedMovieCache.get().list) {
            val uri = Uri.parse(movie.video_play_url)
            if (downloadTracker.getDownloadStatus(uri) == DownloadTracker.DownloadState.DOWNLOADED) {
                downloadedMovieCache.put(downloadedMovieCache.get().apply { updateDownloadStatusMovie(uri) })
            }
        }

    }

    private fun initView() {
        downloaded_recylerview.layoutManager = LinearLayoutManager(activity)
        downloaded_recylerview.adapter = downloadedAdapter.apply {
            onItemClickListener = this@DownloadedFragment
            onDeleteClickListener = this@DownloadedFragment
            collection = downloadedMovieCache.get().getDownloadedList()
            if (collection.isEmpty()) {
                empty_text_1.text = "No Downloaded Yet!"
                empty_text_2.text = "No videos selected for download. Find some today!"
            } else {
                empty_text_1.text = ""
                empty_text_2.text = ""
            }
        }
        back_btn.setOnClickListener { close() }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item != null && item is MovieData) {
            if (networkHandler.isConnected) {
                navigator.showMovieDetails(activity, item)
            } else {
                navigator.showOfflineMovieDetails(activity, item)
            }
        }
    }

    override fun onDeleteItem(movieData: MovieData) {
        DialogAlert().setTitle("Delete this video?").setMessage("Are you sure you want to delete this video? ")
                .setTitleNegative("Cancel").setTitlePositive("OK").onPositive {
                    val uri = Uri.parse(movieData!!.video_play_url)
                    downloadTracker.removeDownload(uri)
                    downloadedMovieCache.put(downloadedMovieCache.get().apply { removeMovie(movieData) })
                    downloadedAdapter.collection = downloadedMovieCache.get().getDownloadedList()
                    if (downloadedAdapter.collection.isEmpty()) {
                        empty_text_1.text = "No Downloaded Yet!"
                        empty_text_2.text = "No videos selected for download. Find some today"
                    }
                }.show(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onDownloadedEvent(event: DownloadedEvent) {
        checkDownloadStatus()
        initView()
    }

}