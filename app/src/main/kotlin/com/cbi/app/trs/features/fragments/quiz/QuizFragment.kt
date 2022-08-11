package com.cbi.app.trs.features.fragments.quiz

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.user.PostUserProfile
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.android.synthetic.main.fragment_quiz_birthday.*
import kotlinx.android.synthetic.main.fragment_quiz_name.*
import org.joda.time.DateTime
import org.joda.time.Years
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class QuizFragment : LightBaseFragment() {
    companion object {
        const val FIRST_NAME = "FIRST_NAME"
        const val LAST_NAME = "LAST_NAME"
        const val BIRTHDAY = "BIRTHDAY"
        const val GENDER = "GENDER"
        const val ACTIVITY_LEVEL = "ACTIVITY_LEVEL"
        const val STAY_REASON = "STAY_REASON"
        const val EXPERIENCE = "EXPERIENCE"
        const val EQUIPMENT = "EQUIPMENT"

        fun instance(): QuizFragment {
            return QuizFragment().apply { arguments = Bundle() }
        }
    }

    private var adapter: PagerAdapter? = null

    @Inject
    lateinit var navigator: Navigator

    lateinit var userProfileViewModel: UserProfileViewModel

    private var step = 0

    var year = 1990
    var month = 0
    var date = 1

    override fun layoutId(): Int {
        return R.layout.fragment_quiz
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        userProfileViewModel = viewModel(viewModelFactory) {
            observe(updateUserProfile, ::onUpdateNameSuccessful)
            failure(failureData, ::handleFailure)
        }
        userDataCache.get()?.user_profile?.dob?.let {
            val dateStr = it.getMonthYearDate("MM/dd/yyyy")
            if (dateStr.isNotEmpty()) {
                month = dateStr.split("/")[0].toInt() - 1
                date = dateStr.split("/")[1].toInt()
                year = dateStr.split("/")[2].toInt()
            }
        }
    }

    private fun onUpdateNameSuccessful(baseEntities: BaseEntities?) {
        hideProgress()
        step++
        checkStep()
    }

    fun moveToNextQuestion(index: Int) {
        quiz_viewpager.setCurrentItem(index, true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        quiz_viewpager.offscreenPageLimit = 6
        if (adapter == null) {
            childFragmentManager.let { adapter = QuizSlidePagerAdapter(it) }
        }
        quiz_viewpager.adapter = adapter
        dots_indicator.setViewPager(quiz_viewpager)
        quiz_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == (quiz_viewpager.adapter?.count ?: 0) - 1) {
                    //show popup
                    DialogAlert()
                            .setTitle(getString(R.string.message))
                            .setMessage(getString(R.string.message_equipment_popup))
                            .setCancel(false)
                            .setTitlePositive("OK")
                            .show(requireContext())
                }
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        last_name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveName()
            }
            return@setOnEditorActionListener false
        }

        skip_btn.setOnClickListener {
            DialogAlert().setTitle("Skip Quiz").setMessage("Are you sure? You will have to restart the quiz next time you log in")
                    .setTitleNegative("Cancel").setTitlePositive("OK").onPositive {
                        navigator.showTrial(activity, userDataCache)
                        activity?.finish()
                    }.show(activity)
        }
        skip_btn.extendTouch()
        back_btn.setOnClickListener {
            if (quiz_viewpager.currentItem > 0) {
                quiz_viewpager.setCurrentItem(quiz_viewpager.currentItem - 1, true)
            } else {
                quiz_viewpager.visibility = View.GONE
                dots_indicator.visibility = View.GONE
                quiz_name.visibility = View.VISIBLE
                back_btn.visibility = View.GONE
                skip_btn.visibility = View.GONE
            }
        }
        userDataCache.get()?.user_profile?.let {
            first_name.setText("${it.first_name}")
            last_name.setText("${it.last_name}")
        }
        birthday.setText("${userDataCache.get()?.user_profile?.dob?.getMonthYearDate("MM/dd/yyyy")}")
        birthday.setOnClickListener {
            val pickerDialog = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                if (!isBeforeToday(month, dayOfMonth, year)) return@OnDateSetListener
                if (getDiffYear(month, dayOfMonth, year) < 13) {
                    CommonUtils.showError(activity, "Ooops !", "You must be at least 13 years old to proceed!")
                    return@OnDateSetListener
                }
                this.year = year
                this.month = month
                this.date = dayOfMonth

                birthday.setText(CommonUtils.getBeautyDate(month, dayOfMonth, year))
            },
                    year, month, date)

            pickerDialog.show()
        }

        checkStep()
    }

    private fun checkStep() {
        when (step) {
            0 -> { //Update Name
                quiz_viewpager.visibility = View.GONE
                dots_indicator.visibility = View.GONE
                quiz_name.visibility = View.VISIBLE
                quiz_birthday.visibility = View.GONE
                quiz_done_btn.visibility = View.VISIBLE
                back_btn.visibility = View.GONE
                skip_btn.visibility = View.GONE

                quiz_done_btn.setOnClickListener { saveName() }
            }
            1 -> { //Update Birthday
                quiz_viewpager.visibility = View.GONE
                dots_indicator.visibility = View.GONE
                quiz_name.visibility = View.GONE
                quiz_birthday.visibility = View.VISIBLE
                quiz_done_btn.visibility = View.VISIBLE
                back_btn.visibility = View.GONE
                skip_btn.visibility = View.GONE

                quiz_done_btn.setOnClickListener { saveBirthday() }
            }
            else -> { //Update Quiz
                quiz_viewpager.visibility = View.VISIBLE
                dots_indicator.visibility = View.VISIBLE
                quiz_name.visibility = View.GONE
                quiz_birthday.visibility = View.GONE
                quiz_done_btn.visibility = View.GONE
                back_btn.visibility = View.GONE
                //change 05/04/2021
                skip_btn.visibility = View.GONE
            }
        }
    }

    private fun saveBirthday() {
        if (birthday.text.toString().trim().isEmpty()) {
            CommonUtils.showError(activity, "Failed Answer Quiz", "Please enter your birthday.")
            return
        }
        val date = SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse(birthday.text.toString() + " 00:00:00 +0000")
        userDataCache.get()?.user_token?.userID?.let {
            showProgress()
            userProfileViewModel.updateUserProfile(Pair(it, PostUserProfile.Param(UserData.UserProfile(dob = date.time / 1000, first_name = first_name.text.toString(), last_name = last_name.text.toString()))))
        }
    }

    private fun saveName() {
        if ((first_name.text.toString().trim().isNullOrEmpty())) {
            CommonUtils.showError(activity, "Failed Answer Quiz", "Please enter your first name")
            return
        }
        if ((last_name.text.toString().trim().isNullOrEmpty())) {
            CommonUtils.showError(activity, "Failed Answer Quiz", "Please enter your last name")
            return
        }

        if (first_name.text.toString().trim().isNotEmpty() && last_name.text.toString().trim().isNotEmpty()) {
            if (first_name.text.toString().length !in 1..30) {
                CommonUtils.showError(activity, "Failed Answer Quiz", "First name should be 1 to 30 characters")
                return
            }
            if (last_name.text.toString().length !in 1..30) {
                CommonUtils.showError(activity, "Failed Answer Quiz", "Last name should be 1 to 30 characters")
                return
            }

            userDataCache.get()?.user_token?.userID?.let {
                showProgress()
                var dob = 0L
                userDataCache.get()?.user_profile?.dob?.let { it2 -> dob = it2 }
                userProfileViewModel.updateUserProfile(Pair(it, PostUserProfile.Param(UserData.UserProfile(dob = dob, first_name = first_name.text.toString(), last_name = last_name.text.toString()))))
            }
        }
    }

    private inner class QuizSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 5

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> QuizGenderFragment.instance(arguments?.apply {

                })
                1 -> QuizActivityLevelFragment.instance(arguments)
                2 -> QuizWhyReasonFragment.instance(arguments)
                3 -> QuizExperienceFragment.instance(arguments)
                else -> QuizEquipmentFragment.instance(arguments)
            }
        }
    }

    class QuizMessageEvent {}

    fun isBeforeToday(month: Int, date: Int, year: Int): Boolean {
        val today = Calendar.getInstance()
        if (today.get(Calendar.YEAR) > year) {
            return true
        } else if (today.get(Calendar.YEAR) == year) {
            if (today.get(Calendar.MONTH) > month) {
                return true
            } else if (today.get(Calendar.MONTH) == month) {
                if (today.get(Calendar.DATE) > date) {
                    return true
                }
            }
        }
        CommonUtils.showError(activity, "Input error", "Please enter your correct date of birth.")
        return false
    }

    fun getDiffYear(month: Int, date: Int, year: Int): Int {
        val todayYear = Calendar.getInstance().get(Calendar.YEAR)
        val todayMonth = Calendar.getInstance().get(Calendar.MONTH)
        val todayDate = Calendar.getInstance().get(Calendar.DATE)
        val start = DateTime(todayYear, todayMonth + 1, todayDate, 0, 0, 0, 0)
        val end = DateTime(year, month + 1, date, 0, 0, 0, 0)
        return Years.yearsBetween(end, start).years
    }
}