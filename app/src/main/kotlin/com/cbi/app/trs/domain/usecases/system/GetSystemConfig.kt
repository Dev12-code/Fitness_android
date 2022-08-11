package com.cbi.app.trs.domain.usecases.system

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.system.SystemConfigEntity
import com.cbi.app.trs.domain.repositories.SystemDataRepository
import javax.inject.Inject

class GetSystemConfig
@Inject constructor(private val systemDataRepository: SystemDataRepository) : UseCase<SystemConfigEntity.Data, UseCase.None>() {
    override suspend fun run(params: None) = systemDataRepository.getSystemConfig()
}