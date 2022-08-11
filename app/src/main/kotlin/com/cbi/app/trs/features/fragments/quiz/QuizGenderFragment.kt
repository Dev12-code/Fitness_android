package com.cbi.app.trs.features.fragments.quiz

import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromLocal
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.features.fragments.quiz.QuizFragment.Companion.GENDER
import kotlinx.android.synthetic.main.fragment_quiz_gender.*
import org.greenrobot.eventbus.EventBus

class QuizGenderFragment : BaseFragment() {
    override fun layoutId(): Int {
        return R.layout.fragment_quiz_gender
    }

    companion object {
        fun instance(arg: Bundle?): QuizGenderFragment {
            return QuizGenderFragment().apply { arguments = arg }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_background.loadFromLocal(R.drawable.iv_quiz_gender)
        when (arguments?.getInt(GENDER, -1)) {
            0 -> quiz_female.isSelected = true
            1 -> quiz_male.isSelected = true
            2 -> quiz_non_binary.isSelected = true
            3 -> quiz_prefer_not_to_say.isSelected = true
        }

        quiz_female.setOnClickListener {
            quiz_female.isSelected = true
            quiz_male.isSelected = false
            quiz_non_binary.isSelected = false
            quiz_prefer_not_to_say.isSelected = false

            arguments?.putInt(GENDER, 0)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(1)
            }
        }
        quiz_male.setOnClickListener {
            quiz_female.isSelected = false
            quiz_male.isSelected = true
            quiz_non_binary.isSelected = false
            quiz_prefer_not_to_say.isSelected = false

            arguments?.putInt(GENDER, 1)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())
            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(1)
            }
        }
        quiz_non_binary.setOnClickListener {
            quiz_female.isSelected = false
            quiz_male.isSelected = false
            quiz_non_binary.isSelected = true
            quiz_prefer_not_to_say.isSelected = false


            arguments?.putInt(GENDER, 2)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(1)
            }
        }
        quiz_prefer_not_to_say.setOnClickListener {
            quiz_female.isSelected = false
            quiz_male.isSelected = false
            quiz_non_binary.isSelected = false
            quiz_prefer_not_to_say.isSelected = true

            arguments?.putInt(GENDER, 3)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let {
                (parentFragment as QuizFragment).moveToNextQuestion(1)
            }
        }
    }
}