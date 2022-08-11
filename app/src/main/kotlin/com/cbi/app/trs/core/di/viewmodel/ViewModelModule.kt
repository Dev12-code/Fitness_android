package com.cbi.app.trs.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cbi.app.trs.features.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindsSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthenticateViewModel::class)
    abstract fun bindsAuthenticateViewModel(authenticateViewModel: AuthenticateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DailyViewModel::class)
    abstract fun bindsMovieViewModel(dailyViewModel: DailyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(QuizViewModel::class)
    abstract fun bindsQuizViewModel(quizViewModel: QuizViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel::class)
    abstract fun bindsUserProfileViewModel(userProfileViewModel: UserProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RefreshTokenViewModel::class)
    abstract fun bindsRefreshTokenViewModel(refreshTokenViewModel: RefreshTokenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindsSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoviePlayViewModel::class)
    abstract fun bindsMoviePlayViewModel(moviePlayViewModel: MoviePlayViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun bindsMovieDetailViewModel(movieDetailViewModel: MovieDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PainViewModel::class)
    abstract fun bindsPainViewModel(painViewModel: PainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindsNotificationViewModel(notificationViewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WorkoutViewModel::class)
    abstract fun bindsWorkoutViewModel(workoutViewModel: WorkoutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WorkoutFilterModel::class)
    abstract fun bindsWorkoutFilterModel(workoutFilterModel: WorkoutFilterModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindsPaymentViewModel(paymentViewModel: PaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BonusViewModel::class)
    abstract fun bindsBonusViewModel(bonusViewModel: BonusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MobilityViewModel::class)
    abstract fun bindsMobilityViewModel(mobilityViewModel: MobilityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindsSettingViewModel(settingViewModel: SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ActivityViewModel::class)
    abstract fun bindsActivityViewModel(activityViewModel: ActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroViewModel::class)
    abstract fun bindsIntroViewModel(introViewModel: IntroViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindsHomeViewModel(homeViewModel: HomeViewModel): ViewModel
}