package com.cbi.app.trs.domain.usecases.system

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.ReviewData
import com.cbi.app.trs.domain.repositories.SystemDataRepository
import javax.inject.Inject

class GetReview
@Inject constructor(private val systemDataRepository: SystemDataRepository) : UseCase<List<ReviewData>, UseCase.None>() {
    override suspend fun run(params: None) = systemDataRepository.getReview()
}