package com.cbi.app.trs.features.fragments.progress

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.progress.achievement.AchievementFragment
import com.cbi.app.trs.features.fragments.progress.leaderboard.LeaderBoardFragment
import com.cbi.app.trs.features.fragments.progress.rules.RulesFragment
import kotlinx.android.synthetic.main.fragment_progress.*
import javax.inject.Inject

class ProgressFragment : DarkBaseFragment() {
    override fun layoutId() = R.layout.fragment_progress

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (childFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            rules.isSelected = true
            rules.typeface = Typeface.DEFAULT_BOLD
            setFragment(childFragmentManager, AchievementFragment())
        } else {
            when {
                childFragmentManager.findFragmentById(R.id.fragmentContainer) is RulesFragment -> {
                    toggleTextView(rules, true)
                }
                childFragmentManager.findFragmentById(R.id.fragmentContainer) is AchievementFragment -> {
                    toggleTextView(achievement, true)
                }
                childFragmentManager.findFragmentById(R.id.fragmentContainer) is LeaderBoardFragment -> {
                    toggleTextView(leadboard, true)
                }
            }
        }

        rules.setOnClickListener {
            toggleTextView(rules, true)
            toggleTextView(achievement, false)
            toggleTextView(leadboard, false)
            if (childFragmentManager.findFragmentById(R.id.fragmentContainer) !is RulesFragment) {
                setFragment(childFragmentManager, RulesFragment())
            }
        }
        achievement.setOnClickListener {
            toggleTextView(rules, false)
            toggleTextView(achievement, true)
            toggleTextView(leadboard, false)
            if (childFragmentManager.findFragmentById(R.id.fragmentContainer) !is AchievementFragment) {
                setFragment(childFragmentManager, AchievementFragment())
            }
        }
        leadboard.setOnClickListener {
            toggleTextView(rules, false)
            toggleTextView(achievement, false)
            toggleTextView(leadboard, true)
            if (childFragmentManager.findFragmentById(R.id.fragmentContainer) !is LeaderBoardFragment) {
                setFragment(childFragmentManager, LeaderBoardFragment())
            }
        }

        back_btn.setOnClickListener { pop(activity) }
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }
}