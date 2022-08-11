package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.QuizStatus
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.quiz.QuizStatusEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.UserService
import com.cbi.app.trs.domain.usecases.quiz.PostQuiz
import javax.inject.Inject

interface QuizRepository {
    fun getQuizStatus(userID: Int): Either<Failure, QuizStatus>
    fun postQuiz(quiz: Pair<Int, PostQuiz.Param>): Either<Failure, BaseEntities>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: UserService,
                        private val userDataCache: UserDataCache) : QuizRepository, BaseNetwork() {
        override fun getQuizStatus(userID: Int): Either<Failure, QuizStatus> {
            return when (networkHandler.isConnected) {
                true -> request(service.getQuizStatus(userID), {
                    it.data
                }, QuizStatusEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postQuiz(quiz: Pair<Int, PostQuiz.Param>): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.postQuiz(quiz.first, quiz.second), {
                    it
                }, BaseEntities.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }


    }
}