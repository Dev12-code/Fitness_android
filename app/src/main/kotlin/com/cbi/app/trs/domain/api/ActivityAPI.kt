package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.activity.AchievementEntity
import com.cbi.app.trs.domain.entities.activity.LeaderBoardEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ActivityAPI {
    companion object {
        private const val UserID = "UserID"

        private const val HISTORY = "{$UserID}/activity/history"
        private const val ACHIEVEMENT = "{$UserID}/activity/user_archivement"
        private const val LEADER_BOARD = "{$UserID}/activity/global_leaderboard"
        private const val TRACKING_USER_STREAK = "{$UserID}/activity/tracking_user_streak"
    }

    @GET(HISTORY)
    fun getHistory(@Path(UserID) userID: Int?, @Query("limit") limit: Int, @Query("page") page: Int): Call<SearchMovieEntity>

    @GET(ACHIEVEMENT)
    fun getAchievement(@Path(UserID) userID: Int?): Call<AchievementEntity>

    @GET(LEADER_BOARD)
    fun getLeaderBoard(@Path(UserID) userID: Int?): Call<LeaderBoardEntity>

    @POST(TRACKING_USER_STREAK)
    fun trackingUserStreak(@Path(UserID) userID: Int?): Call<BaseEntities>
}