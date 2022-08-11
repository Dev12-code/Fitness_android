package com.cbi.app.trs.features.fragments.workout_sport

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.extendTouch
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.cache.WorkoutFilterCache
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.workout.WorkoutEntity
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.workout.GetWorkOutWarmUp
import com.cbi.app.trs.features.fragments.movies.movies_detail.RelatedVideoAdapter
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.viewmodel.WorkoutViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_workout_sport.*
import kotlinx.android.synthetic.main.main_header_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class WorkoutSportFragment : DarkBaseFragment() {
    override fun layoutId() = R.layout.fragment_workout_sport

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var warmUpAdapter: RelatedVideoAdapter

    @Inject
    lateinit var workoutCategoryAdapter: WorkoutSportAdapter

    @Inject
    lateinit var sportCategoryAdapter: WorkoutSportAdapter

    @Inject
    lateinit var archetypeCategoryAdapter: WorkoutSportAdapter

    @Inject
    lateinit var workoutFilterCache: WorkoutFilterCache

    lateinit var workoutViewModel: WorkoutViewModel

    var needReloadData = true

    var param: PagingParam = PagingParam()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (needReloadData) {
            loadData()
            needReloadData = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        workoutViewModel = viewModel(viewModelFactory) {
            observe(categoryWorkoutData, ::onReceiveWorkoutCategory)
            observe(categorySportData, ::onReceiveSportCategory)
            observe(categoryArchetypeData, ::onReceiveArchetypeCategory)
            observe(workoutWarmUpData, ::onReceiveWarmUpData)
            failure(failureData, ::handleFailure)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        setBackgroundPremium()
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun setBackgroundPremium() {
        //set background premium
        sportCategoryAdapter.setShowCoverView(!isAllowForFreemium(false))
        workoutCategoryAdapter.setShowCoverView(!isAllowForFreemium(false))
        archetypeCategoryAdapter.setShowCoverView(!isAllowForFreemium(false))
    }

    override fun onReloadData() {
        loadData()
    }

    private fun onReceiveWarmUpData(data: WorkoutEntity.Data?) {
        forceHide()
        if (data == null) {
            return
        }
        warmUpAdapter.collection.clear()
        warmUpAdapter.collection.addAll(data.list_video)
        warmUpAdapter.notifyDataSetChanged()
    }

    private fun onReceiveWorkoutCategory(list: List<CategoryData>?) {
        forceHide()
        if (list == null) return
        workoutCategoryAdapter.collection = list
        workoutCategoryAdapter.setType(AppConstants.WORKOUTS)
    }

    private fun onReceiveSportCategory(list: List<CategoryData>?) {
        hideProgress()
        if (list == null) return
        sportCategoryAdapter.collection = list
    }

    private fun onReceiveArchetypeCategory(list: List<CategoryData>?) {
        hideProgress()
        if (list == null) return
        archetypeCategoryAdapter.collection = list
    }

    private fun loadData() {
        userDataCache.get()?.user_token?.userID?.let { userId ->
            try {
                forceShowProgress()
                workoutViewModel.getWarmUp(
                    userId,
                    GetWorkOutWarmUp.Params(1999).apply { limit = 3 })
                workoutViewModel.getCategoryWorkout(
                    userId,
                    param.apply { limit = 3 })
                workoutViewModel.getCategorySport(
                    userId,
                    param.apply { limit = 3 })
                workoutViewModel.getCategoryArchetype(
                    userId,
                    param.apply { limit = 3 })
            } catch (e: Exception) {
                forceHide()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun initView() {
        workout_recylerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        sport_recylerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        archetype_recylerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        warm_up_recylerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        warm_up_recylerview.adapter = warmUpAdapter.apply {
            onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item != null && item is MovieData) {
                        navigator.showMovieDetails(activity, item)
                    }
                }
            }
        }

        workout_recylerview.adapter = workoutCategoryAdapter.apply {
            onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item != null && item is CategoryData) {
                        val title = item.category_title
                        if (!title.contains("Squat")) {
                            if (!isAllowForFreemium()) return
                        }
                        navigator.showWorkoutSportDetail(activity, item, "workouts")
                    }
                }
            }
        }
        sport_recylerview.adapter = sportCategoryAdapter.apply {
            onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item != null && item is CategoryData) {
                        if (!isAllowForFreemium()) return
                        navigator.showWorkoutSportDetail(activity, item, "sports")
                    }
                }
            }
        }
        archetype_recylerview.adapter = archetypeCategoryAdapter.apply {
            onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item != null && item is CategoryData) {
                        if (!isAllowForFreemium()) return
                        navigator.showWorkoutSportDetail(activity, item, "archetypes")
                    }
                }
            }
        }

        workout_view_all.setOnClickListener {
            if (!isAllowForFreemium()) return@setOnClickListener
            navigator.showWorkoutSportViewall(activity, 0)
        }
        sport_view_all.setOnClickListener {
            if (!isAllowForFreemium()) return@setOnClickListener
            navigator.showWorkoutSportViewall(activity, 1)
        }
        archetype_view_all.setOnClickListener {
            if (!isAllowForFreemium()) return@setOnClickListener
            navigator.showWorkoutSportViewall(activity, 2)
        }

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

        user_icon.extendTouch()
        progress_icon.extendTouch()
        favourite_icon.extendTouch()
        workout_view_all.extendTouch()
        sport_view_all.extendTouch()
        archetype_view_all.extendTouch()
        home_icon.extendTouch()

        setBackgroundPremium()
    }
}