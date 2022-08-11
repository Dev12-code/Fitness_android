package com.cbi.app.trs.features.fragments.mobility

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.domain.eventbus.CompleteTestEvent
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.MobilityViewModel
import kotlinx.android.synthetic.main.fragment_mobility.*
import kotlinx.android.synthetic.main.main_header_white_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.math.roundToInt


class MobilityFragment : LightBaseFragment() {
    override fun layoutId() = R.layout.fragment_mobility

    companion object {
        const val ALREADY_TEST = "ALREADY_TEST"
    }

    private var mobilityStatus: MobilityStatus? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sharePreferences: SharedPreferences

    lateinit var mobilityViewModel: MobilityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        mobilityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(mobilityStatus, ::onReceiveMobilityStatus)
        }
    }

    private fun onReceiveMobilityStatus(mobilityStatus: MobilityStatus?) {
        forceHide()
        if (mobilityStatus == null) return
        this.mobilityStatus = mobilityStatus
        loadMobilityStatus(mobilityStatus)
    }

    override fun onReloadData() {
        showProgress()
        mobilityViewModel.getMobilityStatus(userID)
    }

    private fun loadMobilityStatus(mobilityStatus: MobilityStatus?) {
        if (mobilityStatus == null) return
        val trunkPercent = if (mobilityStatus.trunk_point_avg > 1) 100 else (mobilityStatus.trunk_point_avg * 100).roundToInt()
        val hipPercent = if (mobilityStatus.hip_point_avg > 1) 100 else (mobilityStatus.hip_point_avg * 100).roundToInt()
        val anklePercent = if (mobilityStatus.ankle_point_avg > 1) 100 else (mobilityStatus.ankle_point_avg * 100).roundToInt()
        val shoulderPercent = if (mobilityStatus.shoulder_point_avg > 1) 100 else (mobilityStatus.shoulder_point_avg * 100).roundToInt()

        trunk_progress.text = "${trunkPercent}%"
        circularProgressBar_trunk.progress = trunkPercent.toFloat()
        CommonUtils.setColorCode(trunkPercent, trunk_progress, circularProgressBar_trunk, mobilityStatus.on_process)

        hip_progress.text = "${hipPercent}%"
        circularProgressBar_hip.progress = hipPercent.toFloat()
        CommonUtils.setColorCode(hipPercent, hip_progress, circularProgressBar_hip, mobilityStatus.on_process)

        ankle_progress.text = "${anklePercent}%"
        circularProgressBar_ankle.progress = anklePercent.toFloat()
        CommonUtils.setColorCode(anklePercent, ankle_progress, circularProgressBar_ankle, mobilityStatus.on_process)

        shoulder_progress.text = "${shoulderPercent}%"
        circularProgressBar_shoulder.progress = (shoulderPercent).toFloat()
        CommonUtils.setColorCode(shoulderPercent, shoulder_progress, circularProgressBar_shoulder, mobilityStatus.on_process)

        if (mobilityStatus.on_process) {
            if (trunkPercent <= 0) {
                tv_trunk.text = String.format("Trunk\n(Incomplete)")
            }

            if (shoulderPercent <= 0) {
                tv_shoulder.text = String.format("Shoulder\n(Incomplete)")
            }

            if (hipPercent <= 0) {
                tv_hip.text = String.format("Hip\n(Incomplete)")
            }

            if (anklePercent <= 0) {
                tv_ankle.text = String.format("Ankle\n(Incomplete)")
            }

            mobility_btn.text = getString(R.string.btn_continue_my_mobility_test)
        }

        if (mobilityStatus.test_date > 0) {
            mobility_btn.text = getString(R.string.go_to_my_mobility_plan)
            mobility_btn.setOnClickListener {
                if (!isAllowForFreemium()) return@setOnClickListener
                navigator.showMobilityPlan(activity)
            }
            setSpannableRetest()
        } else {
            mobility_message.text = getString(R.string.check_your_body_mobility_today)
            mobility_btn.setOnClickListener {
                if (!isAllowForFreemium()) return@setOnClickListener

                val watchLaterIntro = sharePreferences.getLong(AppConstants.WATCH_LATER_INTRO, 0)
                //change from 3h to 1d
                if (watchLaterIntro == 0L || System.currentTimeMillis() - watchLaterIntro > 24 * 60 * 60 * 1000) {
                    navigator.showIntroMobility(activity)
                } else {
                    navigator.showMobilityTest(activity, mobilityStatus)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPaymentPurchased(event: PaymentPurchasedEvent) {
        changeBackground()
        if (mobilityStatus != null) {
            loadMobilityStatus(mobilityStatus)
        }
        EventBus.getDefault().removeStickyEvent(PaymentPurchasedEvent::class.java)
    }

    private fun changeBackground() {
        context?.let {
            if (!isAllowForFreemium(false)) {
                mobility_btn.background = ContextCompat.getDrawable(it, R.drawable.common_premium_btn_selector)
                mobility_btn.setTextColor(ContextCompat.getColor(it, R.color.color_white))
            } else {
                mobility_btn.background = ContextCompat.getDrawable(it, R.drawable.common_btn_selector)
                mobility_btn.setTextColor(ContextCompat.getColor(it, R.color.color_white))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (mobilityStatus == null) {
            showProgress()
            mobilityViewModel.getMobilityStatus(userID)
        } else {
            loadMobilityStatus(mobilityStatus)
        }
    }

    private fun initView() {
        progress_icon.setOnClickListener { if (isAllowForFreemium()) navigator.showProgress(activity) }
        user_icon.setOnClickListener { navigator.showSetting(activity) }
        favourite_icon.setOnClickListener { if (isAllowForFreemium()) navigator.showFavourite(activity) }
        user_name.text = "${userDataCache.get()?.user_profile?.first_name} ${userDataCache.get()?.user_profile?.last_name}"
        userDataCache.get()?.user_profile?.user_avatar?.let { user_avatar.loadFromUrl(it) }

        home_icon.setOnClickListener {
            navigator.showHome(activity)
        }

        user_icon.extendTouch()
        progress_icon.extendTouch()
        favourite_icon.extendTouch()
        home_icon.extendTouch()

        changeBackground()
    }

    private fun setSpannableRetest() {
        if (mobilityStatus == null) return

        if (mobilityStatus!!.test_date + 14 * 24 * 60 * 60 <= System.currentTimeMillis() / 1000) {
            tv_retest_today.visibility = View.VISIBLE
            tv_retest_today.paint?.isUnderlineText = true

            tv_retest_today.setOnClickListener {
                if (!isAllowForFreemium()) return@setOnClickListener

                sharePreferences.edit().putInt(AppConstants.SAVED_POSITION, -1).apply()
                val watchLaterIntro = sharePreferences.getLong(AppConstants.WATCH_LATER_INTRO, 0)
                //change from 3h to 1d
                if (watchLaterIntro == 0L || System.currentTimeMillis() - watchLaterIntro > 24 * 60 * 60 * 1000) {
                    navigator.showIntroMobility(activity)
                } else {
                    navigator.showMobilityTest(activity, mobilityStatus, true)
                }
            }

            val ss = SpannableString("It has been a while since your last Mobility test. Let's check on your progress. Retest today!")
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
//                    if (!isAllowForFreemium()) return
//                    val watchLaterIntro = sharePreferences.getLong(AppConstants.WATCH_LATER_INTRO, 0)
//                    //change from 3h to 1d
//                    if (watchLaterIntro == 0L || System.currentTimeMillis() - watchLaterIntro > 24 * 60 * 60 * 1000) {
//                        navigator.showIntroMobility(activity)
//                    } else {
//                        navigator.showMobilityTest(activity, mobilityStatus, true)
//                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
//                    ds.isUnderlineText = true
//                    if (!isAllowForFreemium(false) && context != null) {
//                        ds.color = ContextCompat.getColor(context!!, R.color.color_e4e4e4)
//                    }
                }
            }
            val resetPosition = ss.indexOf("Retest")
//            ss.setSpan(clickableSpan, resetPosition, resetPosition + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            mobility_message.text = ss
            mobility_message.movementMethod = LinkMovementMethod.getInstance()
            mobility_message.highlightColor = Color.TRANSPARENT
        } else {
            mobility_message.text = "${CommonUtils.getRemainingTime(mobilityStatus!!.test_date)} until the next test! Go to My Mobility Plan below to see the exercises recommended for you until then."
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

    @Subscribe
    fun onAnswerEvent(event: CompleteTestEvent) {
        mobilityStatus = null
    }
}