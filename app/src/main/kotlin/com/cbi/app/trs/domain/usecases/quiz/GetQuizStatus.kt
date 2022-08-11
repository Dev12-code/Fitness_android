package com.cbi.app.trs.domain.usecases.quiz

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.QuizStatus
import com.cbi.app.trs.domain.repositories.QuizRepository
import javax.inject.Inject

class GetQuizStatus @Inject constructor(private val quizRepository: QuizRepository) : UseCase<QuizStatus, Int>() {

    override suspend fun run(userId: Int) = quizRepository.getQuizStatus(userId)
}