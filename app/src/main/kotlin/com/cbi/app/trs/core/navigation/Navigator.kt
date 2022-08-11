package com.cbi.app.trs.core.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentActivity
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.*
import com.cbi.app.trs.domain.usecases.movie.GetSearchResult
import com.cbi.app.trs.features.activities.*
import com.cbi.app.trs.features.fragments.contact_us.ContactUsFragment
import com.cbi.app.trs.features.fragments.daily.DailyMaintenanceFragment
import com.cbi.app.trs.features.fragments.downloaded.DownloadedFragment
import com.cbi.app.trs.features.fragments.home.HomeFragment
import com.cbi.app.trs.features.fragments.intro.IntroFragment
import com.cbi.app.trs.features.fragments.login.Authenticator
import com.cbi.app.trs.features.fragments.login.SignUpFragment
import com.cbi.app.trs.features.fragments.mobility.kelly.KellyFragment
import com.cbi.app.trs.features.fragments.mobility.list.MobilityTestListFragment
import com.cbi.app.trs.features.fragments.mobility.plan.MobilityPlanFragment
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.BONUS_DETAIL
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.CONTENT_FRAGMENT
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOBILITY_RETEST
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOBILITY_STATUS
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOVIE_ID
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.MOVIE_THUMBNAIL
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.OFF_LINE
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment.Companion.PAIN_AREA
import com.cbi.app.trs.features.fragments.payment.price.PriceFragment
import com.cbi.app.trs.features.fragments.progress.ProgressFragment
import com.cbi.app.trs.features.fragments.progress.achievement.AchievementCongratulationFragment
import com.cbi.app.trs.features.fragments.recovery_password.RecoveryPasswordFragment
import com.cbi.app.trs.features.fragments.recovery_password.UpdateNewPasswordFragment
import com.cbi.app.trs.features.fragments.search.SearchFragment
import com.cbi.app.trs.features.fragments.search.SearchResultFragment
import com.cbi.app.trs.features.fragments.setting.SettingTopFragment
import com.cbi.app.trs.features.fragments.setting.notification.NotificationFragment
import com.cbi.app.trs.features.fragments.setting.user_setting.SettingUserFragment
import com.cbi.app.trs.features.fragments.subscription.SubscriptionFragment
import com.cbi.app.trs.features.fragments.webview.WebviewFragment
import com.cbi.app.trs.features.fragments.webview.WebviewFragment.Companion.URI_EXTRA
import com.cbi.app.trs.features.fragments.webview.WebviewFragment.Companion.WEBVIEW_TYPE
import com.cbi.app.trs.features.fragments.workout_sport.detail.WorkoutSportDetailFragment
import com.cbi.app.trs.features.fragments.workout_sport.detail.WorkoutSportDetailFragment.Companion.CATEGORY_DATA
import com.cbi.app.trs.features.fragments.workout_sport.detail.WorkoutSportDetailFragment.Companion.COLLECTION_SLUG
import com.cbi.app.trs.features.fragments.workout_sport.filter.WorkoutSportFilterFragment
import com.cbi.app.trs.features.fragments.workout_sport.view_all.WorkoutSportViewallFragment
import com.cbi.app.trs.features.fragments.workout_sport.view_all.WorkoutSportViewallFragment.Companion.CATEGORY_TYPE
import com.google.android.gms.cast.framework.CastContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Navigator
@Inject constructor(private val authenticator: Authenticator) {

    fun showLogin(context: Activity?) =
        context?.startActivity(LoginActivity.callingIntent(context).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        })

    fun showSplash(context: Activity?) =
        context?.startActivity(SplashActivity.callingIntent(context))

    fun showLanding(context: Activity?) =
        context?.startActivity(MainActivity.callingIntent(context))

    fun showOfflineMode(context: Activity?) = context?.startActivity(
        MainActivity.callingIntent(context).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            putExtra(MainActivity.OFFLINE_MODE, true) })

    fun showMovieDetails(activity: FragmentActivity?, movieData: MovieData) {
        BaseFragment.addFragmentByActivity(activity, MoviePlayFragment().apply {
            arguments = Bundle().apply {
                putInt(MOVIE_ID, movieData.video_id)
                putString(MOVIE_THUMBNAIL, movieData.image_thumbnail)
                putString(CONTENT_FRAGMENT, MoviePlayFragment.ContentFragmentType.MOVIE.value)
            }
        })
    }

    fun showOfflineMovieDetails(activity: FragmentActivity?, movie: MovieData) {
        BaseFragment.addFragmentByActivity(activity, MoviePlayFragment().apply {
            arguments = Bundle().apply {
                putInt(MOVIE_ID, movie.video_id)
                putString(CONTENT_FRAGMENT, MoviePlayFragment.ContentFragmentType.MOVIE.value)
                putBoolean(OFF_LINE, true)
            }
            movieData = movie
            try {
                Handler().postDelayed({ playMovie(movie) }, 500)
            } catch (e: java.lang.Exception) {

            }
        })
    }

    fun showWebBrowser(activity: FragmentActivity?, url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        try {
            activity?.startActivity(i)
        } catch (exception: Exception) {
        }
    }

    fun showPainDetails(activity: FragmentActivity?, painArea: SystemData.PainArea) {
        BaseFragment.addFragmentByActivity(activity, MoviePlayFragment().apply {
            arguments = Bundle().apply {
                putString(CONTENT_FRAGMENT, MoviePlayFragment.ContentFragmentType.PAIN.value)
                putParcelable(PAIN_AREA, painArea)
            }
        })
    }

    fun showRecoveryPassword(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, RecoveryPasswordFragment())
    }

    fun showUpdateNewPassword(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, UpdateNewPasswordFragment())
    }

    fun showQuiz(context: Activity?) {
        context?.startActivity(QuizActivity.callingIntent(context))
    }

    fun showTrial(context: Activity?, userDataCache: UserDataCache) {
        if (userDataCache.get()?.user_profile != null && !userDataCache.get()?.user_profile?.isFreeUser()!!) {
            showLanding(context)
            context?.finish()
        } else {
            context?.startActivity(PaymentActivity.callingIntent(context))
        }
    }

    fun showSubscription(activity: FragmentActivity?, userDataCache: UserDataCache) {
        if (userDataCache.get()?.user_profile != null && !userDataCache.get()?.user_profile?.isFreeUser()!!) {
            BaseFragment.addFragmentByActivity(activity, SubscriptionFragment())
        } else {
            activity?.startActivity(PaymentActivity.callingIntent(activity).apply {
                putExtra(PaymentActivity.TYPE, PaymentActivity.PRICE_FRAGMENT)
            })
        }
    }

    fun openPriceView(activity: FragmentActivity?) {
        activity?.startActivity(PaymentActivity.callingIntent(activity).apply {
            putExtra(PaymentActivity.TYPE, PaymentActivity.PRICE_FRAGMENT)
        })
    }

    fun showContactUs(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, ContactUsFragment())
    }


    fun showMain(context: Activity?, userDataCache: UserDataCache) {
        when (authenticator.userLoggedIn()) {
            true -> {
                showTrial(context, userDataCache)
            }
            false -> showLogin(context)
        }
    }

    fun showPrice(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, PriceFragment())
    }

    fun showSearch(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, SearchFragment())
    }

    fun showDailySearchAndFilter(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, SearchFragment().apply {
            arguments = Bundle().apply {
                putString(SearchFragment.SEARCH_TYPE, SearchFragment.ALLOW_SEARCH_BY_DURATION)
            }
        })
    }

    fun showHome(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, HomeFragment())
    }

    fun showSearchResult(activity: FragmentActivity?, params: GetSearchResult.Params) {
        BaseFragment.addFragmentByActivity(
            activity,
            SearchResultFragment().apply {
                arguments =
                    Bundle().apply { putParcelable(SearchResultFragment.SEARCH_PARAM, params) }
            })
    }

    fun showSearchDailyResult(activity: FragmentActivity?, params: GetSearchResult.Params) {
        BaseFragment.addFragmentByActivity(activity, SearchResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(SearchResultFragment.SEARCH_DAILY_MAINTENANCE, true)
                putParcelable(SearchResultFragment.SEARCH_PARAM, params)
            }
        })
    }

    fun showFavourite(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, SearchResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean("IS_FAVOURITE", true)
                putParcelable(
                    SearchResultFragment.SEARCH_PARAM,
                    GetSearchResult.Params(null, null, null, null, null, null)
                )
            }
        })
    }

    fun showDailyMaintenance(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, DailyMaintenanceFragment())
    }

    fun showWorkoutSportViewall(activity: FragmentActivity?, type: Int) {
        BaseFragment.addFragmentByActivity(
            activity,
            WorkoutSportViewallFragment().apply {
                arguments = Bundle().apply { putInt(CATEGORY_TYPE, type) }
            })
    }

    fun showWorkoutSportDetail(
        activity: FragmentActivity?,
        category: CategoryData,
        collectionSlug: String?
    ) {
        BaseFragment.addFragmentByActivity(activity, WorkoutSportDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(CATEGORY_DATA, category)
                putString(COLLECTION_SLUG, collectionSlug)
            }
        })
    }

    fun showWorkoutSportFilter(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, WorkoutSportFilterFragment())
    }

    fun showMobilityTestList(
        activity: FragmentActivity?,
        currentVideo: MobilityTestVideoData?,
        header: String,
        score: Int?
    ) {
        BaseFragment.addFragmentByActivity(activity,
            MobilityTestListFragment().apply {
                arguments = Bundle().apply {
                    putString(MobilityTestListFragment.HEADER, header)
                    score?.let {
                        putInt(MobilityTestListFragment.SCORE, score)
                    }
                    putParcelable(MobilityTestListFragment.MOBILITY_VIDEO, currentVideo)
                }
            })
    }

    fun showAchievementCongratulation(
        activity: FragmentActivity?,
        achievementData: AchievementData
    ) {
        BaseFragment.addFragmentByActivity(activity, AchievementCongratulationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(AchievementCongratulationFragment.ACHIEVEMENT_DATA, achievementData)
            }
        })
    }

    fun showMobilityTest(
        activity: FragmentActivity?,
        mobilityStatus: MobilityStatus?,
        isRetest: Boolean = false
    ) {
        activity?.supportFragmentManager?.let {
            BaseFragment.setFragment(it, MoviePlayFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        CONTENT_FRAGMENT,
                        MoviePlayFragment.ContentFragmentType.MOBILITY.value
                    )
                    putParcelable(MOBILITY_STATUS, mobilityStatus)
                    putBoolean(MOBILITY_RETEST, isRetest)
                    mFragmentLevel = 2
                }
            })
        }
    }

    fun showMobilityPlan(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, MobilityPlanFragment())
    }

    fun showKellyRecommendation(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, KellyFragment(), R.id.fragmentContainer, true)
    }

    fun showIntroMobility(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, KellyFragment().apply {
            arguments = Bundle().apply {
                putString(KellyFragment.TYPE, KellyFragment.INTRO_TYPE)
            }
        }, R.id.fragmentContainer, true)
    }

    fun showKellyRecommendationFromMobilityTestComplete(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, KellyFragment().apply {
            arguments = Bundle().apply {
                putString(KellyFragment.TYPE, KellyFragment.INTRO_FROM_TEST_COMPLETED)
            }
        }, R.id.fragmentContainer, true)
    }

    fun showSignUpIntroVideo(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, KellyFragment().apply {
            arguments = Bundle().apply {
                putString(KellyFragment.TYPE, KellyFragment.SIGN_UP_INTRO_TYPE)
            }
        }, R.id.fragmentContainer, true)
    }

    fun showProgress(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, ProgressFragment())
    }

    fun showDownloaded(activity: FragmentActivity?) {
//        if ((activity as MainActivity).isOfflineMode) {
//            BaseFragment.setFragment(activity.supportFragmentManager, DownloadedFragment().apply { setFragmentLevel(2) }, R.id.fragmentContainer)
//        } else {
        BaseFragment.addFragmentByActivity(activity, DownloadedFragment(), R.id.fragmentContainer)
//        }
    }

    fun showSetting(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(
            activity,
            SettingTopFragment(),
            R.id.fragmentContainer,
            true
        )
    }

    fun showUserSetting(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, SettingUserFragment())
    }

    fun showBonusDetail(activity: FragmentActivity?, bonus: SystemData.Bonus) {
        BaseFragment.addFragmentByActivity(activity, MoviePlayFragment().apply {
            arguments = Bundle().apply {
                putString(CONTENT_FRAGMENT, MoviePlayFragment.ContentFragmentType.BONUS.value)
                putParcelable(BONUS_DETAIL, bonus)
            }
        })
    }

    fun showNotification(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, NotificationFragment())
    }

    fun showWebView(activity: FragmentActivity?, url: String) {
        BaseFragment.addFragmentByActivity(activity, WebviewFragment().apply {
            arguments = Bundle().apply {
                putString(URI_EXTRA, url)
            }
        })
    }

    fun showPolicy(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, WebviewFragment().apply {
            arguments = Bundle().apply {
                putString(WEBVIEW_TYPE, "policy")
            }
        })
    }

    fun showHelp(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, WebviewFragment().apply {
            arguments = Bundle().apply {
                putString(WEBVIEW_TYPE, "help")
            }
        })
    }

    fun showIntro(activity: FragmentActivity?) {
        BaseFragment.addFragmentByActivity(activity, IntroFragment())
    }

    fun showSignUp(activity: FragmentActivity?, arguments: Bundle?) {
        BaseFragment.addFragmentByActivity(
            activity,
            SignUpFragment().apply { this.arguments = arguments })
    }

    fun showDailySeeMore(activity: FragmentActivity?, params: GetSearchResult.Params) {
        BaseFragment.addFragmentByActivity(activity, SearchResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(SearchResultFragment.SEARCH_DAILY_MAINTENANCE, true)
                putParcelable(SearchResultFragment.SEARCH_PARAM, params)
                putString(
                    SearchResultFragment.TITLE,
                    activity?.getString(R.string.daily_maintenance)
                )
            }
        })
    }

    fun endSession(activity: FragmentActivity) {
        if (activity is LoginActivity) {
            return
        }
        CastContext.getSharedInstance(activity)?.sessionManager?.endCurrentSession(true)
        activity.startActivity(LoginActivity.callingIntentForExpired(activity))
        activity.finish()
    }
}


