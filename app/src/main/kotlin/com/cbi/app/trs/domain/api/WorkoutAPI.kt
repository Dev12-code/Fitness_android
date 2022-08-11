package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.workout.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface WorkoutAPI {
    companion object {
        private const val UserID = "UserID"
        private const val WORKOUT_EQUIPMENT = "{$UserID}/collection/equipments"
        private const val WORKOUT_PREPOST = "{$UserID}/collection/workouts"
        private const val WORKOUT_SPORT = "{$UserID}/collection/sports"
        private const val WORKOUT_ARCHETYPE = "{$UserID}/collection/archetypes"
        private const val WORKOUT_WARM_UP = "{$UserID}/collection/all"

        private const val WORKOUT_CATEGORY = "{$UserID}/collection/workouts_categories"
        private const val SPORT_CATEGORY = "{$UserID}/collection/sports_categories"
        private const val ARCHETYPE_CATEGORY = "{$UserID}/collection/archetypes_categories"

        private const val SUGGESTION_BY_CATEGORY = "{$UserID}/collection/suggestion_videos"
        private const val RELATED_BY_CATEGORY = "{$UserID}/collection/related_videos"
        private const val MOVEMENT_GUIDE = "{$UserID}/collection/movement_guide"
    }

    @GET(WORKOUT_EQUIPMENT)
    fun getWorkoutEquipment(@Path(UserID) userID: Int, @Query("limit") limit: Int, @Query("page") page: Int): Call<WorkoutEntity>

    @GET(WORKOUT_PREPOST)
    fun getWorkoutPrePost(@Path(UserID) userID: Int, @Query("limit") limit: Int, @Query("page") page: Int): Call<WorkoutEntity>

    @GET(WORKOUT_SPORT)
    fun getWorkoutSport(@Path(UserID) userID: Int, @Query("limit") limit: Int, @Query("page") page: Int): Call<WorkoutEntity>

    @GET(WORKOUT_ARCHETYPE)
    fun getWorkoutArchetype(@Path(UserID) userID: Int, @Query("limit") limit: Int, @Query("page") page: Int): Call<WorkoutEntity>

    @GET(WORKOUT_WARM_UP)
    fun getWarmUp(@Path(UserID) userID: Int, @Query("filter_category_id") categoryId: Int, @Query("limit") limit: Int, @Query("page") page: Int): Call<WorkoutEntity>

    @GET(WORKOUT_CATEGORY)
    fun getCategoryWorkout(@Path(UserID) userID: Int?, @Query("limit") limit: Int, @Query("page") page: Int): Call<CategoryEntity>

    @GET(SPORT_CATEGORY)
    fun getCategorySport(@Path(UserID) userID: Int?, @Query("limit") limit: Int, @Query("page") page: Int): Call<CategoryEntity>

    @GET(ARCHETYPE_CATEGORY)
    fun getCategoryArchetype(@Path(UserID) userID: Int?, @Query("limit") limit: Int, @Query("page") page: Int): Call<CategoryEntity>


    @GET(SUGGESTION_BY_CATEGORY)
    fun getSuggestByCategory(@Path(UserID) userID: Int?, @Query("category_id") categoryId: Int, @Query("equipment_ids[]") equipmentIds: List<Int>, @Query("focus_areas[]") focusAreas: List<Int>,
                             @Query("min_duration") minDuration: Int?, @Query("max_duration") maxDuration: Int?,
                             @Query("pre_post_filter") prePostFilter: Int?, @Query("collection_slug") collectionSlug: String?, @Query("limit") limit: Int, @Query("page") page: Int): Call<SuggestByCategoryEntity>

    @GET(RELATED_BY_CATEGORY)
    fun getRelatedByCategory(@Path(UserID) userID: Int?, @Query("category_id") categoryId: Int, @Query("equipment_ids[]") equipmentIds: List<Int>, @Query("focus_areas[]") focusAreas: List<Int>,
                             @Query("min_duration") minDuration: Int?, @Query("max_duration") maxDuration: Int?,
                             @Query("pre_post_filter") prePostFilter: Int?, @Query("collection_slug") collectionSlug: String?, @Query("limit") limit: Int, @Query("page") page: Int): Call<RelatedByCategoryEntity>

    @GET(MOVEMENT_GUIDE)
    fun getMovementGuide(@Path(UserID) userID: Int?, @Query("category_id") categoryId: Int): Call<MovementGuideEntity>
}