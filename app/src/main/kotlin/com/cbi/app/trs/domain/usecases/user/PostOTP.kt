package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostOTP
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<BaseEntities, PostOTP.Params>() {
    data class Params(val email: String)

    override suspend fun run(params: Params) = authenticateRepository.postOTP(params)
}