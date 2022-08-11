package com.cbi.app.trs.features.fragments.search

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.eventbus.FavouriteEvent
import com.cbi.app.trs.domain.usecases.movie.GetSearchResult
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.back_btn
import kotlinx.android.synthetic.main.fragment_search_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class SearchResultFragment : DarkBaseFragment(), OnItemClickListener, OnLoadmoreListener, SearchResultAdapter.OnDeleteClickListener {
    companion object {
        const val SEARCH_PARAM = "SEARCH_PARAM"

        const val SEARCH_DAILY_MAINTENANCE = "SEARCH_DAILY_MAINTENANCE"

        const val TITLE = "TITLE"
    }

    override fun layoutId() = R.layout.fragment_search_result

    private var removedItem: MovieData? = null

    @Inject
    lateinit var searchResultAdapter: SearchResultAdapter

    private var isFavourite = false

    private var isSearchDaily = false

    private var title: String? = null

    @Inject
    lateinit var navigator: Navigator

    lateinit var searchViewModel: SearchViewModel

    private var currentPage = 1
    private var totalPage = 0

    private var searchParam: GetSearchResult.Params? = GetSearchResult.Params(null, null, null, null, null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        arguments?.let {
            isFavourite = it.getBoolean("IS_FAVOURITE", false)
            searchParam = it.getParcelable(SEARCH_PARAM)
            isSearchDaily = it.getBoolean(SEARCH_DAILY_MAINTENANCE, false)
            title = it.getString(TITLE)
        }
        searchViewModel = viewModel(viewModelFactory) {
            observe(searchResult, ::onReceiveSearchResult)
            observe(searchOldMobilityWodResult, ::onReceiveSearchResult)
            observe(searchDailyResult, ::onReceiveSearchResult)
            observe(removeFavouriteResult, ::onReceiveRemoveFavourite)
            observe(failureData, ::handleFailure)
        }
    }

    private fun onReceiveRemoveFavourite(data: BaseEntities?) {
        hideProgress()
        data?.let {
            searchResultAdapter.collection.remove(removedItem)
            removedItem = null
            searchResultAdapter.notifyDataSetChanged()
            if (searchResultAdapter.collection.isEmpty()) {
                empty_text_1.text = "No Favorites Yet!"
                empty_text_2.text = "You have not selected any Favorite videos. Add some today!"
            }

            DialogAlert()
                    .setTitle("Video Removed")
                    .setMessage("You have removed this video from your Favorites.")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
        }
    }

    private fun onReceiveSearchResult(data: SearchMovieEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (currentPage == 1) searchResultAdapter.collection.clear()
        searchResultAdapter.collection.addAll(data.list_video)
        searchResultAdapter.notifyDataSetChanged()

        if (searchResultAdapter.collection.isEmpty()) {
            if (isFavourite) {
                empty_text_1.text = "No Favorites Yet!"
                empty_text_2.text = "You have not selected any Favorite videos. Add some today!"
            } else {
                empty_text_1.text = "No videos found!"
                empty_text_2.text = "Please try again later"
            }
        }

        currentPage = data.page
        totalPage = data.max_page
        searchResultAdapter.onLoadmoreListener = this
    }

    override fun onReloadData() {
        currentPage = 1
        totalPage = 0
        loadData()
    }

    override fun handleFailure(failure: Failure?) {
        super.handleFailure(failure)
        searchResultAdapter.onLoadmoreListener = this
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

    private fun loadData() {
        userDataCache.get()?.user_token?.userID?.let {
            if (searchParam != null) {
                showProgress()
                if (isSearchDaily) {
                    searchViewModel.searchDaily(Pair(it, searchParam!!))
                } else if (!isFavourite) {
                    searchViewModel.searchOldMobility(Pair(it, searchParam!!))
                } else {
                    searchViewModel.favourite(Pair(it, searchParam!!))
                }
            }
        }
    }

    private fun initView() {
        back_btn.setOnClickListener {
            pop(activity)
        }
        search_result_recylerview.layoutManager = GridLayoutManager(activity, 2)
        search_result_recylerview.adapter = searchResultAdapter.apply {
            onItemClickListener = this@SearchResultFragment
            onDeleteClickListener = this@SearchResultFragment
            isRemovable = isFavourite
        }
        if (isSearchDaily) {
            if (!TextUtils.isEmpty(title)) {
                tvTitle.text = title
            } else {
                tvTitle.text = getString(R.string.results)
            }
        } else if (isFavourite) {
            tvTitle.text = getString(R.string.favorites)
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

    override fun onLoadMore() {
        if (currentPage >= totalPage) {
            searchResultAdapter.onLoadmoreListener = null
            return
        }
        currentPage++
        searchParam?.let { it.page++ }
        loadData()
    }

    override fun onDeleteItem(item: MovieData) {
        DialogAlert().setTitle("Remove from Favourite?").setMessage("Are you sure you want to remove this video? ")
                .setTitleNegative("Cancel").setTitlePositive("OK").onPositive {
                    removedItem = item
                    userDataCache.get()?.user_token?.userID?.let {
                        showProgress()
                        searchViewModel.removeFavorite(Pair(it, item.video_id))
                    }
                }.show(activity)
    }

    @Subscribe
    fun onFavoriteEvent(favoriteEvent: FavouriteEvent) {
        if (favoriteEvent.movieData == null) return
        if (isFavourite) {
            for (item in searchResultAdapter.collection) {
                if (item.video_id == favoriteEvent.movieData.video_id) {
                    searchResultAdapter.collection.remove(item)
                    searchResultAdapter.notifyDataSetChanged()
                    break
                }
            }

            if (favoriteEvent.movieData.video_is_favorite) {
                searchResultAdapter.collection.add(favoriteEvent.movieData)
                searchResultAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
}