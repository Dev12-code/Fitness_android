package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.HtmlData
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import javax.inject.Inject

data class GetPolicy
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<HtmlData, Int?>() {

    override suspend fun run(userID: Int?) = authenticateRepository.getPolicy(userID)
}