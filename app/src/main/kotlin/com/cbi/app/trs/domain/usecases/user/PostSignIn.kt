package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostSignIn
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<UserData, PostSignIn.Params>() {
    data class Params(val email: String, val password: String)

    override suspend fun run(params: Params) = authenticateRepository.postSignIn(params)
}