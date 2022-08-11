package com.cbi.app.trs.features.fragments.workout_sport.view_all

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.cache.WorkoutFilterCache
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.viewmodel.WorkoutViewModel
import kotlinx.android.synthetic.main.workout_sport_viewall_fragment.*
import javax.inject.Inject

class WorkoutSportViewallFragment : LightBaseFragment(), OnItemClickListener {
    override fun layoutId() = R.layout.workout_sport_viewall_fragment

    private var categoryType = -1

    @Inject
    lateinit var workoutSportViewallAdapter: WorkoutSportViewallAdapter

    @Inject
    lateinit var navigator: Navigator

    lateinit var workoutViewModel: WorkoutViewModel

    @Inject
    lateinit var workoutFilterCache: WorkoutFilterCache

    var needReloadData = true

    var param: PagingParam = PagingParam()

    var collectionSlug: String? = null

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        EventBus.getDefault().unregister(this)
//    }
//
//    @Subscribe
//    fun onFilterUpdateEvent(event: FilterUpdateEvent) {
//        needReloadData = true
//    }

    companion object {
        const val CATEGORY_TYPE = "CATEGORY_TYPE"
    }

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
            failure(failureData, ::handleFailure)
            observe(categoryWorkoutData, ::onReceiveWorkoutCategory)
            observe(categorySportData, ::onReceiveSportCategory)
            observe(categoryArchetypeData, ::onReceiveArchetypeCategory)
            failure(failureData, ::handleFailure)
        }
        arguments?.let { categoryType = it.getInt(CATEGORY_TYPE, -1) }
    }


    private fun onReceiveWorkoutCategory(list: List<CategoryData>?) {
        hideProgress()
        if (list == null) return
        workoutSportViewallAdapter.collection = list
    }

    private fun onReceiveSportCategory(list: List<CategoryData>?) {
        hideProgress()
        if (list == null) return
        workoutSportViewallAdapter.collection = list
    }

    private fun onReceiveArchetypeCategory(list: List<CategoryData>?) {
        hideProgress()
        if (list == null) return
        workoutSportViewallAdapter.collection = list
    }

    private fun loadData() {
        when (categoryType) {
            0 -> {
                showProgress()
                workoutViewModel.getCategoryWorkout(userID, param.apply { limit = 0 })
            }
            1 -> {
                showProgress()
                workoutViewModel.getCategorySport(userID, param.apply { limit = 0 })
            }
            2 -> {
                showProgress()
                workoutViewModel.getCategoryArchetype(userID, param.apply { limit = 0 })
            }

        }
    }

    override fun onReloadData() {
        loadData()
    }

    private fun initView() {
        when (categoryType) {
            0 -> {
                view_all_title.text = getString(R.string.workouts)
                collectionSlug = "workouts"
            }
            1 -> {
                view_all_title.text = getString(R.string.sports)
                collectionSlug = "sports"
            }
            2 -> {
                view_all_title.text = getString(R.string.archetypes)
                collectionSlug = "archetypes"
            }

        }
        workout_view_all_recylerview.layoutManager = GridLayoutManager(activity, 2)
        workout_view_all_recylerview.adapter = workoutSportViewallAdapter.apply { onItemClickListener = this@WorkoutSportViewallFragment }
        workout_filter_btn.setOnClickListener { navigator.showWorkoutSportFilter(activity) }
        back_btn.setOnClickListener { pop(activity) }
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
        if (item != null && item is CategoryData){
            when (collectionSlug){
                "workouts" -> {
                    val title = item.category_title
                    if (!title.contains("Squat")) {
                        if (!isAllowForFreemium()) return
                    }
                    navigator.showWorkoutSportDetail(activity, item, collectionSlug)
                }
                else -> {
                    if (!isAllowForFreemium()) return
                    navigator.showWorkoutSportDetail(activity, item, collectionSlug)
                }
            }
        }
    }
}