package com.cbi.app.trs.domain.entities.quiz

import com.cbi.app.trs.data.entities.QuizStatus
import com.cbi.app.trs.domain.entities.BaseEntities

data class QuizStatusEntity(val data: QuizStatus) : BaseEntities() {
    companion object {
        fun empty() = QuizStatusEntity(QuizStatus(0))
    }
}