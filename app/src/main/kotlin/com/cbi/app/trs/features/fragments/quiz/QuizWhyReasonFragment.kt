package com.cbi.app.trs.features.fragments.quiz

import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromLocal
import com.cbi.app.trs.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_quiz_why_reason.*
import org.greenrobot.eventbus.EventBus

class QuizWhyReasonFragment : BaseFragment() {
    override fun layoutId(): Int {
        return R.layout.fragment_quiz_why_reason
    }

    companion object {
        fun instance(arg: Bundle?): QuizWhyReasonFragment {
            return QuizWhyReasonFragment().apply { arguments = arg }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_background.loadFromLocal(R.drawable.iv_quiz_why_are_you_here)
        why_quiz_1.setOnClickListener {
            why_quiz_1.isSelected = true
            why_quiz_2.isSelected = false
            why_quiz_3.isSelected = false

            arguments?.putInt(QuizFragment.STAY_REASON, 0)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let{
                (parentFragment as QuizFragment).moveToNextQuestion(3)
            }
        }
        why_quiz_2.setOnClickListener {
            why_quiz_1.isSelected = false
            why_quiz_2.isSelected = true
            why_quiz_3.isSelected = false

            arguments?.putInt(QuizFragment.STAY_REASON, 1)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let{
                (parentFragment as QuizFragment).moveToNextQuestion(3)
            }
        }
        why_quiz_3.setOnClickListener {
            why_quiz_1.isSelected = false
            why_quiz_2.isSelected = false
            why_quiz_3.isSelected = true

            arguments?.putInt(QuizFragment.STAY_REASON, 2)
            EventBus.getDefault().post(QuizFragment.QuizMessageEvent())

            //auto change new tab
            parentFragment?.let{
                (parentFragment as QuizFragment).moveToNextQuestion(3)
            }
        }
    }
}