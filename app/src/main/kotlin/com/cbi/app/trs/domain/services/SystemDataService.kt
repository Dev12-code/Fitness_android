package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.SystemDataAPI
import com.cbi.app.trs.domain.entities.system.SystemAreaEntity
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemDataService
@Inject constructor(retrofit: Retrofit) : SystemDataAPI {
    private val systemDataApi by lazy { retrofit.create(SystemDataAPI::class.java) }
    override fun systemConfig() = systemDataApi.systemConfig()
    override fun systemArea() = systemDataApi.systemArea()
    override fun systemAreaFilteredByDaily(): Call<SystemAreaEntity> {
        return systemDataApi.systemAreaFilteredByDaily()
    }

    override fun systemPainArea() = systemDataApi.systemPainArea()
    override fun systemEquipment() = systemDataApi.systemEquipment()
    override fun systemCollection() = systemDataApi.systemCollection()
    override fun systemAchievement() = systemDataApi.systemAchievement()
    override fun systemPreWorkout() = systemDataApi.systemPreWorkout()
    override fun systemPostWorkout() = systemDataApi.systemPostWorkout()
    override fun systemBonus() = systemDataApi.systemBonus()
    override fun getIntroMovie() = systemDataApi.getIntroMovie()
    override fun getReview() = systemDataApi.getReview()
}