package com.cbi.app.trs.features.fragments.bonus_content

import android.os.Bundle
import android.text.Html
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.features.viewmodel.BonusViewModel
import kotlinx.android.synthetic.main.bonus_item.view.*
import kotlinx.android.synthetic.main.fragment_bonus_content.*
import kotlinx.android.synthetic.main.main_header_layout.*
import javax.inject.Inject

class BonusContentFragment : DarkBaseFragment() {
    override fun layoutId() = R.layout.fragment_bonus_content

    @Inject
    lateinit var navigator: Navigator

    lateinit var bonusViewModel: BonusViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bonusViewModel.loadSystemBonus()
        bonusViewModel.getUserProfile(userID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        bonusViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(systemBonus, ::onReceiveSystemBonus)
        }
    }

    private fun onReceiveSystemBonus(list: List<SystemData.Bonus>?) {
        if (list == null || list.isEmpty()) {
            tv_empty_text.visibility = View.VISIBLE
            return
        }
        bonus_area.removeAllViews()
        for (bonus in list) {
            val view = layoutInflater.inflate(R.layout.bonus_item, null)
            view.bonus_title.text = Html.fromHtml(bonus.bonus_title)
            bonus.bonus_image?.let { view.bonus_image.loadFromUrl(it, false, isPlaceHolder = true) }
            view.setOnClickListener {
                navigator.showBonusDetail(activity, bonus)
            }
            bonus_area.addView(view)
        }
    }

    private fun initView() {
        progress_icon.setOnClickListener { if (isAllowForFreemium()) navigator.showProgress(activity) }
        user_icon.setOnClickListener { navigator.showSetting(activity) }
        favourite_icon.setOnClickListener { if (isAllowForFreemium()) navigator.showFavourite(activity) }
        home_icon.setOnClickListener {
            navigator.showHome(activity)
        }

        user_icon.extendTouch()
        progress_icon.extendTouch()
        favourite_icon.extendTouch()
        home_icon.extendTouch()
    }
}