package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

class PostDeviceToken
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<BaseEntities, Pair<Int?, PostDeviceToken.Params>>() {
    data class Params(val device_id: String = "", val device_type: String = "android", val device_token: String = "")

    override suspend fun run(params: Pair<Int?, PostDeviceToken.Params>) = authenticateRepository.postDeviceToken(params.first, params.second)
}