package com.cbi.app.trs.features.fragments.search

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.usecases.movie.GetSearchResult
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.viewmodel.SearchViewModel
import com.google.android.flexbox.FlexDirection
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_bar_layout.*
import javax.inject.Inject


class SearchFragment : DarkBaseFragment(), OnItemClickListener, TextView.OnEditorActionListener {
    override fun layoutId() = R.layout.fragment_search

    @Inject
    lateinit var navigator: Navigator

    val tags = ArrayList<SystemData.Area>()
    val selectedTag = ArrayList<SystemData.Area>()

    lateinit var searchViewModel: SearchViewModel

    private var maxDuration = AppConstants.MAX_DURATION_10

    private var minDuration = AppConstants.MIN_DURATION_0

    companion object {
        const val ALLOW_SEARCH_BY_DURATION = "ALLOW_SEARCH_BY_DURATION"
        const val ALLOW_SEARCH_BY_KEYWORD = "ALLOW_SEARCH_BY_KEYWORD"
        const val SEARCH_TYPE = "SEARCH_TYPE"
    }

    private var searchType: String = ALLOW_SEARCH_BY_KEYWORD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        arguments?.let {
            searchType = it.getString(SEARCH_TYPE, ALLOW_SEARCH_BY_KEYWORD)
        }

        searchViewModel = viewModel(viewModelFactory) {
            observe(systemArea, ::onReceiveFocusArea)
            observe(systemAreaFiltered, ::onReceiveFocusAreaFiltered)
            observe(failureData, ::handleFailure)
        }
    }

    private fun onReceiveFocusArea(list: List<SystemData.Area>?) {
        if (list != null) {
            tags.addAll(list)
            loadFocusArea()
        }
    }


    private fun onReceiveFocusAreaFiltered(list: List<SystemData.Area>?) {
        if (list != null) {
            tags.addAll(list)
            loadFocusArea()
        }
    }

    private fun loadFocusArea() {
        for (tag in tags) {
            search_tags_flexbox.addView(activity?.let {
                SearchTagView(it).apply {
                    setText(tag.area_title)
                    setTag(tag)
                    if (selectedTag.contains(tag)) {
                        setCheck(isChecked = true, isEnabled = true)
                    }
                    onItemClickListener = this@SearchFragment
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (tags.size == 0) {
            when (searchType) {
                ALLOW_SEARCH_BY_DURATION -> {
                    searchViewModel.loadSystemAreaFiltered()
                }
                else -> {
                    searchViewModel.loadSystemArea()
                }
            }
            if (!isAllowForFreemium(false)) {
                maxDuration = AppConstants.MAX_DURATION_10
                minDuration = AppConstants.MIN_DURATION_0
            } else {
                maxDuration = AppConstants.MAX_DURATION_ALL
                minDuration = AppConstants.MIN_DURATION_0
            }
        } else
            loadFocusArea()
        initView()
    }

    private fun initView() {
        search_tags_flexbox.flexDirection = FlexDirection.ROW
        search_tags_flexbox.clipChildren = false
        search_tags_flexbox.clipToPadding = false
        search_edt.hint = getString(R.string.search_old_mobility_header)

        clear_all.setOnClickListener { clearAll() }
        back_btn.setOnClickListener {
            pop(activity)
        }
        time_all.setOnClickListener {
            if (!isAllowForFreemium(true)) return@setOnClickListener
            maxDuration = AppConstants.MAX_DURATION_ALL
            minDuration = AppConstants.MIN_DURATION_0

            toggleTextView(time_all, true)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, false)
        }
        time_10_mins.setOnClickListener {
            maxDuration = AppConstants.MAX_DURATION_10
            minDuration = AppConstants.MIN_DURATION_0

            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, true)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, false)
        }
        time_20_mins.setOnClickListener {
            if (!isAllowForFreemium(true)) return@setOnClickListener
            maxDuration = AppConstants.MAX_DURATION_20
            minDuration = AppConstants.MIN_DURATION_20

            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, true)
            toggleTextView(time_30_mins, false)
        }
        time_30_mins.setOnClickListener {
            if (!isAllowForFreemium(true)) return@setOnClickListener
            maxDuration = AppConstants.MAX_DURATION_30
            minDuration = AppConstants.MIN_DURATION_30

            toggleTextView(time_all, false)
            toggleTextView(time_10_mins, false)
            toggleTextView(time_20_mins, false)
            toggleTextView(time_30_mins, true)
        }
        //default selected
        when (maxDuration) {
            AppConstants.MAX_DURATION_ALL -> {
                time_all.isSelected = true
                time_all.typeface = Typeface.DEFAULT_BOLD
            }
            AppConstants.MAX_DURATION_10 -> {
                time_10_mins.isSelected = true
                time_10_mins.typeface = Typeface.DEFAULT_BOLD
            }
            AppConstants.MAX_DURATION_20 -> {
                time_20_mins.isSelected = true
                time_20_mins.typeface = Typeface.DEFAULT_BOLD

            }
            AppConstants.MAX_DURATION_30 -> {
                time_30_mins.isSelected = true
                time_30_mins.typeface = Typeface.DEFAULT_BOLD

            }
        }

        search_edt.setOnEditorActionListener(this)
        search_btn.setOnClickListener {
            when (searchType) {
                ALLOW_SEARCH_BY_KEYWORD -> {
                    navigator.showSearchResult(activity, GetSearchResult.Params(search_edt.text.toString(), "", ArrayList(), getFocusArea(), null, null))
                }
                ALLOW_SEARCH_BY_DURATION -> {
                    navigator.showSearchDailyResult(activity, GetSearchResult.Params(null, null, null, getFocusArea(), minDuration, maxDuration))
                }
            }
        }

        //handle show search type
        when (searchType) {
            ALLOW_SEARCH_BY_KEYWORD -> {
                //hide search text
                sear_tags_flexbox_scrollview.visibility = View.GONE
                textView.visibility = View.GONE
                include2.visibility = View.VISIBLE
                search_duration.visibility = View.GONE
                //open keyboard when searching with keyword
                Handler().postDelayed({
                    search_edt.requestFocus()
                    val imm: InputMethodManager? = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.showSoftInput(search_edt, InputMethodManager.SHOW_IMPLICIT)
                }, 500)
                //update search text
                search_btn.text = getString(R.string.btn_search_old_mobility)
            }
            ALLOW_SEARCH_BY_DURATION -> {
                //show duration
                include2.visibility = View.GONE
                search_duration.visibility = View.VISIBLE
                //change texxt for button
                search_btn.text = getString(R.string.filter_btn)
            }
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

    private fun getFocusArea(): List<Int> {
        val focusArea = ArrayList<Int>()
        selectedTag.clear()
        for (i in 0 until search_tags_flexbox.childCount) {
            val child = search_tags_flexbox.getChildAt(i) as SearchTagView
            if (child.isChecked) {
                selectedTag.add(child.tag as SystemData.Area)
                focusArea.add(selectedTag.last().area_id)
            }
        }
        return focusArea
    }

    private fun clearAll() {
        search_edt.setText("")
        for (tag in 0 until search_tags_flexbox.childCount) {
            (search_tags_flexbox.getChildAt(tag) as SearchTagView).setCheck(false)
        }
        selectedTag.clear()
        //clear duration filter
        if (!isAllowForFreemium(false)) {
            time_10_mins.performClick()
        } else {
            time_all.performClick()
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

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search_btn.performClick()
            return true;
        }
        // Return true if you have consumed the action, else false.
        return false;
    }
}