package com.cbi.app.trs.features.fragments.quiz

import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromLocal
import com.cbi.app.trs.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_quiz_activity_level.*
import org.greenrobot.eventbus.EventBus

class QuizActivityLevelFragment : BaseFragment() {
    override fun layoutId(): Int {
        return R.layout.fragment_quiz_activity_level
    }

    companion object {
        fun instance(arg: Bundle?): QuizActivityLevelFragment {
            return QuizActivityLevelFragment().apply { arguments = arg }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_background.loadFromLocal(R.drawable.iv_quiz_activity_level)

        activity_quiz_1.setOnClickListener {
            activity_quiz_1.isSelected = true
            activity_quiz_2.isSelected = false
            activity_quiz_3.isSelected = false

            arguments?.putInt(QuizFragment.ACTIVITY_LEVEL, 0)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(2)
            }
        }
        activity_quiz_2.setOnClickListener {
            activity_quiz_1.isSelected = false
            activity_quiz_2.isSelected = true
            activity_quiz_3.isSelected = false

            arguments?.putInt(QuizFragment.ACTIVITY_LEVEL, 1)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(2)
            }
        }
        activity_quiz_3.setOnClickListener {
            activity_quiz_1.isSelected = false
            activity_quiz_2.isSelected = false
            activity_quiz_3.isSelected = true

            arguments?.putInt(QuizFragment.ACTIVITY_LEVEL, 2)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(2)
            }
        }
    }
}