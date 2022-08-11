package com.cbi.app.trs.features.fragments.movies.pain_detail

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.core.platform.OnDownloadClickListener
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.usecases.pain.GetStartedPain
import com.cbi.app.trs.domain.usecases.pain.GetUnderstandingPain
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.PAIN_AREA
import com.cbi.app.trs.features.viewmodel.PainViewModel
import kotlinx.android.synthetic.main.fragment_pain_detail.*
import javax.inject.Inject

class PainDetailFragment : BaseFragment(), OnItemClickListener, OnDownloadClickListener {
    override fun layoutId() = R.layout.fragment_pain_detail

    private var startedMovie: MovieData? = null

    @Inject
    lateinit var understandingAdapter: PainDetailAdapter

    @Inject
    lateinit var advanceAdapter: PainDetailAdapter

    @Inject
    lateinit var mobilityRxAdapter: PainDetailAdapter

    lateinit var painViewModel: PainViewModel

    var painArea: SystemData.PainArea? = null

    var playingMovie: MovieData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (playingMovie == null) {
            loadData()
        } else {
            loadMobilityRxData()
            loadUnderstandingData()
            loadAdvanceData()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onReloadData() {
        loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        painViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(understandingPain, ::onReceiveUnderstandingPain)
            observe(advancePain, ::onReceiveAdvancePain)
            observe(mobilityRxPain, ::onReceiveMobilityRxPain)
            observe(startedPain, ::onReceiveStartedPain)
        }
        painArea = arguments?.getParcelable(PAIN_AREA)
    }

    private fun onReceiveStartedPain(movieData: MovieData?) {
        hideProgress()
        if (movieData == null) return
        startedMovie = movieData
        playingMovie?.is_playing = false
        playingMovie = movieData
        playingMovie?.is_playing = true
        loadPlayingVideoInfo()
        (parentFragment as MoviePlayFragment).playMovie(playingMovie)

        //call API understand pain
        painViewModel.getUnderstandingPain(Pair(userDataCache.get()?.user_token?.userID, GetUnderstandingPain.Params(painArea?.pain_area_id, "under").apply { limit = 30 }))
    }

    private fun onReceiveMobilityRxPain(data: SearchMovieEntity.Data?) {
        hideProgress()
        if (data == null) return

        if (data.list_video.isNotEmpty()) {
            pain_mobility_rx_area.visibility = View.VISIBLE
            mobilityRxAdapter.collection = data.list_video
        }
        loadMobilityRxData()
    }

    private fun onReceiveUnderstandingPain(data: SearchMovieEntity.Data?) {
        hideProgress()
        if (data == null) return

        if (data.list_video.isNotEmpty()) {
            pain_understanding_area.visibility = View.VISIBLE
            val videoList: MutableList<MovieData> = data.list_video as MutableList<MovieData>
            startedMovie?.let {
                videoList.add(0, it)
            }
            understandingAdapter.collection = videoList
        }
        loadUnderstandingData()
    }

    private fun onReceiveAdvancePain(data: SearchMovieEntity.Data?) {
        hideProgress()
        if (data == null) return

        if (data.list_video.isNotEmpty()) {
            pain_advance_area.visibility = View.VISIBLE
            advanceAdapter.collection = data.list_video
        }
        loadAdvanceData()
    }

    private fun loadAdvanceData() {
        advance_count.text = "${advanceAdapter.collection.size}"
        advance_duration.text = "${getDuration(advanceAdapter.collection)}"
    }

    private fun loadUnderstandingData() {
        understand_count.text = "${understandingAdapter.collection.size}"
        understand_duration.text = "${getDuration(understandingAdapter.collection)}"
    }

    private fun loadPlayingVideoInfo() {
        video_title.text = "${playingMovie?.video_title}"
        if (playingMovie?.video_duration != null) video_duration.text = DateUtils.formatElapsedTime(playingMovie?.video_duration!! * 1L)
    }


