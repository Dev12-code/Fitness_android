package com.cbi.app.trs.features.fragments.progress.leaderboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.entities.LeaderBoardData
import com.cbi.app.trs.data.entities.UserRank
import com.cbi.app.trs.features.viewmodel.ActivityViewModel
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import javax.inject.Inject

class LeaderBoardFragment : BaseFragment() {
    override fun layoutId() = R.layout.fragment_leaderboard

    @Inject
    lateinit var adapter: LeaderBoardAdapter

    lateinit var activityViewModel: ActivityViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        activityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(leaderBoardData, ::onReceiveLeaderBoard)
        }
    }


    private fun onReceiveLeaderBoard(leaderBoardData: LeaderBoardData?) {
        hideProgress()
        if (leaderBoardData == null || leaderBoardData.all_user.isEmpty()) {
            empty_text_1.text = getString(R.string.worldwide_leaderboard_of_the_ready_state)
            empty_text_2.text = getString(R.string.let_s_join_our_activity_and_get_the_change_to_be_on_the_50_top_members)
            //show button OK
            btnOk.visibility = View.VISIBLE
            return
        }
        leadboard_area.visibility = View.VISIBLE
        loadUserRank(leaderBoardData.all_user[0], leaderBoardData.current_user.username == leaderBoardData.all_user[0].username)
        adapter.yourUserName = leaderBoardData.current_user.username
        leaderBoardData.all_user.removeAt(0)
        loadAllUserRank(leaderBoardData.all_user)
    }


    private fun loadAllUserRank(allUser: List<UserRank>) {
        adapter.collection = allUser
    }

    private fun loadUserRank(currentUser: UserRank, isYou: Boolean) {
        leaderboard_image.loadFromUrl(currentUser.user_avatar, false)
        leadboard_name.text = currentUser.username
        leaderboard_badge.text = currentUser.user_badge
        leaderboard_point.text = currentUser.user_point
        leaderboard_index.text = "${currentUser.user_rank_index}"

        if (isYou) {
            current_user_row.setBackgroundColor(Color.parseColor("#ebe8ff"))
        }
    }

    override fun onReloadData() {
        loadData()
    }

    private fun loadData() {
        showProgress()
        activityViewModel.leaderBoard(userID)
    }

    private fun initView() {
        leaderboard_recylerview.layoutManager = LinearLayoutManager(activity)
        leaderboard_recylerview.adapter = adapter

        //event click btn
        btnOk.setOnClickListener {
            pop(activity)
        }
    }
}
