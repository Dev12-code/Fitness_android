package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilityEntity
import com.cbi.app.trs.domain.entities.mobility.MobilityKellyVideoEntity
import com.cbi.app.trs.domain.entities.mobility.MobilitySuggestVideoEntity
import com.cbi.app.trs.domain.entities.mobility.MobilityTestVideoEntity
import com.cbi.app.trs.domain.usecases.mobility.UpdateMobilityResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface MobilityAPI {
    companion object {
        private const val UserID = "UserID"
        private const val MOBILITY_STATUS = "{$UserID}/user/mobility"
        private const val MOBILITY_LIST_VIDEO = "{$UserID}/user/mobility_test"
        private const val MOBILITY_SUGGEST_VIDEO = "{$UserID}/user/mobility_videos"
        private const val MOBILITY_RESULT = "{$UserID}/user/mobility_result"
        private const val MOBILITY_KELLY = "{$UserID}/kelly_recommend_video"
        private const val MOBILITY_INTRO_VIDEO = "{$UserID}/mobility_intro_video"
    }

    @GET(MOBILITY_STATUS)
    fun getMobilityStatus(@Path(UserID) userID: Int?): Call<MobilityEntity>

    @GET(MOBILITY_LIST_VIDEO)
    fun getMobilityTestVideos(@Path(UserID) userID: Int?): Call<MobilityTestVideoEntity>

    @GET(MOBILITY_SUGGEST_VIDEO)
    fun getMobilitySuggestVideos(@Path(UserID) userID: Int?): Call<MobilitySuggestVideoEntity>

    @GET(MOBILITY_KELLY)
    fun getMobilityKellyVideos(@Path(UserID) userID: Int?): Call<MobilityKellyVideoEntity>

    @GET(MOBILITY_INTRO_VIDEO)
    fun getMobilityIntroVideo(@Path(UserID) userID: Int?): Call<MobilityKellyVideoEntity>

    @POST(MOBILITY_RESULT)
    fun updateMobilityResult(@Path(UserID) userID: Int?, @Body param: UpdateMobilityResult.Param): Call<BaseEntities>
}