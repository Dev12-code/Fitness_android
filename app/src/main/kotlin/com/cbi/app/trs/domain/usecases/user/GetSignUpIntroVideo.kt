package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class GetSignUpIntroVideo @Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<MovieData, Int?>() {
    override suspend fun run(userId: Int?) = authenticateRepository.getSignUpIntroVideo(userId)

}