    private fun loadMobilityRxData() {
        mobility_count.text = "${mobilityRxAdapter.collection.size}"
        mobility_duration.text = "${getDuration(mobilityRxAdapter.collection)}"
    }

    private fun getDuration(videos: List<MovieData>): Int {
        var total = 0
        for (item in videos) {
            total += item.video_duration
        }
        return total / 60
    }

    fun loadNextMovie() {
        if (playingMovie == null) return
        var next: MovieData? = null

//        if (!isAllowForFreemium()) return

        val totalVideo = ArrayList<MovieData>().apply {
            startedMovie?.let { add(it) }
            addAll(understandingAdapter.collection)
            addAll(mobilityRxAdapter.collection)
            addAll(advanceAdapter.collection)
        }

        var currentIndex = totalVideo.indexOf(playingMovie!!)
        if (currentIndex == -1 || totalVideo.isEmpty()) {
            DialogAlert()
                    .setTitle("Error")
                    .setMessage("No movies for ${painArea?.pain_area_title}")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
            return
        } else if (currentIndex in 0..totalVideo.size - 2) {
            next = totalVideo[currentIndex + 1]
        } else {
            DialogAlert()
                    .setTitle("Congratulations!")
                    .setMessage("You have watched all movies of ${painArea?.pain_area_title}")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
            return
        }

        if (next != null) {
            playingMovie?.is_playing = false
            playingMovie = next
            playingMovie?.is_playing = true
            loadPlayingVideoInfo()
            mobilityRxAdapter.notifyDataSetChanged()
            understandingAdapter.notifyDataSetChanged()
            advanceAdapter.notifyDataSetChanged()
            (parentFragment as MoviePlayFragment).playMovie(playingMovie)
        }
    }

    private fun loadData() {
        showProgress()
        painViewModel.getStartedPain(Pair(userID, GetStartedPain.Params(painArea?.pain_area_id)))
        painViewModel.getMobilityRxPain(Pair(userDataCache?.get()?.user_token?.userID, GetUnderstandingPain.Params(painArea?.pain_area_id).apply { limit = 30 }))
        painViewModel.getAdvancePain(Pair(userDataCache?.get()?.user_token?.userID, GetUnderstandingPain.Params(painArea?.pain_area_id, "advanced").apply { limit = 30 }))
    }

    private fun initView() {
        area_title.text = "${painArea?.pain_area_title}"
        pain_mobility_rx_title.text = "Your Pain Prescription"
        pain_understanding_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        pain_understanding_recylerview.adapter = understandingAdapter.apply {
//            onLoadmoreListener = this@PainDetailFragment
            onItemClickListener = this@PainDetailFragment
            onDownloadClickListener = this@PainDetailFragment
        }

        pain_mobility_rx_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        pain_mobility_rx_recylerview.adapter = mobilityRxAdapter.apply {
//            onLoadmoreListener = this@PainDetailFragment
            onItemClickListener = this@PainDetailFragment
            onDownloadClickListener = this@PainDetailFragment
        }

        pain_advance_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        pain_advance_recylerview.adapter = advanceAdapter.apply {
//            onLoadmoreListener = this@PainDetailFragment
            onItemClickListener = this@PainDetailFragment
            onDownloadClickListener = this@PainDetailFragment
        }
    }
//
//    override fun onLoadMore() {
//
//    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item != null && item is MovieData) {
            showProgress()
            playingMovie?.is_playing = false
            playingMovie = item
            playingMovie?.is_playing = true
            loadPlayingVideoInfo()
            mobilityRxAdapter.notifyDataSetChanged()
            understandingAdapter.notifyDataSetChanged()
            advanceAdapter.notifyDataSetChanged()
            (parentFragment as MoviePlayFragment).playMovie(playingMovie)
        }
    }

    override fun onDownloadClick(item: MovieData, position: Int) {
        if (isAllowForFreemium()) {
            (parentFragment as MoviePlayFragment).downloadMovie(item)
        }
    }
}