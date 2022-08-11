package com.cbi.app.trs.features.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.features.fragments.quiz.QuizFragment

class QuizActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, QuizActivity::class.java)
    }

    override fun fragment() = QuizFragment.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFullScreenMode()
    }
}
