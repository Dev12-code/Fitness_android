package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostSignUp
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<UserData, PostSignIn.Params>() {

    override suspend fun run(params: PostSignIn.Params) = authenticateRepository.postSignUp(params)
}