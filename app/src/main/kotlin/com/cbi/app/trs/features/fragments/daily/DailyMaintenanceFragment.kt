package com.cbi.app.trs.features.fragments.daily

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.extendTouch
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.search.SearchResultAdapter
import com.cbi.app.trs.features.viewmodel.DailyViewModel
import kotlinx.android.synthetic.main.fragment_daily_maintenance.*
import kotlinx.android.synthetic.main.fragment_search_result.search_result_recylerview
import javax.inject.Inject

class DailyMaintenanceFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId(): Int {
        return R.layout.fragment_daily_maintenance
    }

    private lateinit var dailyViewModel: DailyViewModel

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var searchResultAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        dailyViewModel = viewModel(viewModelFactory) {
            observe(failureData, ::handleFailure)
            observe(dailyMaintenance, ::onReceiveDailyMaintenance)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (searchResultAdapter.collection.size == 0) {
            loadData()
        } else {
            searchResultAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        back_btn.extendTouch()
        back_btn.setOnClickListener {
            pop(activity)
        }
        search_result_recylerview.layoutManager = GridLayoutManager(activity, 2)
        search_result_recylerview.adapter = searchResultAdapter.apply {
            onItemClickListener = this@DailyMaintenanceFragment
        }
    }

    private fun loadData() {
        showProgress()
        dailyViewModel.getDailyMaintenance(userID)
    }

    override fun onReloadData() {
        loadData()
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

    private fun onReceiveDailyMaintenance(list: List<MovieData>?) {
        hideProgress()
        if (list == null || list.isEmpty()) return
        searchResultAdapter.collection = list as ArrayList<MovieData>
    }
}