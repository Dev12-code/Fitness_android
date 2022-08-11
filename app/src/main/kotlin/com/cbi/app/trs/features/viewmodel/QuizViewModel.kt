package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.QuizStatus
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.quiz.GetQuizStatus
import com.cbi.app.trs.domain.usecases.quiz.PostQuiz
import javax.inject.Inject

class QuizViewModel
@Inject constructor(private val getQuizStatus: GetQuizStatus, private val postQuiz: PostQuiz) : BaseViewModel() {
    var quizStatus: MutableLiveData<QuizStatus> = MutableLiveData()
    var quizUpdate: MutableLiveData<BaseEntities> = MutableLiveData()

    fun getQuizStatus(userID: Int) = getQuizStatus(userID) { it.fold(::handleFailure, ::handleQuizStatus) }
    fun updateQuiz(pair: Pair<Int, PostQuiz.Param>) = postQuiz(pair) { it.fold(::handleFailure, ::handleQuizUpdate) }

    fun handleQuizUpdate(quizUpdate: BaseEntities) {
        this.quizUpdate.value = quizUpdate
    }

    fun handleQuizStatus(quizStatus: QuizStatus) {
        this.quizStatus.value = quizStatus
    }
}