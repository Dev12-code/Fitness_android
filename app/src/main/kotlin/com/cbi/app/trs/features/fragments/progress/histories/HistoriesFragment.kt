package com.cbi.app.trs.features.fragments.progress.histories

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.features.viewmodel.ActivityViewModel
import kotlinx.android.synthetic.main.fragment_histories.*
import javax.inject.Inject

class HistoriesFragment : BaseFragment(), OnItemClickListener, OnLoadmoreListener {
    override fun layoutId() = R.layout.fragment_histories

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var adapter: HistoriesAdapter

    lateinit var activityViewModel: ActivityViewModel

    private var currentPage = 1
    private var totalPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        activityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(historyData, ::onReceiveHistory)
        }
    }

    private fun onReceiveHistory(data: SearchMovieEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (currentPage == 1) adapter.collection.clear()
        adapter.collection.addAll(data.list_video)
        if (adapter.collection.isEmpty()) empty_text.text = "When you watch a video or join our mini Q&A, your history will show up here."
        adapter.notifyDataSetChanged()
        currentPage = data.page
        totalPage = data.max_page
        adapter.onLoadmoreListener = this
    }

    override fun onReloadData() {
        currentPage = 1
        totalPage = 0
        loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (adapter.collection.isEmpty()) {
            loadData()
        }
    }

    private fun initView() {
        histories_recylerview.layoutManager = LinearLayoutManager(activity)
        histories_recylerview.adapter = adapter.apply {
            onItemClickListener = this@HistoriesFragment
            onLoadmoreListener = this@HistoriesFragment
        }
    }

    private fun loadData() {
        showProgress()
        activityViewModel.history(userID, PagingParam().apply { page = currentPage })
    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item != null && item is MovieData)
            navigator.showMovieDetails(activity, item)
    }

    override fun onLoadMore() {
        if (currentPage >= totalPage) {
            adapter.onLoadmoreListener = null
            return
        }
        currentPage++
        loadData()
    }
}
