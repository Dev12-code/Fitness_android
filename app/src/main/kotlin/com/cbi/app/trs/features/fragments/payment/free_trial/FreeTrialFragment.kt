package com.cbi.app.trs.features.fragments.payment.free_trial

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.ReviewData
import com.cbi.app.trs.features.viewmodel.PaymentViewModel
import kotlinx.android.synthetic.main.free_trial_fragment.*
import javax.inject.Inject

class FreeTrialFragment : LightBaseFragment() {
    override fun layoutId(): Int {
        return R.layout.free_trial_fragment
    }

    @Inject
    lateinit var adapter: FreeTrialAdapter

    @Inject
    internal lateinit var navigator: Navigator

    lateinit var paymentViewModel: PaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        paymentViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(reviewData, ::onReceiveReview)
        }
    }

    private fun onReceiveReview(list: List<ReviewData>?) {
        hideProgress()
        if (list == null) return
        adapter.collection = list
    }

    override fun onReloadData() {
        showProgress()
        paymentViewModel.getReviewData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (adapter.collection.isEmpty()) {
            showProgress()
            paymentViewModel.getReviewData()
        }
    }

    private fun initView() {
        free_trial_viewpager.adapter = adapter
        dots_indicator.setViewPager(free_trial_viewpager)
        learn_more_btn.setOnClickListener {
            navigator.showLanding(activity)
            activity?.finish()
        }
        start_trial_btn.setOnClickListener {
            navigator.showPrice(activity)
        }
    }
}