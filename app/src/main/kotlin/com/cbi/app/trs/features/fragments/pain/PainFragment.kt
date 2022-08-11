package com.cbi.app.trs.features.fragments.pain

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.extendTouch
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.viewmodel.PainViewModel
import kotlinx.android.synthetic.main.fragment_pain.*
import kotlinx.android.synthetic.main.main_header_white_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PainFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId(): Int {
        return R.layout.fragment_pain
    }

    private var painAreaList: List<SystemData.PainArea> = ArrayList()

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var painBodyAdapter: PainBodyAdapter

    lateinit var painViewModel: PainViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        painViewModel = viewModel(viewModelFactory) {
            observe(systemPainArea, ::onReceivePainArea)
            observe(userProfileData, ::onReceiveUserProfile)
            failure(failureData, ::handleFailure)
        }
    }

    private fun onReceiveUserProfile(data: UserData.UserProfile?) {
        forceHide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (painAreaList.isEmpty()) {
            painViewModel.getSystemPainArea()
        }
        painViewModel.getUserProfile(userID)
        progress_icon.setOnClickListener { if (isAllowForFreemium()) navigator.showProgress(activity) }
        user_icon.setOnClickListener { navigator.showSetting(activity) }
        favourite_icon.setOnClickListener {
            if (isAllowForFreemium()) navigator.showFavourite(
                activity
            )
        }

        home_icon.setOnClickListener {
            navigator.showHome(activity)
        }

        user_icon.extendTouch()
        progress_icon.extendTouch()
        favourite_icon.extendTouch()
        home_icon.extendTouch()

        val isShowToolTip = sharedPreferences.getBoolean("IS_SHOW_TOOLTIP", false);
        if (!isShowToolTip) {
            //update local variables
            sharedPreferences.edit().putBoolean("IS_SHOW_TOOLTIP", true).apply();
            DialogAlert()
                .setMessage(getString(R.string.in_pain_tooltip))
                .setCancel(false)
                .setTitlePositive(getString(R.string.proceed))
                .show(requireContext())
        }
    }

    override fun onReloadData() {
        showProgress()
        painViewModel.getSystemPainArea()
    }

    private fun onReceivePainArea(list: List<SystemData.PainArea>?) {
        forceHide()
        if (list == null) return
        this.painAreaList = list
        initView()
    }

    private fun getPart(
        list: List<SystemData.PainArea>?,
        type: String
    ): ArrayList<SystemData.PainArea> {
        val result = ArrayList<SystemData.PainArea>()
        if (!list.isNullOrEmpty()) {
            for (item in list) {
                if (type == "front" && (item.pain_area_key == "wrist" || item.pain_area_key == "forearm")) {
                    continue
                }
                if (item.pain_area_type.contains(type))
                    result.add(item)
            }
        }
        return result
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        initView()
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }


    private fun initView() {
        pain_viewPager.enabledSwipe = true
        pain_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    pain_left.performClick()
                } else {
                    pain_right.performClick()
                }
            }

        })
        pain_viewPager.adapter = painBodyAdapter.apply {
            onPainItemClickListener = this@PainFragment
            backList = getPart(painAreaList, "back")
            frontList = getPart(painAreaList, "front")
        }
        pain_left.setOnClickListener {
            pain_viewPager.setCurrentItem(0, true)

            toggleTextView(pain_left, true)
            toggleTextView(pain_right, false)
        }
        pain_right.setOnClickListener {
            pain_viewPager.setCurrentItem(1, true)

            toggleTextView(pain_left, false)
            toggleTextView(pain_right, true)
        }
        Handler().postDelayed({
            pain_left?.isSelected = pain_viewPager?.currentItem != 0
            pain_right?.isSelected = pain_viewPager?.currentItem != 1
        }, 200)

        pain_left.performClick()
    }

    private fun toggleTextView(txv: TextView, selected: Boolean) {
        txv.isSelected = selected
        context?.let {
            when (selected) {
                true -> {
                    txv.setTextColor(ContextCompat.getColor(it, R.color.color_white))
                    txv.background =
                        ContextCompat.getDrawable(it, R.drawable.background_selected_tab_pain)
                    txv.typeface =
                        ResourcesCompat.getFont(it, R.font.hurme_geometric_sans1_semi_bold)
                }
                false -> {
                    txv.setTextColor(ContextCompat.getColor(it, R.color.color_text))
                    txv.background = ContextCompat.getDrawable(it, android.R.color.transparent)
                    txv.typeface = ResourcesCompat.getFont(it, R.font.hurme_geometric_sans1)
                }
            }
        }
    }

    override fun onItemClick(item: Any?, position: Int) {
        if (item == null) return

        val isAllow =
            filterAllowArea(item as SystemData.PainArea, "front", "head") || filterAllowArea(
                item,
                "back",
                "head"
            )
                    || filterAllowArea(item, "front", "quads") || filterAllowArea(
                item,
                "back",
                "elbow"
            )

        if (isAllow) {
            navigator.showPainDetails(activity, item)
            return
        }

        if (isAllowForFreemium()) {
            navigator.showPainDetails(activity, item)
            return
        }
    }

    private fun filterAllowArea(item: SystemData.PainArea, type: String, area: String): Boolean {
        if (item.pain_area_type == type && item.pain_area_key.contains(area)) {
            return true
        }
        return false
    }

}