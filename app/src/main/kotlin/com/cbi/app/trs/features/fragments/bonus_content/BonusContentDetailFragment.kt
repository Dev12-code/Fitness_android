package com.cbi.app.trs.features.fragments.bonus_content

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.bonus.BonusMovieEntity
import com.cbi.app.trs.domain.usecases.bonus.GetBonusDetail
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.BONUS_DETAIL
import com.cbi.app.trs.features.viewmodel.BonusViewModel
import kotlinx.android.synthetic.main.fragment_bonus_content_detail.*
import javax.inject.Inject

class BonusContentDetailFragment : BaseFragment(), OnItemClickListener, OnLoadmoreListener {
    override fun layoutId() = R.layout.fragment_bonus_content_detail

    private var playingMovie: MovieData? = null
    private var currentPage: Int = 1
    private var totalPage: Int = 0

    private var bonusDetail: SystemData.Bonus? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var adapter: BonusContentDetailAdapter

    lateinit var bonusViewModel: BonusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        bonusDetail = arguments?.getParcelable(BONUS_DETAIL)
        bonusViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(bonusDetail, ::onReceiveBonusDetail)
        }
    }

    private fun onReceiveBonusDetail(data: BonusMovieEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (currentPage == 1) {
            adapter.collection.clear()
            if (data.list_video.isNotEmpty()) {
                playNextMovie(data.list_video[0])
            }
        }

        adapter.collection.addAll(data.list_video)
        adapter.notifyDataSetChanged()
        if (adapter.collection.isEmpty()) {
            bonusDetail?.let {
                when {
                    it.bonus_title.contains("Mini") -> {
                        empty_text_1.text = "Send your first video question."
                        empty_text_2.text = "It's time to start Mini Q&A."
                    }
                    it.bonus_title.contains("Kelly") -> {
                        empty_text_1.text = "No exercise."
                        empty_text_2.text = "Don't be lazy. Keep energetic and choose a workout."
                    }
                    it.bonus_title.contains("Ask me") -> {
                        empty_text_1.text = "Let's the break time begin."
                        empty_text_2.text = "Give yourself a break from the exercise."
                    }
                }
            }
        }
        currentPage = data.page
        totalPage = data.max_page
        adapter.onLoadmoreListener = this
        loadBonusInfo()
    }

    private fun loadBonusInfo() {
        video_count.text = "${adapter.collection.size}"
        var duration = 0
        for (video in adapter.collection) {
            duration += video.video_duration
        }
        val hour: Int = (duration / 3600)
        val mins: Int = ((duration % 3600) / 60)

        video_hour.text = "$hour"
        video_mins.text = "$mins"
    }

    private fun loadData() {
        showProgress()
        bonusViewModel.callBonusDetail(userID, GetBonusDetail.Params(bonusDetail?.bonus_id).apply { page = currentPage })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (adapter.collection.isEmpty()) {
            currentPage = 1
            loadData()
        }
    }

    override fun onReloadData() {
        currentPage = 1
        loadData()
    }

    private fun initView() {
        bonus_content_detail_recylerview.layoutManager = LinearLayoutManager(activity)
        bonus_content_detail_recylerview.adapter = adapter.apply {
            onItemClickListener = this@BonusContentDetailFragment
            onLoadmoreListener = this@BonusContentDetailFragment
        }
        bonus_content_detail_title.text = "${bonusDetail?.bonus_title}"
    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item != null && item is MovieData) {
            playNextMovie(item)
        }
    }

    private fun playNextMovie(movie: MovieData?) {
        playingMovie?.is_playing = false
        playingMovie = movie
        playingMovie?.is_playing = true
        (parentFragment as MoviePlayFragment).playMovie(playingMovie)
        adapter.notifyDataSetChanged()
    }

    override fun onLoadMore() {
        if (currentPage >= totalPage) {
            adapter.onLoadmoreListener = null
            return
        }
        currentPage++
        loadData()
    }

    fun loadNextMovie() {
        if (playingMovie == null) return
        var next: MovieData? = null

        var currentIndex = adapter.collection.indexOf(playingMovie!!)
        if (currentIndex in 0..adapter.collection.size - 2) {
            next = adapter.collection[currentIndex + 1]
        } else if (currentIndex == adapter.collection.size - 1) {
            DialogAlert()
                    .setTitle("Congratulations!")
                    .setMessage("You have watched all movies of ${bonusDetail?.bonus_title}")
                    .setCancel(false)
                    .setTitlePositive(getString(R.string.ok))
                    .show(requireContext())
            return
        }

        if (next != null) {
            playNextMovie(next)
        }
    }
}