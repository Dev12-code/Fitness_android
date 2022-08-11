package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.movie.MovieDetailEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.PainService
import com.cbi.app.trs.domain.usecases.pain.GetStartedPain
import com.cbi.app.trs.domain.usecases.pain.GetUnderstandingPain
import javax.inject.Inject

interface PainRepository {
    fun getUnderstandingPain(userId: Int?, painAreaId: Int?, filter: String?, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data>
    fun getMobilityRxPain(userId: Int?, painAreaId: Int?, filter: String?, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data>
    fun getStartedPain(userId: Int?, painAreaId: Int?): Either<Failure, MovieData>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: PainService) : PainRepository, BaseNetwork() {

        override fun getUnderstandingPain(userId: Int?, painAreaId: Int?, filter: String?, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUnderstandingPain(userId, painAreaId, filter, limit, page), {
                    it.data
                }, SearchMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMobilityRxPain(userId: Int?, painAreaId: Int?, filter: String?, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMobilityRxPain(userId, painAreaId, filter, limit, page), {
                    it.data
                }, SearchMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getStartedPain(userId: Int?, painAreaId: Int?): Either<Failure, MovieData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getStartedPain(userId, painAreaId), {
                    it.data
                }, MovieDetailEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}