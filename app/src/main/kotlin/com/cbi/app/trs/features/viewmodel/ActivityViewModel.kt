package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.LeaderBoardData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.activity.AchievementEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.activity.GetAchievement
import com.cbi.app.trs.domain.usecases.activity.GetHistory
import com.cbi.app.trs.domain.usecases.activity.GetLeaderBoard
import com.cbi.app.trs.domain.usecases.activity.TrackingUserStreakUseCase
import javax.inject.Inject

class ActivityViewModel @Inject constructor(private val getHistory: GetHistory,
                                            private val getAchievement: GetAchievement,
                                            private val trackingUserStreakUseCase: TrackingUserStreakUseCase,
                                            private val getLeaderBoard: GetLeaderBoard) : BaseViewModel() {
    var historyData: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()
    var achievementData: MutableLiveData<AchievementEntity.Data> = MutableLiveData()
    var leaderBoardData: MutableLiveData<LeaderBoardData> = MutableLiveData()
    var trackingUserStreak: MutableLiveData<BaseEntities> = MutableLiveData()

    fun history(userID: Int?, param: PagingParam) = getHistory(Pair(userID, param)) { it.fold(::handleFailure, ::handleHistoryData) }

    private fun handleHistoryData(data: SearchMovieEntity.Data) {
        this.historyData.value = data
    }

    fun achievement(userID: Int?) = getAchievement(userID) { it.fold(::handleFailure, ::handleAchievementData) }

    private fun handleAchievementData(data: AchievementEntity.Data) {
        this.achievementData.value = data
    }

    fun leaderBoard(userID: Int?) = getLeaderBoard(userID) { it.fold(::handleFailure, ::handleLeaderBoardData) }

    private fun handleLeaderBoardData(data: LeaderBoardData) {
        this.leaderBoardData.value = data
    }

    private fun handleTrackingUserStreak(data: BaseEntities) {
        this.trackingUserStreak.value = data
    }

    fun trackingUserStreak(userID: Int?) = trackingUserStreakUseCase(userID) {
        it.fold(::handleFailureTracking, ::handleTrackingUserStreak)
    }

    var failureDataTracking: MutableLiveData<Failure> = MutableLiveData()

    private fun handleFailureTracking(failure: Failure?) {
        this.failureDataTracking.value = failure
    }
}