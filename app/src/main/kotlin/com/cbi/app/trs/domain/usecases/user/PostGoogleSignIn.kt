package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostGoogleSignIn
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<UserData, PostGoogleSignIn.Params>() {
    data class Params(val google_token: String)

    override suspend fun run(params: Params): Either<Failure, UserData> = authenticateRepository.postGoogleSignIn(params)
}