package com.cbi.app.trs.features.fragments.mobility.list

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.loadFromUrlFit
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.data.entities.MobilityTestVideoData
import com.cbi.app.trs.domain.eventbus.AnswerEvent
import com.cbi.app.trs.domain.eventbus.OnExitTheTestEvent
import com.cbi.app.trs.features.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_mobility_test_list.*
import kotlinx.android.synthetic.main.mobility_test_item.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MobilityTestListFragment : DarkBaseFragment() {
    override fun layoutId() = R.layout.fragment_mobility_test_list

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sharePreferences: SharedPreferences

    companion object {
        const val HEADER = "HEADER"
        const val MOBILITY_VIDEO = "MOBILITY_VIDEO"
        const val SCORE = "SCORE"
    }

    var mobilityVideo: MobilityTestVideoData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        mobilityVideo = arguments?.getParcelable(MOBILITY_VIDEO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
        (activity as MainActivity).navigationView.visibility = View.GONE

        btn_exit_the_test.setOnClickListener {
            close()
            EventBus.getDefault().postSticky(OnExitTheTestEvent())
        }
    }

    private fun loadData() {
        mobilityVideo?.let {
            mobility_test_1_image.loadFromUrlFit(it.pose_image.red_pose, isPlaceHolder = true)
            mobility_test_2_image.loadFromUrlFit(it.pose_image.yellow_pose, isPlaceHolder = true)
            mobility_test_3_image.loadFromUrlFit(it.pose_image.green_pose, isPlaceHolder = true)
            //set text
            mobility_test_1_text.text = it.pose_image.red_text
            mobility_test_2_text.text = it.pose_image.yellow_text
            mobility_test_3_text.text = it.pose_image.green_text
        }
    }

    override fun onBackPressed(): Boolean {
        close()
        return true
    }

    private fun initView() {
        back_btn.setOnClickListener { close() }
        mobility_test_1_image.setOnClickListener {
            Handler().postDelayed({
                EventBus.getDefault().post(AnswerEvent(1))
            }, 200)
            close()
        }
        mobility_test_2_image.setOnClickListener {
            Handler().postDelayed({
                EventBus.getDefault().post(AnswerEvent(2))
            }, 200)
            close()
        }
        mobility_test_3_image.setOnClickListener {
            Handler().postDelayed({
                EventBus.getDefault().post(AnswerEvent(3))
            }, 200)
            close()
        }
        val header = arguments?.getString(HEADER)
        header?.let {
            tv_test_header.text = header
        }

        when (arguments?.getInt(SCORE, 0)) {
            1 -> {
                mobility_test_1.background = ContextCompat.getDrawable(context!!, R.drawable.background_stroke_mobility_list)
                selected_mobility_test_1.visibility = View.VISIBLE
            }
            2 -> {
                mobility_test_2.background = ContextCompat.getDrawable(context!!, R.drawable.background_stroke_mobility_list)
                selected_mobility_test_2.visibility = View.VISIBLE
            }
            3 -> {
                mobility_test_3.background = ContextCompat.getDrawable(context!!, R.drawable.background_stroke_mobility_list)
                selected_mobility_test_3.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }
}