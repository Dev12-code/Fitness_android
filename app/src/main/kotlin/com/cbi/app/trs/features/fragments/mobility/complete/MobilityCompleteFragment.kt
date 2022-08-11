package com.cbi.app.trs.features.fragments.mobility.complete

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.domain.eventbus.CompleteTestEvent
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.viewmodel.MobilityViewModel
import kotlinx.android.synthetic.main.fragment_mobility_complete.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.math.roundToInt

class MobilityCompleteFragment : DarkBaseFragment(), View.OnClickListener {
    override fun layoutId() = R.layout.fragment_mobility_complete


    lateinit var mobilityViewModel: MobilityViewModel

    private var mobilityStatus: MobilityStatus? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        mobilityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(mobilityStatus, ::onReceiveMobilityStatus)
        }
    }

    private fun onReceiveMobilityStatus(mobilityStatus: MobilityStatus?) {
        hideProgress()
        if (mobilityStatus == null) return
        this.mobilityStatus = mobilityStatus
        loadMobilityStatus(mobilityStatus)
    }

    private fun loadMobilityStatus(mobilityStatus: MobilityStatus) {
        trunk_progress.text = "${(mobilityStatus.trunk_point_avg * 100).roundToInt()}%"
        shoulder_progress.text = "${(mobilityStatus.shoulder_point_avg * 100).roundToInt()}%"
        hip_progress.text = "${(mobilityStatus.hip_point_avg * 100).roundToInt()}%"
        ankle_progress.text = "${(mobilityStatus.ankle_point_avg * 100).roundToInt()}%"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (mobilityStatus == null) {
            loadData()
        }
    }

    override fun onReloadData() {
        loadData()
    }

    private fun loadData() {
        showProgress()
        mobilityViewModel.getMobilityStatus(userID)
    }

    private fun initView() {
        back_btn.setOnClickListener {
            (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
        }

        btn_finish.extendTouch()
        btn_finish.setOnClickListener {
            handleCompleted()
        }

        shoulder_result.setOnClickListener(this)
        trunk_result.setOnClickListener(this)
        hip_result.setOnClickListener(this)
        ankle_result.setOnClickListener(this)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        handleCompleted()
    }

    private fun handleCompleted() {
        val watchLaterKellyRecommend = sharedPreferences.getLong(AppConstants.WATCH_LATER_KELLY_RECOMMEND, 0)
        //change from 3h to 1d
        if (watchLaterKellyRecommend == 0L || System.currentTimeMillis() - watchLaterKellyRecommend > 24 * 60 * 60 * 1000) {
            navigator.showKellyRecommendationFromMobilityTestComplete(activity)
        } else {
            (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
        }
    }
}