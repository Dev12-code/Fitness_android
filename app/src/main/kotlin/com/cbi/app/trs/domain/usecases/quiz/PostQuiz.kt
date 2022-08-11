package com.cbi.app.trs.domain.usecases.quiz

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.QuizRepository
import javax.inject.Inject

class PostQuiz @Inject constructor(private val quizRepository: QuizRepository) : UseCase<BaseEntities, Pair<Int, PostQuiz.Param>>() {
    data class Param(val gender: Int, val activity_level: Int, val experience_mobilizing: Int,
                     val reason_to_join: Int, val my_equipment_ids: ArrayList<Int>)

    override suspend fun run(quiz: Pair<Int, PostQuiz.Param>) = quizRepository.postQuiz(quiz)
}