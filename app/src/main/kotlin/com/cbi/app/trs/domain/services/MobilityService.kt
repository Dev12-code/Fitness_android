package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.MobilityAPI
import com.cbi.app.trs.domain.entities.mobility.MobilityKellyVideoEntity
import com.cbi.app.trs.domain.usecases.mobility.UpdateMobilityResult
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobilityService
@Inject constructor(retrofit: Retrofit) : MobilityAPI {
    private val mobilityAPI by lazy { retrofit.create(MobilityAPI::class.java) }

    override fun getMobilityStatus(userID: Int?) = mobilityAPI.getMobilityStatus(userID)

    override fun getMobilityTestVideos(userID: Int?) = mobilityAPI.getMobilityTestVideos(userID)

    override fun getMobilitySuggestVideos(userID: Int?) = mobilityAPI.getMobilitySuggestVideos(userID)

    override fun getMobilityKellyVideos(userID: Int?) = mobilityAPI.getMobilityKellyVideos(userID)

    override fun getMobilityIntroVideo(userID: Int?): Call<MobilityKellyVideoEntity> {
        return mobilityAPI.getMobilityIntroVideo(userID)
    }

    override fun updateMobilityResult(userID: Int?, param: UpdateMobilityResult.Param) = mobilityAPI.updateMobilityResult(userID, param)
}