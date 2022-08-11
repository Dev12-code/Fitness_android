package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.workout.RelatedByCategoryEntity
import com.cbi.app.trs.domain.entities.workout.SuggestByCategoryEntity
import com.cbi.app.trs.domain.entities.workout.WorkoutEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.workout.*
import javax.inject.Inject

class WorkoutViewModel @Inject constructor(private val getCategoryArchetype: GetCategoryArchetype,
                                           private val getCategorySport: GetCategorySport,
                                           private val getCategoryWorkout: GetCategoryWorkout,
                                           private val getSuggestByCategory: GetSuggestByCategory,
                                           private val getMovementGuide: GetMovementGuide,
                                           private val getRelatedByCategory: GetRelatedByCategory,
                                           private val getWorkOutWarmUp: GetWorkOutWarmUp
)

    : BaseViewModel() {
    val categoryWorkoutData: MutableLiveData<List<CategoryData>> = MutableLiveData()
    val categorySportData: MutableLiveData<List<CategoryData>> = MutableLiveData()
    val workoutWarmUpData: MutableLiveData<WorkoutEntity.Data> = MutableLiveData()
    val categoryArchetypeData: MutableLiveData<List<CategoryData>> = MutableLiveData()
    val suggestByCategoryData: MutableLiveData<SuggestByCategoryEntity.Data> = MutableLiveData()
    val suggestPostByCategoryData: MutableLiveData<SuggestByCategoryEntity.Data> = MutableLiveData()
    val relatedByCategoryData: MutableLiveData<RelatedByCategoryEntity.Data> = MutableLiveData()
    val movementGuideData: MutableLiveData<MovieData> = MutableLiveData()

    fun getCategoryWorkout(userId: Int?, pagingParam: PagingParam) = getCategoryWorkout(Pair(userId, pagingParam)) { it.fold(::handleFailure, ::handleCategoryWorkout) }

    private fun handleCategoryWorkout(list: List<CategoryData>) {
        this.categoryWorkoutData.value = list
    }

    fun getCategorySport(userId: Int?, pagingParam: PagingParam) = getCategorySport(Pair(userId, pagingParam)) { it.fold(::handleFailure, ::handleCategorySport) }

    private fun handleCategorySport(list: List<CategoryData>) {
        this.categorySportData.value = list
    }

    fun getWarmUp(userId: Int, pagingParam: GetWorkOutWarmUp.Params){
        return getWorkOutWarmUp(Pair(userId, pagingParam)) { it.fold(::handleFailure, ::handleCategoryWarmUp) }
    }

    private fun handleCategoryWarmUp(list: WorkoutEntity.Data) {
        this.workoutWarmUpData.value = list
    }

    fun getCategoryArchetype(userId: Int?, pagingParam: PagingParam) = getCategoryArchetype(Pair(userId, pagingParam)) { it.fold(::handleFailure, ::handleCategoryArchetype) }

    private fun handleCategoryArchetype(list: List<CategoryData>) {
        this.categoryArchetypeData.value = list
    }

    fun getSuggestByCategory(userId: Int?, param: GetSuggestByCategory.Param) = getSuggestByCategory(Pair(userId, param)) { it.fold(::handleFailure, ::handleSuggestByCategory) }

    private fun handleSuggestByCategory(data: SuggestByCategoryEntity.Data) {
        this.suggestByCategoryData.value = data
    }

    fun getSuggestPostByCategory(userId: Int?, param: GetSuggestByCategory.Param) = getSuggestByCategory(Pair(userId, param)) { it.fold(::handleFailure, ::handleSuggestPostByCategory) }

    private fun handleSuggestPostByCategory(data: SuggestByCategoryEntity.Data) {
        this.suggestPostByCategoryData.value = data
    }

    fun getRelatedByCategory(userId: Int?, param: GetSuggestByCategory.Param) = getRelatedByCategory(Pair(userId, param)) { it.fold(::handleFailure, ::handleRelatedByCategory) }

    private fun handleRelatedByCategory(data: RelatedByCategoryEntity.Data) {
        this.relatedByCategoryData.value = data
    }

    fun getMovementGuide(userId: Int?, param: GetMovementGuide.Param) = getMovementGuide(Pair(userId, param)) { it.fold(::handleFailure, ::handleMovementGuide) }

    private fun handleMovementGuide(data: MovieData) {
        this.movementGuideData.value = data
    }
}