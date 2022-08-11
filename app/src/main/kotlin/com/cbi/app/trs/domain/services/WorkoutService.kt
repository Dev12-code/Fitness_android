package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.WorkoutAPI
import com.cbi.app.trs.domain.entities.workout.CategoryEntity
import com.cbi.app.trs.domain.entities.workout.MovementGuideEntity
import com.cbi.app.trs.domain.entities.workout.WorkoutEntity
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutService
@Inject constructor(retrofit: Retrofit) : WorkoutAPI {
    private val workoutAPI by lazy { retrofit.create(WorkoutAPI::class.java) }
    override fun getWorkoutEquipment(userID: Int, limit: Int, page: Int) = workoutAPI.getWorkoutEquipment(userID, limit, page)

    override fun getWorkoutPrePost(userID: Int, limit: Int, page: Int) = workoutAPI.getWorkoutPrePost(userID, limit, page)

    override fun getWorkoutSport(userID: Int, limit: Int, page: Int) = workoutAPI.getWorkoutSport(userID, limit, page)

    override fun getWorkoutArchetype(userID: Int, limit: Int, page: Int) = workoutAPI.getWorkoutArchetype(userID, limit, page)

    override fun getWarmUp(userID: Int, categoryId: Int, limit: Int, page: Int): Call<WorkoutEntity> {
        return workoutAPI.getWarmUp(userID, categoryId, limit, page)
    }

    override fun getCategoryWorkout(userID: Int?, limit: Int, page: Int) = workoutAPI.getCategoryWorkout(userID, limit, page)

    override fun getCategorySport(userID: Int?, limit: Int, page: Int) = workoutAPI.getCategorySport(userID, limit, page)

    override fun getCategoryArchetype(userID: Int?, limit: Int, page: Int) = workoutAPI.getCategoryArchetype(userID, limit, page)

    override fun getSuggestByCategory(userID: Int?, categoryId: Int, equipmentIds: List<Int>, focusAreas: List<Int>,
                                      minDuration: Int?, maxDuration: Int?, prePostFilter: Int?, collectionSlug: String?, limit: Int, page: Int) = workoutAPI.getSuggestByCategory(userID, categoryId, equipmentIds, focusAreas, minDuration, maxDuration, prePostFilter,
            collectionSlug, limit, page)

    override fun getRelatedByCategory(userID: Int?, categoryId: Int, equipmentIds: List<Int>, focusAreas: List<Int>,
                                      minDuration: Int?, maxDuration: Int?, prePostFilter: Int?, collectionSlug: String?, limit: Int, page: Int) = workoutAPI.getRelatedByCategory(userID, categoryId, equipmentIds, focusAreas, minDuration, maxDuration, prePostFilter, collectionSlug, limit, page)

    override fun getMovementGuide(userID: Int?, categoryId: Int): Call<MovementGuideEntity> {
        return workoutAPI.getMovementGuide(userID, categoryId)
    }
}