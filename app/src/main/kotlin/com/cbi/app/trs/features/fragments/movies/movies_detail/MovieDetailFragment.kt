package com.cbi.app.trs.features.fragments.movies.movies_detail

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UpsellData
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.movie.GetReferenceMovie
import com.cbi.app.trs.domain.usecases.movie.PostUpSell
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOVIE_DETAIL
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOVIE_ID
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOVIE_THUMBNAIL
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.OFF_LINE
import com.cbi.app.trs.features.fragments.search.SearchTagView
import com.cbi.app.trs.features.viewmodel.MovieDetailViewModel
import com.cbi.app.trs.features.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.coach_tip_item.view.*
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class MovieDetailFragment : BaseFragment(), OnItemClickListener {

    override fun layoutId() = R.layout.fragment_movie_detail
    private var upsellTotalPage = 0
    private var upsellCurrentPage = 1

    var equipment_tags: ArrayList<SystemData.Equipment> = ArrayList()

    var area_tags: ArrayList<SystemData.Area> = ArrayList()

    var movieDetails: MovieData? = null
    lateinit var movieDetailViewModel: MovieDetailViewModel

    lateinit var userProfileViewModel: UserProfileViewModel

    @Inject
    lateinit var upSellAdapter: GearOnRTSAdapter

    @Inject
    lateinit var referenceAdapter: RelatedVideoAdapter

    @Inject
    lateinit var navigator: Navigator

    var isOfflineMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        movieDetails = arguments?.getParcelable(MOVIE_DETAIL)
        arguments?.let { isOfflineMode = it.getBoolean(OFF_LINE) }
        movieDetailViewModel = viewModel(viewModelFactory) {
            observe(systemEquipment, ::onReceiveEquipmentSystem)
            observe(upsell, ::onReceiveUpsell)
            observe(referenceMovie, ::onReceiveReferenceMovie)
            failure(failureData, ::handleFailure)
        }

        userProfileViewModel = viewModel(viewModelFactory) {
            observe(systemArea, ::onReceiveAreaSystem)
            failure(failureData, ::handleFailure)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        referenceAdapter.setShowCoverView(!isAllowForFreemium(false))
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun onReceiveReferenceMovie(list: ArrayList<MovieData>?) {
        hideProgress()
        if (list == null) return
        //show only 3 videos
        if (list.size >= 3) {
            referenceAdapter.collection = list.take(3) as ArrayList<MovieData>
        } else {
            referenceAdapter.collection = list
        }
        referenceAdapter.setShowCoverView(!isAllowForFreemium(false))

        if (list.isNotEmpty()) may_as_you_like_title.visibility = View.VISIBLE
    }

    private fun onReceiveUpsell(data: UpsellEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (upsellCurrentPage == 1) upSellAdapter.collection.clear()
        upSellAdapter.collection.addAll(data.list_data)
        upSellAdapter.notifyDataSetChanged()
        if (data.list_data.isNotEmpty()) gear_on_rts_title.visibility = View.VISIBLE
        upsellCurrentPage = data.page
        upsellTotalPage = data.max_page
        upSellAdapter.onLoadmoreListener = upsellLoadmore
    }

    private fun onReceiveAreaSystem(list: List<SystemData.Area>?) {
        list?.let {
            area_tags.addAll(it)
            loadArea()
        }
    }

    private fun onReceiveEquipmentSystem(list: List<SystemData.Equipment>?) {
        list?.let {
            equipment_tags.addAll(it)
            loadEquipment()
        }
    }

    private fun loadEquipment() {
        if (movieDetails == null || equipment_tags.isEmpty()) return
        val movieTags = movieDetails!!.required_equipment_ids

        for (tag in equipment_tags) {
            if (movieTags.contains(tag.equipment_id)) {
                required_equipment_title.visibility = View.VISIBLE
                required_equipment.addView(activity?.let {
                    SearchTagView(it).apply {
                        setText(tag.equipment_title)
                        setCheck(isChecked = true, isEnabled = false)
                        if (required_equipment.childCount == 0) {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            ).apply {
                                setMargins(20.dp2px, 0.dp2px, 0.dp2px, 0.dp2px)
                                minimumWidth = 80.dp2px
                            }
                        }
                    }
                })
            }
        }
        if (coach_tips_title.visibility == View.VISIBLE || required_equipment_title.visibility == View.VISIBLE) expand_btn.visibility =
            View.VISIBLE
    }

    private fun loadArea() {
        if (movieDetails == null || area_tags.isEmpty()) return
        val areaIds = movieDetails!!.area_ids

        for (tag in area_tags) {
            if (areaIds.contains(tag.area_id)) {
                required_area_title.visibility = View.VISIBLE
                required_area.addView(activity?.let {
                    SearchTagView(it).apply {
                        setText(tag.area_title)
                        setCheck(isChecked = true, isEnabled = false)
                        if (required_area.childCount == 0) {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            ).apply {
                                setMargins(20.dp2px, 0.dp2px, 0.dp2px, 0.dp2px)
                                minimumWidth = 80.dp2px
                            }
                        }
                    }
                })
            }
        }

        if (required_area_title.visibility == View.VISIBLE) expand_btn.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (equipment_tags.isEmpty()) {
            loadData()
        } else {
            loadEquipment()
        }

        if (area_tags.isEmpty()) {
            userProfileViewModel.loadSystemArea()
        } else {
            loadArea()
        }
        loadMovieContent()
    }

    override fun onReloadData() {
        upsellTotalPage = 0
        upsellCurrentPage = 1
        loadData()
    }

    private fun loadMovieContent() {
        if (movieDetails == null) return
        video_title.text = movieDetails!!.video_title
        video_description.text = movieDetails!!.video_description
        if (TextUtils.isEmpty(movieDetails?.video_description)){
            video_description.gone()
        }
        if (movieDetails!!.coach_tips.isNotEmpty()) coach_tips_title.visibility = View.VISIBLE
        if (coach_tips_title.visibility == View.VISIBLE || required_equipment_title.visibility == View.VISIBLE) expand_btn.visibility =
            View.VISIBLE
        coach_tips.removeAllViews()
        for (tip in movieDetails!!.coach_tips) {
            val itemView = layoutInflater.inflate(R.layout.coach_tip_item, null)
            itemView.tip_content.text = tip
            coach_tips.addView(itemView)
        }
    }

    private fun loadData() {
        movieDetailViewModel.getSystemEquipment()
        if (movieDetails != null && !isOfflineMode) {
            showProgress()
            userDataCache.get()?.user_token?.userID?.let {
                movieDetailViewModel.getUpSell(Pair(it, PostUpSell.Params(movieDetails!!.video_id)))
                movieDetailViewModel.getReferenceMovie(
                    Pair(
                        it,
                        GetReferenceMovie.Params(movieDetails!!.video_id)
                    )
                )
            }
        }

    }

    private fun initView() {
        may_as_you_like_recyclerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        may_as_you_like_recyclerview.adapter =
            referenceAdapter.apply { onItemClickListener = this@MovieDetailFragment }

        gear_on_rts_recyclerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        gear_on_rts_recyclerview.adapter =
            upSellAdapter.apply { onItemClickListener = this@MovieDetailFragment }

        expand_btn.setOnClickListener {
            if (expandable_layout.isExpanded) {
                expand_btn.setImageResource(R.drawable.ic_arrow_drop_up_circle)
            } else {
                expand_btn.setImageResource(R.drawable.ic_arrow_drop_down_circle)
            }
            expandable_layout.isExpanded = !expandable_layout.isExpanded
        }
        expand_btn.extendTouch()
    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item != null) {
            when (item) {
                is MovieData -> {
                    if (!isAllowForFreemium()) {
                        return
                    }
                    (parentFragment as MoviePlayFragment).arguments?.putInt(MOVIE_ID, item.video_id)
                    (parentFragment as MoviePlayFragment).arguments?.putString(
                        MOVIE_THUMBNAIL,
                        item.image_thumbnail
                    )
                    (parentFragment as MoviePlayFragment).needInitViewChildFragment = true
                    (parentFragment as MoviePlayFragment).onReloadData()
                }
                is UpsellData -> {
                    navigator.showWebBrowser(activity, item.product_reference_url)
                }
            }
        }
    }

    private val upsellLoadmore: OnLoadmoreListener? = object : OnLoadmoreListener {
        override fun onLoadMore() {
            if (upsellCurrentPage >= upsellTotalPage) {
                upSellAdapter.onLoadmoreListener = null
                return
            }
            upsellCurrentPage++
            showProgress()
            movieDetailViewModel.getUpSell(
                Pair(
                    userID,
                    PostUpSell.Params(movieDetails!!.video_id).apply { page = upsellCurrentPage })
            )
        }
    }
}