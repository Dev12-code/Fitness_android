package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostUpdateNewPassword
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<BaseEntities, Pair<Int?, PostUpdateNewPassword.Params>>() {
    data class Params(val old_password: String, val new_password: String, val rematch_new_password: String)

    override suspend fun run(params: Pair<Int?, Params>): Either<Failure, BaseEntities> {
        return authenticateRepository.postUpdateNewPassword(params.first, params.second)
    }
}