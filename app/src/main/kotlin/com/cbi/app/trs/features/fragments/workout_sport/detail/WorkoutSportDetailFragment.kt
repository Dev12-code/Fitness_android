package com.cbi.app.trs.features.fragments.workout_sport.detail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.AlarmManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnItemPremiumClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.cache.WorkoutFilterCache
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.WorkoutFilterData
import com.cbi.app.trs.domain.entities.workout.RelatedByCategoryEntity
import com.cbi.app.trs.domain.entities.workout.SuggestByCategoryEntity
import com.cbi.app.trs.domain.eventbus.FilterUpdateEvent
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.workout.GetMovementGuide
import com.cbi.app.trs.domain.usecases.workout.GetSuggestByCategory
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.alarms.AlarmReceiver
import com.cbi.app.trs.features.fragments.movies.movies_detail.RelatedVideoAdapter
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.viewmodel.WorkoutViewModel
import kotlinx.android.synthetic.main.workout_sport_detail_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class WorkoutSportDetailFragment : DarkBaseFragment(), OnItemClickListener, OnItemPremiumClickListener {
    override fun layoutId() = R.layout.workout_sport_detail_fragment

    companion object {
        const val CATEGORY_DATA = "CATEGORY_DATA"
        const val COLLECTION_SLUG = "COLLECTION_SLUG"
    }

    var categoryData: CategoryData? = null

    var paramPreVideo: GetSuggestByCategory.Param = GetSuggestByCategory.Param(0)
    var paramPostVideo: GetSuggestByCategory.Param = GetSuggestByCategory.Param(0)
    var paramRelated: GetSuggestByCategory.Param = GetSuggestByCategory.Param(0)
    var paramMovementGuide: GetMovementGuide.Param = GetMovementGuide.Param(0)

    var currentPagePre = 1
    var totalPagePre = 0

    var currentPagePost = 1
    var totalPagePost = 0

    private val onSuggestLoadmore: OnLoadmoreListener = object : OnLoadmoreListener {
        override fun onLoadMore() {
            if (currentPagePre >= totalPagePre) {
                suggestByCategoryAdapter.onLoadmoreListener = null
                return
            }
            currentPagePre++
            showProgress()
            workoutViewModel.getSuggestByCategory(userID, paramPreVideo.apply { page = currentPagePre })
        }
    }

    var currentPageRelate = 1
    var totalPageRelate = 0
    private val onRelatedLoadmore: OnLoadmoreListener = object : OnLoadmoreListener {
        override fun onLoadMore() {
            if (currentPageRelate >= totalPageRelate) {
                relatedByCategoryAdapter.onLoadmoreListener = null
                return
            }
            currentPageRelate++
            showProgress()
            workoutViewModel.getRelatedByCategory(userID, paramRelated.apply { page = currentPageRelate })
        }
    }

    var topItem: MovieData? = null

    var movementGuideData: MovieData? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var suggestByCategoryAdapter: RelatedVideoAdapter

    @Inject
    lateinit var suggestPostByCategoryAdapter: RelatedVideoAdapter

    @Inject
    lateinit var relatedByCategoryAdapter: WorkoutsRelatedVideo

    @Inject
    lateinit var workoutFilterCache: WorkoutFilterCache

    lateinit var workoutViewModel: WorkoutViewModel

    var needReloadData = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        relatedByCategoryAdapter.setShowCoverView(!isAllowForFreemium(false))
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    @Subscribe
    fun onFilterUpdateEvent(event: FilterUpdateEvent) {
        needReloadData = true
        suggestByCategoryAdapter.collection = ArrayList()
        relatedByCategoryAdapter.collection = ArrayList()
        suggestPostByCategoryAdapter.collection = ArrayList()

        currentPageRelate = 1
        currentPagePre = 1
        currentPagePost = 1

        paramPreVideo.page = 1
        paramRelated.page = 1
        paramPostVideo.page = 1

        totalPageRelate = 0
        totalPagePre = 0
        totalPagePost = 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadDataForTopItem()
        if (needReloadData) {
            loadParameter()
            clearTopItem()
            loadData()
            needReloadData = false
        }

        //check to show title
        if (suggestPostByCategoryAdapter.collection.isNullOrEmpty()){
            workout_detail_post_title.gone()
        }

        if (relatedByCategoryAdapter.collection.isNullOrEmpty()){
            workout_detail_relate_title.gone()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        categoryData = arguments?.getParcelable(CATEGORY_DATA)
        paramPreVideo.collection_slug = arguments?.getString(COLLECTION_SLUG)
        paramPostVideo.collection_slug = arguments?.getString(COLLECTION_SLUG)
        paramRelated.collection_slug = arguments?.getString(COLLECTION_SLUG)
        workoutViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(suggestByCategoryData, ::onReceiveSuggestPreData)
            observe(suggestPostByCategoryData, ::onReceiveSuggestPostData)
            observe(relatedByCategoryData, ::onReceiveRelatedData)
            observe(movementGuideData, ::onReceiveMovementGuideData)
        }
    }

    private fun loadParameter() {
        categoryData?.let {
            paramPreVideo.category_id = it.category_id
            paramPostVideo.category_id = it.category_id
            paramRelated.category_id = it.category_id
            paramMovementGuide.category_id = it.category_id
        }
        paramPreVideo.equipment_ids = workoutFilterCache.get().equipment_ids
        paramPostVideo.equipment_ids = workoutFilterCache.get().equipment_ids
        paramRelated.equipment_ids = workoutFilterCache.get().equipment_ids

        paramPreVideo.focus_areas = workoutFilterCache.get().focus_areas
        paramPostVideo.focus_areas = workoutFilterCache.get().focus_areas
        paramRelated.focus_areas = workoutFilterCache.get().focus_areas

        //for suggest pre video, set filter  =1
        paramPreVideo.pre_post_filter = 1
        //for suggest post video, set filter = 2
        paramPostVideo.pre_post_filter = 2

        paramRelated.pre_post_filter = 0

        when (workoutFilterCache.get().duration) {
            0 -> {
                paramPreVideo.min_duration = 0
                paramPreVideo.max_duration = 10000.minToSecond()

                paramPostVideo.min_duration = 0
                paramPostVideo.max_duration = 10000.minToSecond()

                paramRelated.min_duration = 0
                paramRelated.max_duration = 10000.minToSecond()
            }
            1 -> {
                paramPreVideo.min_duration = 10.minToSecond()
                paramPreVideo.max_duration = 20.minToSecond()

                paramPostVideo.min_duration = 10.minToSecond()
                paramPostVideo.max_duration = 20.minToSecond()

                paramRelated.min_duration = 10.minToSecond()
                paramRelated.max_duration = 20.minToSecond()
            }
            2 -> {
                paramPreVideo.min_duration = 20.minToSecond()
                paramPreVideo.max_duration = 30.minToSecond()

                paramPostVideo.min_duration = 20.minToSecond()
                paramPostVideo.max_duration = 30.minToSecond()

                paramRelated.min_duration = 20.minToSecond()
                paramRelated.max_duration = 30.minToSecond()

            }
            3 -> {
                paramPreVideo.min_duration = 30.minToSecond()
                paramPreVideo.max_duration = 10000.minToSecond()

                paramPostVideo.min_duration = 30.minToSecond()
                paramPostVideo.max_duration = 10000.minToSecond()

                paramRelated.min_duration = 30.minToSecond()
                paramRelated.max_duration = 10000.minToSecond()
            }
        }
    }

    private fun onReceiveMovementGuideData(data: MovieData?) {
        hideProgress()
        if (data != null && data.video_id != 0){
            movement_guide.visible()
            iv_movement.visible()
        }
        movementGuideData = data
    }

    private fun onReceiveRelatedData(data: RelatedByCategoryEntity.Data?) {
        hideProgress()
        if (data?.post_related_videos == null) {
            workout_detail_relate_title.visibility = View.GONE
            return
        }
        workout_detail_relate_title.visible()
        currentPageRelate = data.page
        totalPageRelate = data.max_page
        if (currentPageRelate == 1) {
            relatedByCategoryAdapter.collection.clear()
        }
        if (data.post_related_videos.isNotEmpty()) workout_detail_relate_title.visibility = View.VISIBLE
        relatedByCategoryAdapter.collection.addAll(data.post_related_videos)
        //do filter post video
        val keysOfPost = suggestPostByCategoryAdapter.collection.map { it.video_id }
        relatedByCategoryAdapter.collection.removeAll { it.video_id in keysOfPost }

        //do filter pre video
        topItem?.let {
            val keysOfPre = arrayListOf(it.video_id)
            relatedByCategoryAdapter.collection.removeAll { it.video_id in keysOfPre }
        }

        if (relatedByCategoryAdapter.collection.isNullOrEmpty()) {
            workout_detail_relate_title.visibility = View.GONE
        } else {
            workout_detail_relate_title.visibility = View.VISIBLE
        }
        relatedByCategoryAdapter.notifyDataSetChanged()
        relatedByCategoryAdapter.setShowCoverView(!isAllowForFreemium(false))
        //remove load more
        relatedByCategoryAdapter.onLoadmoreListener = onRelatedLoadmore
    }

    private fun onReceiveSuggestPreData(data: SuggestByCategoryEntity.Data?) {
        hideProgress()
        if (data?.suggestion_videos == null) {
            tv_pre_workouts_video.visibility = View.GONE
            //hide topview
            top_view.visibility = View.GONE
            return
        }
        currentPagePre = data.page
        totalPagePre = data.max_page
        if (currentPagePre == 1) {
            suggestByCategoryAdapter.collection.clear()
            //hide top item
            if ((data.suggestion_videos.isNotEmpty())) {
                //show top view
                top_view.visibility = View.VISIBLE
                topItem = data.suggestion_videos[0]
                data.suggestion_videos.removeAt(0)
                loadDataForTopItem()
            }
        }
        suggestByCategoryAdapter.collection.addAll(data.suggestion_videos)
        suggestByCategoryAdapter.notifyDataSetChanged()
        //remove load more
//        suggestByCategoryAdapter.onLoadmoreListener = onSuggestLoadmore
    }

    private fun onReceiveSuggestPostData(data: SuggestByCategoryEntity.Data?) {
        //call API related
        workoutViewModel.getRelatedByCategory(userID, paramRelated)

        hideProgress()
        if (data?.suggestion_videos == null) {
            workout_detail_post_title.visibility = View.GONE
            return
        }
        currentPagePost = data.page
        totalPagePost = data.max_page
        if (currentPagePost == 1) {
            suggestPostByCategoryAdapter.collection.clear()
        }
        suggestPostByCategoryAdapter.collection.addAll(data.suggestion_videos)
        suggestPostByCategoryAdapter.notifyDataSetChanged()
        workout_detail_post_title.visible()
        //remove load more
//        suggestByCategoryAdapter.onLoadmoreListener = onSuggestLoadmore
    }

    private fun loadDataForTopItem() {
        topItem?.let { it1 ->
            suggest_video_image.loadFromUrl(it1.image_thumbnail, isPlaceHolder = true)
            suggest_video_image.setOnClickListener {
                setReminderForVideo(it1)
                navigator.showMovieDetails(activity, it1)
            }

            suggest_video_title.text = it1.video_title
            suggest_video_duration.text = "${it1.video_duration / 60}"
//            suggest_video_count.text = "${it1.view_count}"
        }
        if (relatedByCategoryAdapter.collection.isNotEmpty()) workout_detail_relate_title.visibility = View.VISIBLE
    }

    private fun clearTopItem() {
        topItem = null
        suggest_video_image.loadFromUrl("", isPlaceHolder = true)
        suggest_video_image.setOnClickListener { }

        suggest_video_title.text = ""
        suggest_video_duration.text = "-"
//        suggest_video_count.text = "-"
    }

    private fun loadData() {
        showProgress()
        //set limit = 3
        paramPreVideo.limit = 3
        paramPostVideo.limit = 3
        paramRelated.limit = 10;

        workoutViewModel.getSuggestByCategory(userID, paramPreVideo)
        workoutViewModel.getSuggestPostByCategory(userID, paramPostVideo)
        //get movement guide
        workoutViewModel.getMovementGuide(userID, paramMovementGuide)
    }

    override fun onReloadData() {
        loadParameter()
        currentPageRelate = 1
        currentPagePre = 1
        currentPagePost = 1

        paramPreVideo.page = 1
        paramRelated.page = 1
        paramPostVideo.page = 1

        totalPageRelate = 0
        totalPagePre = 0
        totalPagePost = 0

        clearTopItem()
        loadData()
    }

    private fun initView() {
//        workout_detail_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        workout_detail_post_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        workout_detail_relate_recylerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

//        workout_detail_recylerview.adapter = suggestByCategoryAdapter.apply {
//            onItemClickListener = this@WorkoutSportDetailFragment
//            //remove load more
//            onLoadmoreListener = onSuggestLoadmore
//            isShowPrePost = true
//        }

        workout_detail_post_recylerview.adapter = suggestPostByCategoryAdapter.apply {
            onItemClickListener = this@WorkoutSportDetailFragment
            //remove load more
//            onLoadmoreListener = onSuggestLoadmore
            isShowPrePost = true
        }

        workout_detail_relate_recylerview.adapter = relatedByCategoryAdapter.apply {
            onItemClickListener = this@WorkoutSportDetailFragment

            onLoadmoreListener = onRelatedLoadmore
            isShowPrePost = true
        }

        workout_filter_btn.setOnClickListener { navigator.showWorkoutSportFilter(activity) }

        category_title.text = "${categoryData?.category_title}"

        back_btn.setOnClickListener {
            workoutFilterCache.put(WorkoutFilterData())
            pop(activity)
        }

        iv_movement.extendTouch()
        iv_movement.setOnClickListener {
            movementGuideData?.let {
                navigator.showMovieDetails(activity, it)
            }
        }

        movement_guide.extendTouch()
        movement_guide.setOnClickListener {
            movementGuideData?.let {
                navigator.showMovieDetails(activity, it)
            }
        }
    }

    private fun setReminderForVideo(item: MovieData) {
        context?.let {
            val alarmManager = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notifyIntent = Intent(it, AlarmReceiver::class.java)
            notifyIntent.putExtra("type", AppConstants.REMINDER_WATCH_POST_VIDEO)
            val notifyPendingIntent = PendingIntent.getBroadcast(
                    it,
                    AppConstants.REQUEST_CODE_REMINDER_WATCH_POST,
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            when (item.pre_post_type) {
                1 -> {
                    // Alarm time in 1 hour
                    val DELAY_IN_SECOND = 60 * 60

                    val triggerTime = System.currentTimeMillis() + DELAY_IN_SECOND * 1_000L
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, notifyPendingIntent)
                    } else {
                        AlarmManagerCompat.setExactAndAllowWhileIdle(
                                alarmManager,
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                notifyPendingIntent
                        )
                    }
                }
                else -> {
                    //remove all alarm
                    alarmManager.cancel(notifyPendingIntent)
                }
            }
        }
    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item != null && item is MovieData) {
            setReminderForVideo(item)
            navigator.showMovieDetails(activity, item)
        }
    }

    override fun onItemPremiumClick(item: Any?, position: Int) {
        if (item != null && item is MovieData) {
            if (!isAllowForFreemium()) {
                return
            }
            setReminderForVideo(item)
            navigator.showMovieDetails(activity, item)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        workoutFilterCache.put(WorkoutFilterData())
        super.onDestroy()
    }


    override fun onBackPressed(): Boolean {
        workoutFilterCache.put(WorkoutFilterData())
        return super.onBackPressed()
    }
}