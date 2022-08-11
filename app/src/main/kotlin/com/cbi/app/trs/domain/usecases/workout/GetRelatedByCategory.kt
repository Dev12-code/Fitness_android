package com.cbi.app.trs.domain.usecases.workout

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.workout.RelatedByCategoryEntity
import com.cbi.app.trs.domain.repositories.WorkoutRepository
import javax.inject.Inject

class GetRelatedByCategory
@Inject constructor(private val workoutRepository: WorkoutRepository) : UseCase<RelatedByCategoryEntity.Data, Pair<Int?, GetSuggestByCategory.Param>>() {
    override suspend fun run(params: Pair<Int?, GetSuggestByCategory.Param>): Either<Failure, RelatedByCategoryEntity.Data> {
        val queries = params.second
        return workoutRepository.getRelatedByCategory(params.first, queries.category_id, queries.equipment_ids, queries.focus_areas, queries.min_duration, queries.max_duration, queries.pre_post_filter,
                queries.collection_slug, queries.limit, queries.page)
    }

}