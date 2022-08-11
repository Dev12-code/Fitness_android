package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostUserProfile
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<BaseEntities, Pair<Int?, PostUserProfile.Param>>() {
    data class Param(val data: UserData.UserProfile?)

    override suspend fun run(params: Pair<Int?, Param>) = authenticateRepository.postUserProfile(params.first, params.second)
}