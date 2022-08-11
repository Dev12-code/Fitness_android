package com.cbi.app.trs.features.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import androidx.fragment.app.FragmentActivity
import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.R
import com.cbi.app.trs.core.di.ApplicationComponent
import com.cbi.app.trs.core.extension.getMonthYearDate
import com.cbi.app.trs.core.platform.BaseDialog
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.eventbus.UpdateProfileEvent
import com.cbi.app.trs.domain.usecases.user.PostUserProfile
import com.cbi.app.trs.features.utils.CommonUtils
import kotlinx.android.synthetic.main.dialog_edit_profile.*
import kotlinx.android.synthetic.main.dialog_edit_profile.birthday
import kotlinx.android.synthetic.main.dialog_edit_profile.first_name
import kotlinx.android.synthetic.main.dialog_edit_profile.last_name
import kotlinx.android.synthetic.main.fragment_quiz_birthday.*
import org.greenrobot.eventbus.EventBus
import org.joda.time.DateTime
import org.joda.time.Years
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditProfileDialog : BaseDialog() {
    private val TAG: String = "EditProfileDialog"
    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as AndroidApplication).appComponent
    }

    @Inject
    lateinit var userDataCache: UserDataCache

    var year = 1990
    var month = 0
    var date = 1

    fun show(context: Context) {
        super.show(context, TAG)
    }

    fun show(activity: FragmentActivity?) {
        if (activity == null) return
        super.show(activity, TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        userDataCache.get()?.user_profile?.dob?.let {
            val dateStr = it.getMonthYearDate("MM/dd/yyyy")
            if (dateStr.isNotEmpty()) {
                month = dateStr.split("/")[0].toInt() - 1
                date = dateStr.split("/")[1].toInt()
                year = dateStr.split("/")[2].toInt()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.DialogDimTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setDimAmount(0.5f)
        dialog.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
        return dialog
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.dialog_edit_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        first_name.setText("${userDataCache.get()?.user_profile?.first_name}")
        last_name.setText("${userDataCache.get()?.user_profile?.last_name}")
        birthday.setText("${userDataCache.get()?.user_profile?.dob?.getMonthYearDate("MM/dd/yyyy")}")
        cancel_btn.setOnClickListener { dialog?.dismiss() }
        birthday.setOnClickListener {
            val pickerDialog = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                if (!isBeforeToday(month, dayOfMonth, year)) return@OnDateSetListener
                if (getDiffYear(month, dayOfMonth, year) < 13) {
                    CommonUtils.showError(activity, "Ooops !", "You must be at least 13 years old to proceed!")
                    return@OnDateSetListener
                }
                this@EditProfileDialog.year = year
                this@EditProfileDialog.month = month
                this@EditProfileDialog.date = dayOfMonth
                birthday.setText(CommonUtils.getBeautyDate(month, dayOfMonth, year))
//
//                val date = SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse(birthday.text.toString() + " 00:00:00 +0000")
//                arguments?.putLong(QuizFragment.BIRTHDAY, date.time / 1000)
            },
                    year, month, date)

            pickerDialog.show()
        }
        save_btn.setOnClickListener {
            handleSave()
        }
    }

    private fun handleSave() {
        if (first_name.text.toString().trim().isEmpty()) {
            CommonUtils.showError(activity, "Update Profile", "Please enter your first name.")
            return
        }
        if (last_name.text.toString().trim().isEmpty()) {
            CommonUtils.showError(activity, "Update Profile", "Please enter your last name.")
            return
        }
        if (first_name.text.toString().trim().isNotEmpty() && last_name.text.toString().trim().isNotEmpty()) {
            if (first_name.text.toString().length !in 1..30) {
                CommonUtils.showError(activity, "Update Profile", "First name should be 1 to 30 characters.")
                return
            }
            if (last_name.text.toString().length !in 1..30) {
                CommonUtils.showError(activity, "Update Profile", "Last name should be 1 to 30 characters.")
                return
            }
        }
        if (birthday.text.toString().trim().isEmpty()) {
            CommonUtils.showError(activity, "Update Profile", "Please enter your birthday.")
            return
        }
        val date = SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse(birthday.text.toString() + " 00:00:00 +0000")
        EventBus.getDefault().post(UpdateProfileEvent(PostUserProfile.Param(UserData.UserProfile(dob = date.time / 1000, first_name = first_name.text.toString(), last_name = last_name.text.toString()))))
        dialog?.dismiss()
    }

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
