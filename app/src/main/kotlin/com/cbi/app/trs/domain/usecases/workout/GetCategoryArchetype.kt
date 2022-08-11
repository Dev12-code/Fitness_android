package com.cbi.app.trs.domain.usecases.workout

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.domain.repositories.WorkoutRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetCategoryArchetype
@Inject constructor(private val workoutRepository: WorkoutRepository) : UseCase<List<CategoryData>, Pair<Int?, PagingParam>>() {
    override suspend fun run(params: Pair<Int?, PagingParam>): Either<Failure, List<CategoryData>> {
        val queries = params.second
        return workoutRepository.getCategoryArchetype(params.first, queries.limit, queries.page)
    }


}