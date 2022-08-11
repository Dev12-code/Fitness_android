package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PostUserAvatar
@Inject constructor(private val authenticateRepository: AuthenticateRepository) : UseCase<BaseEntities, Pair<Int?, MultipartBody.Part>>() {

    override suspend fun run(params: Pair<Int?, MultipartBody.Part>) = authenticateRepository.postUserAvatar(params.first, params.second)
}