package com.cbi.app.trs.features.fragments.progress.achievement

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.entities.AchievementData
import com.cbi.app.trs.features.activities.MainActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_achievement.btnOk
import kotlinx.android.synthetic.main.fragment_achievement_congrat.*

class AchievementCongratulationFragment : BaseFragment() {
    override fun layoutId() = R.layout.fragment_achievement_congrat

    companion object {
        const val ACHIEVEMENT_DATA = "ACHIEVEMENT_DATA"
    }


    var achievementData: AchievementData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        achievementData = arguments?.getParcelable(ACHIEVEMENT_DATA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    private fun loadData() {

    }

    override fun onReloadData() {
        loadData()
    }


    private fun initView() {

        achievementData?.let {
            iv_badge.loadFromUrl(it.achievement_active_image, false, isPlaceHolder = true)
            tv_badge_name.text = it.achievement_title
            tv_badge_points.text = String.format(getString(R.string.milestone_points), it.achievement_milestone)
            tv_congrat_message.text = String.format(getString(R.string.achievement_congrat_message), it.achievement_milestone)

            //animation scale down text
            val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(tv_congrat,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f))
            scaleDown.duration = 1000L

            scaleDown.repeatCount = ObjectAnimator.INFINITE
            scaleDown.repeatMode = ObjectAnimator.REVERSE

            scaleDown.start()


            //play audio
            val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.fireworks_audio)
            mediaPlayer?.setOnCompletionListener {
                try {
                    scaleDown.cancel()
                    //stop animation
                    if (animationView != null) {
                        animationView.cancelAnimation()
                        animationView.visibility = View.GONE
                    }
                    if (btnOk != null){
                        //show button OK
                        btnOk.visibility = View.VISIBLE
                    }
                }
                catch (e: Exception){
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
            mediaPlayer?.start()
        }
        //event click button OK
        btnOk.setOnClickListener {
            pop(activity)
        }
    }
}
