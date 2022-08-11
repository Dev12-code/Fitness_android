package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.PainAPI
import com.cbi.app.trs.domain.usecases.pain.GetUnderstandingPain
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PainService
@Inject constructor(retrofit: Retrofit) : PainAPI {
    private val painAPI by lazy { retrofit.create(PainAPI::class.java) }
    override fun getStartedPain(userID: Int?, painAreaId: Int?) = painAPI.getStartedPain(userID, painAreaId)
    override fun getUnderstandingPain(userID: Int?, painAreaId: Int?, filter: String?, limit: Int, page: Int) = painAPI.getUnderstandingPain(userID, painAreaId, filter, limit, page)
    override fun getMobilityRxPain(userID: Int?, painAreaId: Int?, filter: String?, limit: Int, page: Int) = painAPI.getMobilityRxPain(userID, painAreaId, filter, limit, page)
}