package com.cbi.app.trs.features.fragments.quiz

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.loadFromLocal
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.quiz.PostQuiz
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.QuizViewModel
import com.cbi.app.trs.features.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.fragment_quiz_equipment.*
import javax.inject.Inject


class QuizEquipmentFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener {
    override fun layoutId(): Int {
        return R.layout.fragment_quiz_equipment
    }

    companion object {
        fun instance(arg: Bundle?): QuizEquipmentFragment {
            return QuizEquipmentFragment().apply { arguments = arg }
        }
    }

    lateinit var quizViewModel: QuizViewModel
    lateinit var splashViewModel: SplashViewModel

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        quizViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(quizUpdate, ::onReceiveQuizUpdate)
        }
        splashViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(systemEquipment, ::oneReceiveEquipment)
        }
    }

    private fun oneReceiveEquipment(list: List<SystemData.Equipment>?) {
        if (list == null) return
        quiz_equipment_container.removeAllViews()

        for (item in list) {
            val itemView = layoutInflater.inflate(R.layout.quiz_equipment_item, null) as AppCompatCheckBox
            itemView.text = item.equipment_title
            itemView.id = item.equipment_id
            if (item.equipment_title.contains("Lacrosse Ball") || item.equipment_title.contains("Foam Roller")) {
                itemView.isChecked = true
                itemView.isEnabled = false
            }
            itemView.setOnCheckedChangeListener(this)
            quiz_equipment_container.addView(itemView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_background.loadFromLocal(R.drawable.iv_quiz_equipment)
        initView()
        splashViewModel.loadSystemEquipment(true)
    }

    fun onReceiveQuizUpdate(baseEntities: BaseEntities?) {
        hideProgress()
        if (baseEntities == null) return
        if (baseEntities.isSuccess) {
            navigator.showTrial(activity, userDataCache)
            activity?.finish()
        } else {
            CommonUtils.showError(activity, "Quiz", "Update Quiz Failed")
        }
    }

    private fun initView() {
        quiz_done_btn.setOnClickListener {
            arguments?.let {
                if (it.getInt(QuizFragment.GENDER, -1) >= 0
                        && it.getInt(QuizFragment.ACTIVITY_LEVEL, -1) >= 0
                        && it.getInt(QuizFragment.STAY_REASON, -1) >= 0
                        && it.getInt(QuizFragment.EXPERIENCE, -1) >= 0
                        && getEquipmentResult().isNotEmpty()) {

                    userDataCache.get()?.user_token?.userID?.let { it1 ->
                        showProgress()
                        quizViewModel.updateQuiz(Pair(it1, PostQuiz.Param(it.getInt(QuizFragment.GENDER, -1),
                                it.getInt(QuizFragment.ACTIVITY_LEVEL, -1), it.getInt(QuizFragment.EXPERIENCE, -1), it.getInt(QuizFragment.STAY_REASON, -1), getEquipmentResult())))
                    }
                } else {
                    CommonUtils.showError(activity, "Failed Answer Quiz", "Please answer all questions.")
                }
            }
        }
    }

    private fun getEquipmentResult(): ArrayList<Int> {
        var equipmentCheckList = ArrayList<Int>()
        if (quiz_equipment_container.childCount == 0)
            return equipmentCheckList
        else
            for (itemViewIndex in 0..(quiz_equipment_container.childCount - 1)) {
                val itemView = quiz_equipment_container.getChildAt(itemViewIndex) as AppCompatCheckBox
                if (itemView.isChecked) equipmentCheckList.add(itemView.id)
            }
        return equipmentCheckList
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        if (getEquipmentResult().isEmpty()) {
//            Handler().postDelayed({
//                quiz_equipment_container.getChildAt(0)?.performClick()
//            }, 500)
//            return
//        }
    }
}