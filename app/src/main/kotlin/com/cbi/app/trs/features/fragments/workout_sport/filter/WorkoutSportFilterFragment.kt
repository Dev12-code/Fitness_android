package com.cbi.app.trs.features.fragments.workout_sport.filter

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.cache.WorkoutFilterCache
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.WorkoutFilterData
import com.cbi.app.trs.domain.eventbus.FilterUpdateEvent
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.search.SearchTagView
import com.cbi.app.trs.features.viewmodel.WorkoutFilterModel
import com.google.android.flexbox.FlexDirection
import kotlinx.android.synthetic.main.fragment_search.back_btn
import kotlinx.android.synthetic.main.fragment_search.clear_all
import kotlinx.android.synthetic.main.fragment_workout_sport_filter.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class WorkoutSportFilterFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId() = R.layout.fragment_workout_sport_filter

    val equipment_tags = ArrayList<SystemData.Equipment>()

    val focus_area_tags = ArrayList<SystemData.Area>()

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var workoutFilterCache: WorkoutFilterCache

    lateinit var workoutFilterModel: WorkoutFilterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        workoutFilterModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(systemArea, ::onReceiveSystemArea)
            observe(systemEquipment, ::onReceiveSystemEquipment)
        }
    }

    private fun onReceiveSystemEquipment(list: List<SystemData.Equipment>?) {
        if (list != null) {
            equipment_tags.addAll(list)
            loadEquipment()
        }
    }

    private fun onReceiveSystemArea(list: List<SystemData.Area>?) {
        if (list != null) {
            focus_area_tags.addAll(list)
            loadFocusArea()
        }
    }

    private fun loadEquipment() {
        equipment_tags_flexbox.removeAllViews()
        for (tag in equipment_tags) {
            equipment_tags_flexbox.addView(activity?.let {
                SearchTagView(it).apply {
                    setText(tag.equipment_title)
                    setTag(tag)
                    if (workoutFilterCache.get().equipment_ids.contains(tag.equipment_id)) {
                        setCheck(isChecked = true, isEnabled = true)
                    }
                    onItemClickListener = this@WorkoutSportFilterFragment
                }
            })
        }
    }

    private fun loadFocusArea() {
        focused_area_tags_flexbox.removeAllViews()
        for (tag in focus_area_tags) {
            focused_area_tags_flexbox.addView(activity?.let {
                SearchTagView(it).apply {
                    setText(tag.area_title)
                    setTag(tag)
                    if (workoutFilterCache.get().focus_areas.contains(tag.area_id)) {
                        setCheck(isChecked = true, isEnabled = true)
                    }
                    onItemClickListener = this@WorkoutSportFilterFragment
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadFilterFromCache()
    }

    override fun onReloadData() {
        loadFilterFromCache()
    }

    private fun loadFilterFromCache() {
        workoutFilterModel.loadSystemArea()
        workoutFilterModel.loadSystemEquipment()
        when (workoutFilterCache.get().duration) {
            0 -> {
                time_all.performClick()
            }
            1 -> {
                time_10_mins.performClick()
            }
            2 -> {
                time_20_mins.performClick()
            }
            3 -> {
                time_30_mins.performClick()
            }
        }
        when (workoutFilterCache.get().pre_post_filter) {
            1 -> pre_video_switch_button.isChecked = true
            2 -> post_video_switch_button.isChecked = true
            else -> {
                pre_video_switch_button.isChecked = false
                post_video_switch_button.isChecked = false
            }
        }
    }

    private fun initView() {
        equipment_tags_flexbox.flexDirection = FlexDirection.ROW
        equipment_tags_flexbox.clipChildren = false
        equipment_tags_flexbox.clipToPadding = false

        focused_area_tags_flexbox.flexDirection = FlexDirection.ROW
        focused_area_tags_flexbox.clipChildren = false
        focused_area_tags_flexbox.clipToPadding = false

        toggleTextView(time_all, false)
        toggleTextView(time_10_mins, false)
        toggleTextView(time_20_mins, false)
        toggleTextView(time_30_mins, false)

        clear_all.setOnClickListener {
            for (tag in 0 until equipment_tags_flexbox.childCount) {
                (equipment_tags_flexbox.getChildAt(tag) as SearchTagView).setCheck(false)
            }

            for (tag in 0 until focused_area_tags_flexbox.childCount) {
                (focused_area_tags_flexbox.getChildAt(tag) as SearchTagView).setCheck(false)
            }

            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, false)

            pre_video_switch_button.isChecked = false
            post_video_switch_button.isChecked = false
        }

        pre_video_switch_button.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) post_video_switch_button.isChecked = false
            }
        }

        post_video_switch_button.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) pre_video_switch_button.isChecked = false
            }
        }

        back_btn.setOnClickListener {
            pop(activity)
        }
        apply_btn.setOnClickListener {
            saveFilter()
            EventBus.getDefault().post(FilterUpdateEvent())
            close()
        }

        initDurationCategory()
    }

    private fun saveFilter() {
        val workoutFilterData = WorkoutFilterData()
        for (child in 0 until equipment_tags_flexbox.childCount) {
            val itemView = equipment_tags_flexbox.getChildAt(child) as SearchTagView
            if (itemView.isChecked) {
                workoutFilterData.equipment_ids.add((itemView.tag as SystemData.Equipment).equipment_id)
            }
        }

        for (child in 0 until focused_area_tags_flexbox.childCount) {
            val itemView = focused_area_tags_flexbox.getChildAt(child) as SearchTagView
            if (itemView.isChecked) {
                workoutFilterData.focus_areas.add((itemView.tag as SystemData.Area).area_id)
            }
        }
        when {
            time_all.isSelected -> workoutFilterData.duration = 0
            time_10_mins.isSelected -> workoutFilterData.duration = 1
            time_20_mins.isSelected -> workoutFilterData.duration = 2
            time_30_mins.isSelected -> workoutFilterData.duration = 3
            else -> workoutFilterData.duration = -1
        }

        when {
            pre_video_switch_button.isChecked -> workoutFilterData.pre_post_filter = 1
            post_video_switch_button.isChecked -> workoutFilterData.pre_post_filter = 2
            else -> {
                workoutFilterData.pre_post_filter = 0
            }
        }

        workoutFilterCache.put(workoutFilterData)
    }

    private fun initDurationCategory() {
        time_all.setOnClickListener {
            toggleTextView(time_all, true)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, false)
        }
        time_10_mins.setOnClickListener {
            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, true)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, false)
        }
        time_20_mins.setOnClickListener {
            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, true)
            toggleTextView(time_30_mins, false)
        }
        time_30_mins.setOnClickListener {
            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, true)
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onItemClick(item: Any?, position: Int) {
    }
}