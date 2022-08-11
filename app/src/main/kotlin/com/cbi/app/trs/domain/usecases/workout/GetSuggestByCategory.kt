package com.cbi.app.trs.domain.usecases.workout

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.workout.SuggestByCategoryEntity
import com.cbi.app.trs.domain.repositories.WorkoutRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetSuggestByCategory
@Inject constructor(private val workoutRepository: WorkoutRepository) : UseCase<SuggestByCategoryEntity.Data, Pair<Int?, GetSuggestByCategory.Param>>() {
    data class Param(var category_id: Int, var equipment_ids: ArrayList<Int> = ArrayList(), var focus_areas: ArrayList<Int> = ArrayList(),
                     var min_duration: Int? = null, var max_duration: Int? = null, var pre_post_filter: Int? = null, var collection_slug: String? = null) : PagingParam()

    override suspend fun run(params: Pair<Int?, Param>): Either<Failure, SuggestByCategoryEntity.Data> {
        val queries = params.second
        return workoutRepository.getSuggestByCategory(params.first, queries.category_id, queries.equipment_ids, queries.focus_areas, queries.min_duration, queries.max_duration, queries.pre_post_filter,
                queries.collection_slug, queries.limit, queries.page)
    }


}