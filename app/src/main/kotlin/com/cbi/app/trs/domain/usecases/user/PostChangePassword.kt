package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostChangePassword
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<BaseEntities, PostChangePassword.Params>() {
    data class Params(val password: String, val otp_id: String, val email: String)

    override suspend fun run(params: Params) = authenticateRepository.postChangePassword(params)
}