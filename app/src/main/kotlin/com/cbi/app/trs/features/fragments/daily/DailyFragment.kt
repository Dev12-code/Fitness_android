package com.cbi.app.trs.features.fragments.daily

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.cache.SystemDataCache
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.UpsellData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.entities.workout.WorkoutEntity
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.movie.GetFeatureMovie
import com.cbi.app.trs.domain.usecases.movie.GetSearchResult
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.movies.movies_detail.GearOnRTSAdapter
import com.cbi.app.trs.features.fragments.movies.movies_detail.RelatedVideoAdapter
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.viewmodel.DailyViewModel
import kotlinx.android.synthetic.main.fragment_daily.*
import kotlinx.android.synthetic.main.main_header_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class DailyFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId(): Int {
        return R.layout.fragment_daily
    }

    private var mobilityStatus: MobilityStatus? = null
    private var userId: Int? = 0

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var featureMovieAdapter: FeatureAdapter

    @Inject
    lateinit var newVideoAdapter: NewVideoAdapter

    @Inject
    lateinit var workoutAdapter: RelatedVideoAdapter

    @Inject
    lateinit var upSellAdapter: GearOnRTSAdapter

    @Inject
    lateinit var sharePreferences: SharedPreferences

    private lateinit var dailyViewModel: DailyViewModel

    lateinit var billingClient: BillingClient
    var isBillingConnected: Boolean = false

    private var featureType = 0
    private var workoutType = 0

    private var upsellCurrentPage = 1
    private var upsellTotalPage = 0

    private var bannerVideoData: MovieData? = null

    private val originalList = arrayListOf<MovieData>()

    private var equipmentList = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        dailyViewModel = viewModel(viewModelFactory) {
            observe(featureMovie, ::onReceiveFeatureMovie)
            observe(upSellBestSeller, ::onReceiveUpSellBestSeller)
            observe(purchaseTokenData, ::onReceiveSendPurchaseToken)
            observe(userProfile, ::onReceiveUserProfile)

            failure(failureData, ::handleFailure)
        }
        userId = userDataCache.get()?.user_token?.userID
        fragmentManager?.let { dailyViewModel.resumeDownloadVideo(it) }

        initBilling()
    }

    private fun initBilling() {
        billingClient = BillingClient.newBuilder(requireContext())
            .setListener { billingResult, mutableList -> }
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    AppLog.e("Duy", "onBillingInitialized")
                    isBillingConnected = true
                    paymentCache.get()?.purchase?.let {
                        if (userID == it.userId && isBillingConnected) {
                            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(it.purchaseToken)
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                            }
                            dailyViewModel.sendPurchaseToken(
                                userID,
                                PostPurchaseToken.Params(it.purchaseProductId, it.purchaseToken)
                            )
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        showProgress()
        changeBackground()
        loadFeatureMovie()
        featureMovieAdapter.setShowCoverView(!isAllowForFreemium(false))
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun onReceiveUserProfile(userProfile: UserData.UserProfile?) {
        forceHide()
        //send event bus to refresh adapter
        EventBus.getDefault().postSticky(PaymentPurchasedEvent())
    }

    private fun onReceiveSendPurchaseToken(baseEntities: BaseEntities?) {
        hideProgress()
        if (baseEntities == null) return
        if (baseEntities.isSuccess) {
            paymentCache.update(null)
            DialogAlert()
                .setTitle("Payment Successful !")
                .setMessage("Thank you! Payment received.")
                .setCancel(false)
                .setTitlePositive("OK")
                .show(activity)
            dailyViewModel.getUserProfile(userID)
        } else {
            DialogAlert()
                .setTitle("Sorry, Payment Failed!")
                .setMessage("Payment failed. Please try another payment method")
                .setCancel(false)
                .setTitlePositive("OK")
                .show(activity)
        }
    }

    private fun onReceiveUpSellBestSeller(data: UpsellEntity.Data?) {
        forceHide()
        if (data == null) return
        if (upsellCurrentPage == 1) upSellAdapter.collection.clear()
        upSellAdapter.collection.addAll(data.list_data)
        upSellAdapter.notifyDataSetChanged()
        upsellCurrentPage = data.page
        upsellTotalPage = data.max_page
        upSellAdapter.onLoadmoreListener = upsellLoadmore
    }

    private fun onReceiveWorkoutData(data: WorkoutEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (workoutCurrentPage == 1) workoutAdapter.collection.clear()
        workoutAdapter.collection.addAll(data.list_video)
        workoutAdapter.notifyDataSetChanged()
        workoutCurrentPage = data.page
        workoutTotalPage = data.max_page
        workoutAdapter.onLoadmoreListener = workoutLoadmore
    }

    private fun onReceiveMobilityStatus(mobilityStatus: MobilityStatus?) {
        this.mobilityStatus = mobilityStatus
        hideProgress()
    }

    private fun onReceiveFeatureMovie(list: List<MovieData>?) {
        forceHide()
        if (list == null || list.isEmpty()) return
        loadFeatureBanner(list[0])
        //add see more view
        val videoList: MutableList<MovieData> = list as MutableList<MovieData>
        videoList.add(MovieData.emptySeeMore())
        //get original list
        originalList.addAll(videoList)

        featureMovieAdapter.collection = originalList.minus(list[0])
        featureMovieAdapter.setShowCoverView(!isAllowForFreemium(false))
    }

    private fun loadFeatureBanner(movieData: MovieData?) {
        if (movieData == null) {
            return
        }
        this.bannerVideoData = movieData

        feature_banner.visibility = View.VISIBLE
        feature_banner_image.loadFromUrl(movieData.image_thumbnail, isPlaceHolder = true)
        feature_banner_title.text = movieData.video_title
        video_duration.text = "${movieData.video_duration / 60}"
    }

    private fun onReceiveNewMovie(newMovie: List<MovieData>?) {
        newVideoAdapter.collection = newMovie
        hideProgress()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        if (bannerVideoData == null) {
            showProgress()
            loadData()
        } else {
            if (featureMovieAdapter.collection != null) {
                loadFeatureBanner(originalList[0])
                featureMovieAdapter.collection = originalList.minus(originalList[0])
            }
        }

        //get equipment list
        userDataCache.get()?.user_profile?.user_settings?.my_equipments?.let {
            equipmentList.clear()
            for (id in it) {
                equipmentList.add(id.equipment_id)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        paymentCache.get()?.purchase?.let {
            if (userID == it.userId && isBillingConnected) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                }
                dailyViewModel.sendPurchaseToken(
                    userID,
                    PostPurchaseToken.Params(it.purchaseProductId, it.purchaseToken)
                )
            }
        }
    }

    private fun loadFeatureMovie() {
        userId?.let {
            dailyViewModel.getFeatureMovie(Pair(it, getFeatureParam()))
        }
    }

    private fun loadData() {
        userId?.let {
            loadFeatureMovie()
            dailyViewModel.getUpSellBestSeller(Pair(it, PagingParam()))
//            dailyViewModel.getNewMovie(it)
//            callWorkoutAPI()
//            dailyViewModel.getUpSell(Pair(it, PostUpSell.Params(null).apply { page = upsellCurrentPage }))
//            dailyViewModel.getMobilityStatus(it)
        }
    }

    private fun getFeatureParam(): GetFeatureMovie.Params {
        return GetFeatureMovie.Params(0.minToSecond(), 10000.minToSecond()).apply { limit = 1 }
            .apply {
                if (!isAllowForFreemium(false)) {
                    ro = "free"
                }
            }
    }

    override fun onReloadData() {
        workoutCurrentPage = 1
        workoutTotalPage = 0
        upsellCurrentPage = 1
        upsellTotalPage = 0
        newVideoAdapter.collection = ArrayList()
        featureMovieAdapter.collection = ArrayList()
        workoutAdapter.collection = ArrayList()
        upSellAdapter.collection = ArrayList()
        loadData()
    }

    private fun initView() {
        initFeatureBanner()
        initRelatedVideoRecylerView()
//        initFeatureCategory()
//        initWorkoutCategory()
//
//        initFeatureRecylerView()
//
//        initWorkoutRecylerView()
        initGearRecylerView()

//        search_edt.isFocusable = false
//        search_edt.setOnClickListener { if (isAllowForFreemium()) navigator.showSearch(activity) }
        progress_icon.setOnClickListener { if (isAllowForFreemium()) navigator.showProgress(activity) }
        user_icon.setOnClickListener { navigator.showSetting(activity) }
        favourite_icon.setOnClickListener {
            if (isAllowForFreemium()) navigator.showFavourite(
                activity
            )
        }
        home_icon.setOnClickListener {
            navigator.showHome(activity)
        }

        filter_btn.setOnClickListener {
            if (isAllowForFreemium()) navigator.showDailySearchAndFilter(activity)
        }

        user_icon.extendTouch()
        progress_icon.extendTouch()
        favourite_icon.extendTouch()
        home_icon.extendTouch()

        //change background filter
        changeBackground()

    }

    private fun changeBackground() {
        context?.let {
            if (!isAllowForFreemium(false)) {
                filter_btn.background =
                    ContextCompat.getDrawable(it, R.drawable.common_premium_btn_selector)
                filter_btn.setTextColor(ContextCompat.getColor(it, R.color.color_white))
            } else {
                filter_btn.background =
                    ContextCompat.getDrawable(it, R.drawable.common_btn_selector)
                filter_btn.setTextColor(ContextCompat.getColor(it, R.color.color_white))
            }
        }
    }

    private fun initFeatureBanner() {
        feature_banner.setOnClickListener {
            this.bannerVideoData?.let { banner ->
                navigator.showMovieDetails(activity, banner)
            }
        }
    }

//    private fun initFeatureCategory() {
//        toggleTextView(time_all, false)
//        toggleTextView(time_10_mins, false)
//        toggleTextView(time_20_mins, false)
//        toggleTextView(time_30_mins, false)
//
//        if (!isAllowForFreemium(false)) featureType = 1
//
//        when (featureType) {
//            0 -> {
//                time_all.isSelected = true
//                time_all.typeface = Typeface.DEFAULT_BOLD
//            }
//            1 -> {
//                time_10_mins.isSelected = true
//                time_10_mins.typeface = Typeface.DEFAULT_BOLD
//            }
//            2 -> {
//                time_20_mins.isSelected = true
//                time_20_mins.typeface = Typeface.DEFAULT_BOLD
//
//            }
//            3 -> {
//                time_30_mins.isSelected = true
//                time_30_mins.typeface = Typeface.DEFAULT_BOLD
//
//            }
//        }
//        time_all.setOnClickListener {
//            if (!isAllowForFreemium(true)) return@setOnClickListener
//            featureType = 0
//            toggleTextView(time_all, true)
//            toggleTextView(time_10_mins, false)
//            toggleTextView(time_20_mins, false)
//            toggleTextView(time_30_mins, false)
//            userId?.let {
//                showProgress()
//                dailyViewModel.getFeatureMovie(Pair(it, getFeatureParam()))
//            }
//        }
//        time_10_mins.setOnClickListener {
//            featureType = 1
//            toggleTextView(time_all, false)
//            toggleTextView(time_10_mins, true)
//            toggleTextView(time_20_mins, false)
//            toggleTextView(time_30_mins, false)
//            userId?.let {
//                showProgress()
//                dailyViewModel.getFeatureMovie(Pair(it, getFeatureParam()))
//            }
//        }
//        time_20_mins.setOnClickListener {
//            if (!isAllowForFreemium(true)) return@setOnClickListener
//            featureType = 2
//            toggleTextView(time_all, false)
//            toggleTextView(time_10_mins, false)
//            toggleTextView(time_20_mins, true)
//            toggleTextView(time_30_mins, false)
//            userId?.let {
//                showProgress()
//                dailyViewModel.getFeatureMovie(Pair(it, getFeatureParam()))
//            }
//        }
//        time_30_mins.setOnClickListener {
//            if (!isAllowForFreemium(true)) return@setOnClickListener
//            featureType = 3
//            toggleTextView(time_all, false)
//            toggleTextView(time_10_mins, false)
//            toggleTextView(time_20_mins, false)
//            toggleTextView(time_30_mins, true)
//            userId?.let {
//                showProgress()
//                dailyViewModel.getFeatureMovie(Pair(it, getFeatureParam()))
//            }
//        }
//    }

//    private fun initWorkoutCategory() {
//        toggleTextView(workout_equipment, false)
//        toggleTextView(workout_pre_post, false)
//        toggleTextView(workout_sport, false)
//        toggleTextView(workout_archetype, false)
//
//        if (!isAllowForFreemium(false)) workoutType = 1
//
//        when (workoutType) {
//            0 -> {
//                workout_equipment.isSelected = true
//                workout_equipment.typeface = Typeface.DEFAULT_BOLD
//            }
//            1 -> {
//                workout_pre_post.isSelected = true
//                workout_pre_post.typeface = Typeface.DEFAULT_BOLD
//            }
//            2 -> {
//                workout_sport.isSelected = true
//                workout_sport.typeface = Typeface.DEFAULT_BOLD
//            }
//            3 -> {
//                workout_archetype.isSelected = true
//                workout_archetype.typeface = Typeface.DEFAULT_BOLD
//            }
//        }
//
//        workout_equipment.setOnClickListener {
//            if (!isAllowForFreemium(true)) return@setOnClickListener
//            workoutCurrentPage = 1
//            workoutTotalPage = 0
//            toggleTextView(workout_equipment, true)
//            toggleTextView(workout_pre_post, false)
//            toggleTextView(workout_sport, false)
//            toggleTextView(workout_archetype, false)
//            workoutType = 0
//            callWorkoutAPI()
//        }
//        workout_pre_post.setOnClickListener {
//            workoutCurrentPage = 1
//            workoutTotalPage = 0
//            toggleTextView(workout_equipment, false)
//            toggleTextView(workout_pre_post, true)
//            toggleTextView(workout_sport, false)
//            toggleTextView(workout_archetype, false)
//            workoutType = 1
//            callWorkoutAPI()
//        }
//        workout_sport.setOnClickListener {
//            if (!isAllowForFreemium(true)) return@setOnClickListener
//            workoutCurrentPage = 1
//            workoutTotalPage = 0
//            toggleTextView(workout_equipment, false)
//            toggleTextView(workout_pre_post, false)
//            toggleTextView(workout_sport, true)
//            toggleTextView(workout_archetype, false)
//            workoutType = 2
//            callWorkoutAPI()
//        }
//        workout_archetype.setOnClickListener {
//            if (!isAllowForFreemium(true)) return@setOnClickListener
//            workoutCurrentPage = 1
//            workoutTotalPage = 0
//            toggleTextView(workout_equipment, false)
//            toggleTextView(workout_pre_post, false)
//            toggleTextView(workout_sport, false)
//            toggleTextView(workout_archetype, true)
//            workoutType = 3
//            callWorkoutAPI()
//        }
//    }

    private fun callWorkoutAPI() {
        when (workoutType) {
            0 -> {
                userId?.let {
                    showProgress()
                    dailyViewModel.getWorkoutEquipment(
                        it,
                        PagingParam().apply { page = workoutCurrentPage })
                }
            }
            1 -> {
                userId?.let {
                    showProgress()
                    dailyViewModel.getWorkoutPrePost(
                        it,
                        PagingParam().apply { page = workoutCurrentPage })
                }
            }
            2 -> {
                userId?.let {
                    showProgress()
                    dailyViewModel.getWorkoutSport(
                        it,
                        PagingParam().apply { page = workoutCurrentPage })
                }
            }
            3 -> {
                userId?.let {
                    showProgress()
                    dailyViewModel.getWorkoutArchetype(
                        it,
                        PagingParam().apply { page = workoutCurrentPage })
                }
            }
        }
    }

    private fun toggleTextView(txv: TextView, selected: Boolean) {
        txv.isSelected = selected
        when (selected) {
            true -> {
                txv.typeface = Typeface.DEFAULT_BOLD
            }
            false -> {
                txv.typeface = Typeface.DEFAULT
            }
        }
    }

//    private fun initFeatureRecylerView() {
//        feature_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        feature_recylerview.adapter = featureMovieAdapter.apply { onItemClickListener = this@DailyFragment }
//    }

    private fun initRelatedVideoRecylerView() {
        related_video_recylerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        related_video_recylerview.adapter =
            featureMovieAdapter.apply { onItemClickListener = this@DailyFragment }
    }

    private fun initGearRecylerView() {
        gear_recylerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        gear_recylerview.adapter = upSellAdapter.apply {
            onItemClickListener = this@DailyFragment
            onLoadmoreListener = upsellLoadmore
        }
    }

//    private fun initWorkoutRecylerView() {
//        workout_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        workout_recylerview.adapter = workoutAdapter.apply {
//            onItemClickListener = this@DailyFragment
//            onLoadmoreListener = workoutLoadmore
//        }
//    }

    private var workoutCurrentPage = 1
    private var workoutTotalPage = 0

    private val workoutLoadmore: OnLoadmoreListener? = object : OnLoadmoreListener {
        override fun onLoadMore() {
            if (workoutCurrentPage >= workoutTotalPage) {
                workoutAdapter.onLoadmoreListener = null
                return
            }
            workoutCurrentPage++
            callWorkoutAPI()
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
            dailyViewModel.getUpSellBestSeller(
                Pair(
                    userID,
                    PagingParam().apply { page = upsellCurrentPage })
            )
        }

    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item == null) {
            when (position) {
                AppConstants.VIDEO_SEE_MORE_ID -> {
                    //show daily maintenance
                    if (isAllowForFreemium()) {
                        navigator.showDailySeeMore(
                            activity,
                            GetSearchResult.Params(null, "", equipmentList, null, 0, 600000)
                        )
                    }
                }
                -1 -> {
                    (activity as MainActivity).selectNavigation(R.id.navigation_workout)
                }
            }
        } else {
            when (item) {
                is MovieData -> {
                    if (isAllowForFreemium()) {
                        navigator.showMovieDetails(activity, item)
                    }
                }
                is UpsellData -> {
                    navigator.showWebBrowser(activity, item.product_reference_url)
                }

            }
        }
    }
}