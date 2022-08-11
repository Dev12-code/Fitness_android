package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilityEntity
import com.cbi.app.trs.domain.entities.mobility.MobilityKellyVideoEntity
import com.cbi.app.trs.domain.entities.mobility.MobilitySuggestVideoEntity
import com.cbi.app.trs.domain.entities.mobility.MobilityTestVideoEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.MobilityService
import com.cbi.app.trs.domain.usecases.mobility.UpdateMobilityResult
import javax.inject.Inject

interface MobilityRepository {
    fun getMobilityStatus(userId: Int?): Either<Failure, MobilityStatus>
    fun updateMobilityStatus(userId: Int?, param: UpdateMobilityResult.Param): Either<Failure, BaseEntities>
    fun getMobilityTestVideo(userId: Int?): Either<Failure, MobilityTestVideoEntity.Data>
    fun getMobilitySuggestVideo(userId: Int?): Either<Failure, MobilitySuggestVideoEntity.Data>
    fun getMobilityKellyVideo(userId: Int?): Either<Failure, MovieData>
    fun getMobilityIntroVideo(userId: Int?): Either<Failure, MovieData>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: MobilityService) : MobilityRepository, BaseNetwork() {
        override fun getMobilityStatus(userId: Int?): Either<Failure, MobilityStatus> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMobilityStatus(userId), {
                    it.data
                }, MobilityEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun updateMobilityStatus(userId: Int?, param: UpdateMobilityResult.Param): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.updateMobilityResult(userId, param), {
                    it
                }, BaseEntities.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMobilityTestVideo(userId: Int?): Either<Failure, MobilityTestVideoEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMobilityTestVideos(userId), {
                    it.data
                }, MobilityTestVideoEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMobilitySuggestVideo(userId: Int?): Either<Failure, MobilitySuggestVideoEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMobilitySuggestVideos(userId), {
                    it.data
                }, MobilitySuggestVideoEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMobilityKellyVideo(userId: Int?): Either<Failure, MovieData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMobilityKellyVideos(userId), {
                    it.data
                }, MobilityKellyVideoEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMobilityIntroVideo(userId: Int?): Either<Failure, MovieData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMobilityIntroVideo(userId), {
                    it.data
                }, MobilityKellyVideoEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}