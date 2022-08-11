package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.ActivityAPI
import com.cbi.app.trs.domain.entities.BaseEntities
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityService
@Inject constructor(retrofit: Retrofit) : ActivityAPI {
    private val activityAPI by lazy { retrofit.create(ActivityAPI::class.java) }
    override fun getHistory(userID: Int?, limit: Int, page: Int) = activityAPI.getHistory(userID, limit, page)

    override fun getAchievement(userID: Int?) = activityAPI.getAchievement(userID)

    override fun getLeaderBoard(userID: Int?) = activityAPI.getLeaderBoard(userID)

    override fun trackingUserStreak(userID: Int?): Call<BaseEntities> {
        return activityAPI.trackingUserStreak(userID)
    }
}