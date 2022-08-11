package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.movie.MovieListEntity
import com.cbi.app.trs.domain.entities.system.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

internal interface SystemDataAPI {
    companion object {
        private const val SYSTEM_CONFIG = "system/config"
        private const val SYSTEM_AREA = "system/areas"
        private const val SYSTEM_AREA_FILTERED_BY_DAILY = "system/areas_filtered_by_daily_mainternance"
        private const val SYSTEM_PAIN_AREA = "system/pain_areas"
        private const val SYSTEM_EQUIPMENT = "system/equipments"
        private const val SYSTEM_COLLECTIONS = "system/collections"
        private const val SYSTEM_ACHIEVEMENT = "system/archivement"
        private const val SYSTEM_PRE = "system/pre"
        private const val SYSTEM_POST = "system/post"
        private const val SYSTEM_BONUS = "system/bonus"
        private const val INTRO_MOVIE = "app/intro_videos"
        private const val REVIEW = "app/reviews"
    }

    @GET(SYSTEM_CONFIG)
    @Headers("No-Authentication: true")
    fun systemConfig(): Call<SystemConfigEntity>

    @GET(SYSTEM_AREA)
    @Headers("No-Authentication: true")
    fun systemArea(): Call<SystemAreaEntity>

    @GET(SYSTEM_AREA_FILTERED_BY_DAILY)
    @Headers("No-Authentication: true")
    fun systemAreaFilteredByDaily(): Call<SystemAreaEntity>

    @GET(SYSTEM_PAIN_AREA)
    @Headers("No-Authentication: true")
    fun systemPainArea(): Call<SystemPainAreaEntity>

    @GET(SYSTEM_EQUIPMENT)
    @Headers("No-Authentication: true")
    fun systemEquipment(): Call<SystemEquipmentEntity>

    @GET(SYSTEM_COLLECTIONS)
    @Headers("No-Authentication: true")
    fun systemCollection(): Call<SystemCollectionEntity>

    @GET(SYSTEM_ACHIEVEMENT)
    @Headers("No-Authentication: true")
    fun systemAchievement(): Call<SystemAchievementEntity>

    @GET(SYSTEM_PRE)
    @Headers("No-Authentication: true")
    fun systemPreWorkout(): Call<SystemPreWorkoutEntity>

    @GET(SYSTEM_POST)
    @Headers("No-Authentication: true")
    fun systemPostWorkout(): Call<SystemPostWorkoutEntity>

    @GET(SYSTEM_BONUS)
    @Headers("No-Authentication: true")
    fun systemBonus(): Call<SystemBonusEntity>

    @GET(INTRO_MOVIE)
    @Headers("No-Authentication: true")
    fun getIntroMovie(): Call<MovieListEntity>

    @GET(REVIEW)
    @Headers("No-Authentication: true")
    fun getReview(): Call<ReviewEntity>
}