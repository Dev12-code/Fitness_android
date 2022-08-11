package com.cbi.app.trs.features.fragments.splash

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.QuizStatus
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.features.fragments.login.Authenticator
import com.cbi.app.trs.features.viewmodel.QuizViewModel
import com.cbi.app.trs.features.viewmodel.RefreshTokenViewModel
import com.cbi.app.trs.features.viewmodel.SplashViewModel
import javax.inject.Inject

class SplashFragment : LightBaseFragment() {
    override fun layoutId() = R.layout.fragment_splash

    var apiCount: Int = 0

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    lateinit var authenticator: Authenticator

    private lateinit var splashViewModel: SplashViewModel

    private lateinit var quizViewModel: QuizViewModel

    private lateinit var refreshTokenViewModel: RefreshTokenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        splashViewModel = viewModel(viewModelFactory) {
//            observe(systemConfig, ::onReceiveSystemConfig)
            observe(systemArea, ::onReceiveSystemArea)
//            observe(systemAreaFiltered, ::onReceiveSystemAreaFiltered)
//            observe(systemPainArea, ::onReceiveSystemPainArea)
            observe(systemEquipment, ::onReceiveSystemEquipment)
//            observe(systemCollection, ::onReceiveSystemCollection)
//            observe(systemAchievement, ::onReceiveSystemAchievement)
//            observe(systemPreWorkout, ::onReceiveSystemPreWorkout)
//            observe(systemPostWorkout, ::onReceiveSystemPostWorkout)
//            observe(systemBonus, ::onReceiveSystemBonus)
            observe(userProfile, ::onReceiveUserProfile)

            failure(failureData, ::handleFailure)
        }

        quizViewModel = viewModel(viewModelFactory) {
            observe(quizStatus, ::onReceiveQuizStatus)
            failure(failureData, ::handleFailure)
        }

        refreshTokenViewModel = viewModel(viewModelFactory) {
            observe(refreshTokenData, ::onRefreshTokenSuccessfully)
            observe(failureRefreshData, ::handleFailureRefresh)
        }
    }

    private fun handleFailureRefresh(failure: Failure?) {
        if (failure is Failure.NetworkConnection) {
            navigator.showLanding(activity)
            activity?.finish()
        } else {
            userDataCache.clear()
            mNavigator.showLogin(activity)
        }
    }

    private fun onRefreshTokenSuccessfully(data: UserData?) {
        loadData()
    }


    private fun onReceiveUserProfile(userProfile: UserData.UserProfile?) {
        apiCount--
        count()
    }

    private fun count() {
        if (apiCount > 0) return
        apiCount = 0

        //get quiz status first
        if (authenticator.userLoggedIn()) {
            userDataCache.get()?.user_token?.userID?.let {
                quizViewModel.getQuizStatus(it)
            }
        } else {
            navigator.showMain(activity, userDataCache)
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (firstTimeCreated(savedInstanceState)) {
            if (authenticator.userLoggedIn()) {
                //call API to refresh token
                userDataCache.get()?.user_token?.refresh_token?.let {
                    refreshTokenViewModel.refreshToken(
                        it
                    )
                }
            } else {
                loadData()
            }
        }
    }

    private fun loadData() {
        apiCount = 0
        if (authenticator.userLoggedIn()) {
            splashViewModel.getUserProfile(userID)
            apiCount++
        }
        splashViewModel.loadSystemEquipment()
        apiCount++
        splashViewModel.loadSystemArea()
        apiCount++
    }

    private fun onReceiveQuizStatus(quizStatus: QuizStatus?) {
        hideProgress()
        if (quizStatus == null) {
            navigator.showMain(activity, userDataCache)
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
            return
        }
        if (quizStatus.initial_quiz == 0) {//Not yet update quiz
            navigator.showQuiz(activity)
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        } else {
            navigator.showMain(activity, userDataCache)
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }


//        splashViewModel.loadSystemAchievement()
//        splashViewModel.loadSystemCollection()
//        splashViewModel.loadSystemPreWorkout()
//        splashViewModel.loadSystemPostWorkout()
//        splashViewModel.loadSystemBonus()
    }

    override fun onReloadData() {
        super.onReloadData()
        loadData()
    }

    private fun onReceiveSystemConfig(data: SystemData.Config?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemArea(data: List<SystemData.Area>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemAreaFiltered(data: List<SystemData.Area>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemPainArea(data: List<SystemData.PainArea>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemEquipment(data: List<SystemData.Equipment>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemAchievement(data: List<SystemData.Achievement>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemCollection(data: List<SystemData.Collection>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemPreWorkout(data: List<SystemData.PreWorkout>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemPostWorkout(data: List<SystemData.PostWorkout>?) {
        apiCount--
        count()
    }

    private fun onReceiveSystemBonus(data: List<SystemData.Bonus>?) {
        apiCount--
        count()
    }
}