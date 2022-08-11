package com.cbi.app.trs.features.fragments.intro

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.features.viewmodel.IntroViewModel
import kotlinx.android.synthetic.main.fragment_intro.*
import kotlinx.android.synthetic.main.fragment_intro.dots_indicator
import kotlinx.android.synthetic.main.free_trial_fragment.*

class IntroFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    override fun layoutId() = R.layout.fragment_intro

    var adapter: IntroListAdapter? = null

    lateinit var introViewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        introViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(introData, ::onReceiveIntro)
        }
        if (adapter == null) {
            adapter = IntroListAdapter(childFragmentManager)
        }
    }

    private fun onReceiveIntro(list: List<MovieData>?) {
        hideProgress()
        if (list == null) return
        adapter?.collection = list
    }

    override fun onReloadData() {
        introViewModel.getIntroMovie()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        introViewModel.getIntroMovie()
    }

    private fun initView() {
        intro_viewpager.adapter = adapter
        intro_viewpager.offscreenPageLimit == 1
        intro_viewpager.addOnPageChangeListener(this)
        back_btn.setOnClickListener { close() }
        dots_indicator.setViewPager(intro_viewpager)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }
}