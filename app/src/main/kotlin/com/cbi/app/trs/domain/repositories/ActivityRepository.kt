package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.entities.AchievementData
import com.cbi.app.trs.data.entities.LeaderBoardData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.activity.AchievementEntity
import com.cbi.app.trs.domain.entities.activity.LeaderBoardEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.ActivityService
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

interface ActivityRepository {
    fun getHistory(userId: Int?, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data>
    fun getAchievement(userId: Int?): Either<Failure, AchievementEntity.Data>
    fun getLeaderBoard(userId: Int?): Either<Failure, LeaderBoardData>
    fun trackingUserStreak(userId: Int?): Either<Failure, BaseEntities>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: ActivityService) : ActivityRepository, BaseNetwork() {

        override fun getHistory(userId: Int?, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getHistory(userId, limit, page), {
                    it.data
                }, SearchMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getAchievement(userId: Int?): Either<Failure, AchievementEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getAchievement(userId), {
                    it.data
                }, AchievementEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getLeaderBoard(userId: Int?): Either<Failure, LeaderBoardData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getLeaderBoard(userId), {
                    it.data
                }, LeaderBoardEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun trackingUserStreak(userId: Int?): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.trackingUserStreak(userId), {
                    it
                }, BaseEntities.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}