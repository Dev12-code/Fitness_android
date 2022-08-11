package com.cbi.app.trs.features.fragments.quiz

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.features.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_quiz_birthday.*
import org.greenrobot.eventbus.EventBus
import org.joda.time.DateTime
import org.joda.time.Years
import java.text.SimpleDateFormat
import java.util.*


class QuizBirthdayFragment : BaseFragment() {
    override fun layoutId(): Int {
        return R.layout.fragment_quiz_birthday
    }

    companion object {
        fun instance(arg: Bundle?): QuizBirthdayFragment {
            return QuizBirthdayFragment().apply { arguments = arg }
        }
    }

    var year = 1990
    var month = 0
    var date = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        birthday.setOnClickListener {
            val pickerDialog = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                if (!isBeforeToday(month, dayOfMonth, year)) return@OnDateSetListener
                if (getDiffYear(month, dayOfMonth, year) < 13) {
                    CommonUtils.showError(activity, "Ooops !", "You must be at least 13 years old to proceed!")
                    return@OnDateSetListener
                }

                this@QuizBirthdayFragment.year = year
                this@QuizBirthdayFragment.month = month
                this@QuizBirthdayFragment.date = dayOfMonth
                birthday.setText("${month + 1}/$dayOfMonth/$year")

                val date = SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse(birthday.text.toString() + " 00:00:00 +0000")
                arguments?.putLong(QuizFragment.BIRTHDAY, date.time / 1000)

                EventBus.getDefault().post(QuizFragment.QuizMessageEvent())
            },
                    year, month, date)

            pickerDialog.show()
        }
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
        CommonUtils.showError(activity, "Input Error", "Birthday must be before today.")
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