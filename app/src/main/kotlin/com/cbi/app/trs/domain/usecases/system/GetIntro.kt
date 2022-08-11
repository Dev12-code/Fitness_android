package com.cbi.app.trs.domain.usecases.system

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.repositories.SystemDataRepository
import javax.inject.Inject

class GetIntro
@Inject constructor(private val systemDataRepository: SystemDataRepository) : UseCase<List<MovieData>, UseCase.None>() {
    override suspend fun run(params: None) = systemDataRepository.getIntro()
}