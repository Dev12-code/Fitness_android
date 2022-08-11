package com.cbi.app.trs.features.fragments.quiz

import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromLocal
import com.cbi.app.trs.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_quiz_experience.*
import org.greenrobot.eventbus.EventBus

class QuizExperienceFragment : BaseFragment() {
    override fun layoutId(): Int {
        return R.layout.fragment_quiz_experience
    }

    companion object {
        fun instance(arg: Bundle?): QuizExperienceFragment {
            return QuizExperienceFragment().apply { arguments = arg }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_background.loadFromLocal(R.drawable.iv_quiz_how_often)
        exp_quiz_1.setOnClickListener {
            exp_quiz_1.isSelected = true
            exp_quiz_2.isSelected = false
            exp_quiz_3.isSelected = false

            arguments?.putInt(QuizFragment.EXPERIENCE, 0)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(4)
            }
        }
        exp_quiz_2.setOnClickListener {
            exp_quiz_1.isSelected = false
            exp_quiz_2.isSelected = true
            exp_quiz_3.isSelected = false

            arguments?.putInt(QuizFragment.EXPERIENCE, 1)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(4)
            }
        }
        exp_quiz_3.setOnClickListener {
            exp_quiz_1.isSelected = false
            exp_quiz_2.isSelected = false
            exp_quiz_3.isSelected = true

            arguments?.putInt(QuizFragment.EXPERIENCE, 2)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(4)
            }
        }
    }
}