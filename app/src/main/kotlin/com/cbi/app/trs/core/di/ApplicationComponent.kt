package com.cbi.app.trs.core.di

import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.core.di.viewmodel.ViewModelModule
import com.cbi.app.trs.core.navigation.RouteActivity
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.fcm.MyFirebaseMessagingService
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.activities.SplashActivity
import com.cbi.app.trs.features.dialog.EditProfileDialog
import com.cbi.app.trs.features.dialog.NoInternetDialog
import com.cbi.app.trs.features.fragments.bonus_content.BonusContentDetailFragment
import com.cbi.app.trs.features.fragments.bonus_content.BonusContentFragment
import com.cbi.app.trs.features.fragments.daily.DailyFragment
import com.cbi.app.trs.features.fragments.daily.DailyMaintenanceFragment
import com.cbi.app.trs.features.fragments.downloaded.DownloadedFragment
import com.cbi.app.trs.features.fragments.home.HomeFragment
import com.cbi.app.trs.features.fragments.intro.IntroDetailFragment
import com.cbi.app.trs.features.fragments.intro.IntroFragment
import com.cbi.app.trs.features.fragments.login.LoginFragment
import com.cbi.app.trs.features.fragments.login.SignUpFragment
import com.cbi.app.trs.features.fragments.mobility.MobilityFragment
import com.cbi.app.trs.features.fragments.mobility.complete.MobilityCompleteFragment
import com.cbi.app.trs.features.fragments.mobility.kelly.KellyFragment
import com.cbi.app.trs.features.fragments.mobility.list.MobilityTestListFragment
import com.cbi.app.trs.features.fragments.mobility.plan.MobilityPlanFragment
import com.cbi.app.trs.features.fragments.mobility.test.MobilityTestFragment
import com.cbi.app.trs.features.fragments.movies.DownloadService
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment
import com.cbi.app.trs.features.fragments.movies.movies_detail.MovieDetailFragment
import com.cbi.app.trs.features.fragments.movies.pain_detail.PainDetailFragment
import com.cbi.app.trs.features.fragments.pain.PainFragment
import com.cbi.app.trs.features.fragments.payment.PaymentBaseFragment
import com.cbi.app.trs.features.fragments.payment.free_trial.FreeTrialFragment
import com.cbi.app.trs.features.fragments.payment.price.PriceFragment
import com.cbi.app.trs.features.fragments.progress.ProgressFragment
import com.cbi.app.trs.features.fragments.progress.achievement.AchievementCongratulationFragment
import com.cbi.app.trs.features.fragments.progress.achievement.AchievementFragment
import com.cbi.app.trs.features.fragments.progress.histories.HistoriesFragment
import com.cbi.app.trs.features.fragments.progress.leaderboard.LeaderBoardFragment
import com.cbi.app.trs.features.fragments.quiz.QuizEquipmentFragment
import com.cbi.app.trs.features.fragments.quiz.QuizFragment
import com.cbi.app.trs.features.fragments.recovery_password.RecoveryPasswordFragment
import com.cbi.app.trs.features.fragments.recovery_password.UpdateNewPasswordFragment
import com.cbi.app.trs.features.fragments.search.SearchFragment
import com.cbi.app.trs.features.fragments.search.SearchResultFragment
import com.cbi.app.trs.features.fragments.setting.SettingTopFragment
import com.cbi.app.trs.features.fragments.setting.notification.NotificationFragment
import com.cbi.app.trs.features.fragments.setting.user_setting.SettingUserFragment
import com.cbi.app.trs.features.fragments.splash.SplashFragment
import com.cbi.app.trs.features.fragments.subscription.SubscriptionFragment
import com.cbi.app.trs.features.fragments.webview.WebviewFragment
import com.cbi.app.trs.features.fragments.workout_sport.WorkoutSportFragment
import com.cbi.app.trs.features.fragments.workout_sport.detail.WorkoutSportDetailFragment
import com.cbi.app.trs.features.fragments.workout_sport.filter.WorkoutSportFilterFragment
import com.cbi.app.trs.features.fragments.workout_sport.view_all.WorkoutSportViewallFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class, ExoModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(routeActivity: RouteActivity)
    fun inject(movieDetailFragment: MovieDetailFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(downloadService: DownloadService)
    fun inject(splashActivity: SplashActivity)
    fun inject(baseFragment: BaseFragment)
    fun inject(freeTrialFragment: FreeTrialFragment)
    fun inject(priceFragment: PriceFragment)
    fun inject(quizFragment: QuizFragment)
    fun inject(quizEquipmentFragment: QuizEquipmentFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(dailyFragment: DailyFragment)
    fun inject(painFragment: PainFragment)
    fun inject(moviePlayFragment: MoviePlayFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(searchResultFragment: SearchResultFragment)
    fun inject(workoutSportFragment: WorkoutSportFragment)
    fun inject(workoutSportViewallFragment: WorkoutSportViewallFragment)
    fun inject(workoutSportDetailFragment: WorkoutSportDetailFragment)
    fun inject(workoutSportFilterFragment: WorkoutSportFilterFragment)
    fun inject(mobilityFragment: MobilityFragment)
    fun inject(mobilityTestListFragment: MobilityTestListFragment)
    fun inject(mobilityTestFragment: MobilityTestFragment)
    fun inject(mobilityPlanFragment: MobilityPlanFragment)
    fun inject(bonusContentFragment: BonusContentFragment)
    fun inject(mobilityCompleteFragment: MobilityCompleteFragment)
    fun inject(progressFragment: ProgressFragment)
    fun inject(historiesFragment: HistoriesFragment)
    fun inject(bonusContentDetailFragment: BonusContentDetailFragment)
    fun inject(settingTopFragment: SettingTopFragment)
    fun inject(splashFragment: SplashFragment)
    fun inject(kellyFragment: KellyFragment)
    fun inject(paymentBaseFragment: PaymentBaseFragment)
    fun inject(recoveryPasswordFragment: RecoveryPasswordFragment)
    fun inject(painDetailFragment: PainDetailFragment)
    fun inject(notificationFragment: NotificationFragment)
    fun inject(downloadedFragment: DownloadedFragment)
    fun inject(fragment: WebviewFragment)
    fun inject(fragment: AchievementFragment)
    fun inject(fragment: LeaderBoardFragment)
    fun inject(fragment: SettingUserFragment)
    fun inject(fragment: SubscriptionFragment)
    fun inject(fragment: IntroFragment)
    fun inject(fragment: IntroDetailFragment)
    fun inject(fragment: SignUpFragment)
    fun inject(myFirebaseMessagingService: MyFirebaseMessagingService)
    fun inject(dialog: NoInternetDialog)
    fun inject(dialog: EditProfileDialog)
    fun inject(homeFragment: HomeFragment)
    fun inject(dailyMaintenanceFragment: DailyMaintenanceFragment)
    fun inject(achievementCongratulationFragment: AchievementCongratulationFragment)
    fun inject(updateNewPasswordFragment: UpdateNewPasswordFragment)
}
