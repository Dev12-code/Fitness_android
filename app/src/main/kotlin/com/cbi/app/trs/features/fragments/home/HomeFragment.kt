package com.cbi.app.trs.features.fragments.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.*
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.movie.GetFeatureMovie
import com.cbi.app.trs.domain.usecases.movie.PostUpSell
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.movies.movies_detail.GearOnRTSAdapter
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_pain_body.*
import kotlinx.android.synthetic.main.search_bar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.math.roundToInt

class HomeFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId() = R.layout.fragment_home

    private var mobilityStatus: MobilityStatus? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sharePreferences: SharedPreferences

    @Inject
    lateinit var homeBonusContentAdapter: HomeBonusContentAdapter

    @Inject
    lateinit var homeWorkoutSpotsAdapter: HomeWorkoutSpotsAdapter

    @Inject
    lateinit var upSellAdapter: GearOnRTSAdapter


    private lateinit var homeViewModel: HomeViewModel

    private lateinit var bannerVideo: MovieData

    private var upSellCurrentPage = 1
    private var upSellTotalPage = 0


    override fun onItemClick(item: Any?, position: Int) {
        if (item == null) {
            if (position == -1)
                (activity as MainActivity).selectNavigation(R.id.navigation_workout)
        } else {
            when (item) {
                is SystemData.Bonus -> {
                    if (!isAllowForFreemium()) return
                    navigator.showBonusDetail(activity, item)
                }
                is UpsellData -> {
                    navigator.showWebBrowser(activity, item.product_reference_url)
                }
                is HomeWorkoutSpotsData -> {
                    (activity as MainActivity).selectNavigation(R.id.navigation_workout)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        homeViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(mobilityStatus, ::onReceiveMobilityStatus)
            observe(systemBonus, ::onReceiveSystemBonus)
            observe(upSell, ::onReceiveUpSell)
            observe(featureMovie, ::onReceiveFeatureMovie)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        showProgress()
        loadData()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        changeBackground()
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onReloadData() {
        loadData()
    }

    private fun loadData() {
        //load mobility status
        if (mobilityStatus == null) {
            homeViewModel.getMobilityStatus(userID)
        } else {
            loadMobilityStatus(mobilityStatus)
        }
        //load system bonus
        homeViewModel.loadSystemBonus()
        //load TRS gear
        homeViewModel.getUpSell(Pair(userID, PostUpSell.Params(null).apply { page = upSellCurrentPage }))
        //load feature video
        homeViewModel.getFeatureMovie(Pair(userID, getFeatureParam()))
    }

    private fun initFeatureBanner() {
        feature_banner.setOnClickListener {
            if (::bannerVideo.isInitialized) {
                navigator.showMovieDetails(activity, bannerVideo)
            }
        }
    }

    private fun getFeatureParam(): GetFeatureMovie.Params {
        return GetFeatureMovie.Params(0.minToSecond(), 10000.minToSecond()).apply { limit = 5 }
    }

    private fun onReceiveFeatureMovie(list: List<MovieData>?) {
        hideProgress()
        if (list == null || list.isEmpty()) return
        loadFeatureBanner(list[0])
        bannerVideo = list[0]
    }

    private fun loadFeatureBanner(movieData: MovieData?) {
        if (movieData == null) {
            return
        }
        feature_banner.visibility = View.VISIBLE
        feature_banner_image.loadFromUrl(movieData.image_thumbnail, isPlaceHolder = true)
        feature_banner_title.text = movieData.video_title
        video_duration.text = "${movieData.video_duration / 60}"
    }

    private fun initView() {
        initFeatureBanner()
        search_edt.isFocusable = false
        search_edt.hint = getString(R.string.search_old_mobility_header)
        search_edt.setOnClickListener { if (isAllowForFreemium()) navigator.showSearch(activity) }


        workout_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        workout_recylerview.adapter = homeWorkoutSpotsAdapter.apply {
            onItemClickListener = this@HomeFragment
        }

        //init data workouts
        val workOuts = ArrayList<HomeWorkoutSpotsData>().apply {
            add(HomeWorkoutSpotsData(AppConstants.WORKOUTS, R.drawable.img_workouts, getString(R.string.by_workout)))
            add(HomeWorkoutSpotsData(AppConstants.SPORTS, R.drawable.img_sports, getString(R.string.by_sport)))
            add(HomeWorkoutSpotsData(AppConstants.ARCHETYPES, R.drawable.img_archetypes, getString(R.string.by_archetype)))
        }
        homeWorkoutSpotsAdapter.collection = workOuts

        bonus_content_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bonus_content_recylerview.adapter = homeBonusContentAdapter.apply { onItemClickListener = this@HomeFragment }

        gear_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        gear_recylerview.adapter = upSellAdapter.apply {
            onItemClickListener = this@HomeFragment
            onLoadmoreListener = upSellLoadmore
        }

        back_btn.extendTouch()
        back_btn.setOnClickListener {
            pop(activity)
        }

        full_front_img.setOnClickListener {
            (activity as MainActivity).selectNavigation(R.id.navigation_pain)
        }

        changeBackground()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun changeBackground() {
        context?.let {
            if (!isAllowForFreemium(false)) {
                mobility_btn.background = ContextCompat.getDrawable(it, R.drawable.common_premium_btn_selector)
                mobility_btn.setTextColor(ContextCompat.getColor(it, R.color.color_white))
            } else {
                mobility_btn.background = ContextCompat.getDrawable(it, R.drawable.common_btn_selector)
                mobility_btn.setTextColor(ContextCompat.getColor(it, R.color.color_white))
            }
        }
    }

    private fun onReceiveSystemBonus(list: List<SystemData.Bonus>?) {
        hideProgress()
        if (list == null) return
        homeBonusContentAdapter.collection = list
    }


    private fun onReceiveUpSell(data: UpsellEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (upSellCurrentPage == 1) upSellAdapter.collection.clear()
        upSellAdapter.collection.addAll(data.list_data)
        upSellAdapter.notifyDataSetChanged()
        upSellCurrentPage = data.page
        upSellTotalPage = data.max_page
        upSellAdapter.onLoadmoreListener = upSellLoadmore
    }


    private fun onReceiveMobilityStatus(mobilityStatus: MobilityStatus?) {
        hideProgress()
        if (mobilityStatus == null) return
        this.mobilityStatus = mobilityStatus
        loadMobilityStatus(mobilityStatus)
    }

    private fun loadMobilityStatus(mobilityStatus: MobilityStatus?) {
        if (mobilityStatus == null) return
        val trunkPercent = if (mobilityStatus.trunk_point_avg > 1) 100 else (mobilityStatus.trunk_point_avg * 100).roundToInt()
        val hipPercent = if (mobilityStatus.hip_point_avg > 1) 100 else (mobilityStatus.hip_point_avg * 100).roundToInt()
        val anklePercent = if (mobilityStatus.ankle_point_avg > 1) 100 else (mobilityStatus.ankle_point_avg * 100).roundToInt()
        val shoulderPercent = if (mobilityStatus.shoulder_point_avg > 1) 100 else (mobilityStatus.shoulder_point_avg * 100).roundToInt()

        trunk_progress.text = "${trunkPercent}%"
        circularProgressBar_trunk.progress = trunkPercent.toFloat()
        CommonUtils.setColorCode(trunkPercent, trunk_progress, circularProgressBar_trunk, mobilityStatus.on_process)

        hip_progress.text = "${hipPercent}%"
        circularProgressBar_hip.progress = hipPercent.toFloat()
        CommonUtils.setColorCode(hipPercent, hip_progress, circularProgressBar_hip, mobilityStatus.on_process)

        ankle_progress.text = "${anklePercent}%"
        circularProgressBar_ankle.progress = anklePercent.toFloat()
        CommonUtils.setColorCode(anklePercent, ankle_progress, circularProgressBar_ankle, mobilityStatus.on_process)

        shoulder_progress.text = "${shoulderPercent}%"
        circularProgressBar_shoulder.progress = (shoulderPercent).toFloat()
        CommonUtils.setColorCode(shoulderPercent, shoulder_progress, circularProgressBar_shoulder, mobilityStatus.on_process)

        if (mobilityStatus.test_date > 0) {
            //check 14 days from the last test
            //show text Go to My mobility plan
            mobility_btn.text = getString(R.string.go_to_my_mobility_plan)
            mobility_btn.setOnClickListener {
                if (!isAllowForFreemium()) return@setOnClickListener
                navigator.showMobilityPlan(activity)
            }
        } else {
            //show text Test My mobility
            mobility_btn.text = getString(R.string.take_the_mobility_test)
            mobility_btn.setOnClickListener {
                if (!isAllowForFreemium()) return@setOnClickListener
                (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
            }
        }
    }

    private val upSellLoadmore: OnLoadmoreListener? = object : OnLoadmoreListener {
        override fun onLoadMore() {
            if (upSellCurrentPage >= upSellTotalPage) {
                upSellAdapter.onLoadmoreListener = null
                return
            }
            upSellCurrentPage++
            showProgress()
            homeViewModel.getUpSell(Pair(userID, PostUpSell.Params(null).apply { page = upSellCurrentPage }))
        }

    }
}