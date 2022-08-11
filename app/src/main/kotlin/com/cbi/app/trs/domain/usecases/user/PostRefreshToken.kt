package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostRefreshToken
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<UserData, PostRefreshToken.Params>() {
    data class Params(val refresh_token: String)

    override suspend fun run(params: Params) = authenticateRepository.postRefreshToken(params)
}