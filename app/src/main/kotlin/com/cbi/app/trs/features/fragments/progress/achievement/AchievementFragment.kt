package com.cbi.app.trs.features.fragments.progress.achievement

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.cache.AchievementBadgeCache
import com.cbi.app.trs.domain.entities.activity.AchievementEntity
import com.cbi.app.trs.features.viewmodel.ActivityViewModel
import kotlinx.android.synthetic.main.fragment_achievement.*
import javax.inject.Inject

class AchievementFragment : BaseFragment() {
    override fun layoutId() = R.layout.fragment_achievement

    @Inject
    lateinit var adapter: AchievementAdapter
    lateinit var activityViewModel: ActivityViewModel

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var achievementBadgeCache: AchievementBadgeCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        activityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(achievementData, ::onReceiveAchievement)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
    }

    private fun loadData() {
        showProgress()
        activityViewModel.achievement(userID)
    }

    override fun onReloadData() {
        loadData()
    }

    private fun onReceiveAchievement(list: AchievementEntity.Data?) {
        hideProgress()
        if (list?.user_archiverments == null) {
            empty_text_1.text = getString(R.string.more_points_more_fun)
            empty_text_2.text = getString(R.string.more_points_more_fun_message)
            //show button OK
            btnOk.visibility = View.VISIBLE
            return
        }
        adapter.collection = list.user_archiverments
        //showing your streak day
        var yourStreaksMessage = String.format(getString(R.string.your_streak_day), list.user_streak_point)
        if (list.user_streak_point <= 1) {
            yourStreaksMessage = yourStreaksMessage.replace("Days", "Day")
        }
        tv_your_streaks.text = yourStreaksMessage
        //get first badge inactive
        val badge = list.user_archiverments.filter { !it.achievement_is_active }.minBy { it.achievement_milestone }
        badge?.let {
            tv_your_streaks_message.text = String.format(getString(R.string.your_streak_message), it.achievement_milestone)
            iv_badge.loadFromUrl(it.achievement_active_image, isAnimation = false, isPlaceHolder = false)

            val progress = (list.user_streak_point.toDouble() / it.achievement_milestone.toDouble()) * 100
            progress_badge.progress = progress.toFloat()

            //change background
            var background = R.drawable.streaks_background
            when (it.achievement_title) {
                "Cougar" -> {
                    background = R.drawable.cougar_bg
                }
                "Cheetah" -> {
                    background = R.drawable.cheetah
                }
                "Tiger" -> {
                    background = R.drawable.streaks_background
                }
                "Jaguar" -> {
                    background = R.drawable.jaguar
                }
                "Lion" -> {
                    background = R.drawable.lion
                }
                "Panther" -> {
                    background = R.drawable.panther
                }
                "Leopard" -> {
                    background = R.drawable.leopard
                }
            }
            //load image
            iv_background.loadFromLocal(background, isAnimation = false, isPlaceHolder = false)

        }
        if (adapter.collection.isEmpty()) {
            empty_text_1.text = getString(R.string.more_points_more_fun)
            empty_text_2.text = getString(R.string.more_points_more_fun_message)
            //show button OK
            btnOk.visibility = View.VISIBLE
        } else {
            empty_text_1.text = ""
            empty_text_2.text = ""
            //show button OK
            btnOk.visibility = View.GONE
        }

        //compare old list and new list to show congrat views
        val cacheList = achievementBadgeCache.get()?.user_archiverments
        if (cacheList == null || cacheList.isEmpty()) {
            //don't have any cache data, do find any active badge
            val badge = list.user_archiverments.filter { it.achievement_is_active }.maxBy { it.achievement_milestone }
            badge?.let {
                navigator.showAchievementCongratulation(activity, badge)
            }
        } else {
            //compare cache list and new list
            val lastCacheBadgeActive = cacheList.filter { it.achievement_is_active }.maxBy { it.achievement_milestone }
            val lastServerBadgeActive = list.user_archiverments.filter { it.achievement_is_active }.maxBy { it.achievement_milestone }

            //case 1: cache empty and server have
            if (lastCacheBadgeActive == null && lastServerBadgeActive != null) {
                navigator.showAchievementCongratulation(activity, lastServerBadgeActive)
            }

            //case 2: both not empty, check milestone
            if (lastCacheBadgeActive != null && lastServerBadgeActive != null) {
                if (lastServerBadgeActive.achievement_milestone > lastCacheBadgeActive.achievement_milestone) {
                    navigator.showAchievementCongratulation(activity, lastServerBadgeActive)
                }
            }
        }

        achievementBadgeCache.put(list)
    }

    private fun initView() {
        achievement_recylerview.layoutManager = GridLayoutManager(activity, 3)
        achievement_recylerview.adapter = adapter

        //event click button OK
        btnOk.setOnClickListener {
            pop(activity)
        }
    }
}
