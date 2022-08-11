package com.cbi.app.trs.features.fragments.mobility.plan

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.mobility.MobilitySuggestVideoEntity
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.movies.movies_detail.MovieDetails
import com.cbi.app.trs.features.viewmodel.MobilityViewModel
import kotlinx.android.synthetic.main.fragment_mobility_plan.*
import java.lang.Exception
import javax.inject.Inject

class MobilityPlanFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId() = R.layout.fragment_mobility_plan

    @Inject
    lateinit var navigator: Navigator

    var adapter: MobilityPlanAdapter? = null

    lateinit var mobilityViewModel: MobilityViewModel

    val suggestVideos = ArrayList<MobilityPractice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        mobilityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(mobilitySuggestVideo, ::onReceiveSuggestVideo)
        }
    }

    private fun onReceiveSuggestVideo(data: MobilitySuggestVideoEntity.Data?) {
        hideProgress()
        if (data == null) return
        suggestVideos.add(MobilityPractice("Trunk", R.drawable.trunk_mobility_test_result, data.trunk_videos))
        suggestVideos.add(MobilityPractice("Shoulder", R.drawable.shoulder_mobility_test_result, data.shoulder_videos))
        suggestVideos.add(MobilityPractice("Hip", R.drawable.hip_mobility_test_result, data.hip_videos))
        suggestVideos.add(MobilityPractice("Ankle", R.drawable.ankle_mobility_test_result, data.ankle_videos))
        adapter = MobilityPlanAdapter(suggestVideos).apply { onItemClickListener = this@MobilityPlanFragment }
        mobility_plan_recylerview.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (adapter == null) {
            showProgress()
            mobilityViewModel.getMobilitySuggestVideo(userID)
        } else {
            adapter?.onRestoreInstanceState(savedInstanceState)
            mobility_plan_recylerview.adapter = adapter
        }
    }

    override fun onReloadData() {
        showProgress()
        suggestVideos.clear()
        mobilityViewModel.getMobilitySuggestVideo(userID)
    }

    private fun initView() {
        back_btn.setOnClickListener { pop(activity) }

        mobility_plan_recylerview.layoutManager = LinearLayoutManager(activity)
        if (mobility_plan_recylerview.itemAnimator is DefaultItemAnimator) {
            (mobility_plan_recylerview.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
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
        if (item != null && item is MovieData)
            navigator.showMovieDetails(activity, item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            (mobility_plan_recylerview?.adapter as MobilityPlanAdapter).onSaveInstanceState(outState)
        } catch (e: Exception) {

        }
    }
}