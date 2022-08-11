package com.cbi.app.trs.features.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.BuildConfig
import com.cbi.app.trs.R
import com.cbi.app.trs.core.di.ApplicationComponent
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.system.SystemConfigEntity
import com.cbi.app.trs.domain.eventbus.LostConnectionEvent
import com.cbi.app.trs.domain.eventbus.ReloginAgainEvent
import com.cbi.app.trs.domain.eventbus.ShowPopupPostVideoEvent
import com.cbi.app.trs.domain.eventbus.ShowPopupReminderIAP
import com.cbi.app.trs.features.alarms.AlarmReceiver
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.dialog.NoInternetDialog
import com.cbi.app.trs.features.dialog.QuestionnaireDialog
import com.cbi.app.trs.features.fragments.bonus_content.BonusContentFragment
import com.cbi.app.trs.features.fragments.daily.DailyFragment
import com.cbi.app.trs.features.fragments.login.Authenticator
import com.cbi.app.trs.features.fragments.mobility.MobilityFragment
import com.cbi.app.trs.features.fragments.pain.PainFragment
import com.cbi.app.trs.features.fragments.workout_sport.WorkoutSportFragment
import com.cbi.app.trs.features.utils.*
import com.cbi.app.trs.features.viewmodel.ActivityViewModel
import com.cbi.app.trs.features.viewmodel.MobilityViewModel
import com.cbi.app.trs.features.viewmodel.RefreshTokenViewModel
import com.cbi.app.trs.features.viewmodel.SplashViewModel
import com.google.android.gms.cast.framework.CastContext
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject


private const val APP_UPDATE_REQUEST_CODE = 1991

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    LifecycleObserver {

    companion object {
        fun callingIntent(context: Context) = Intent(context, MainActivity::class.java)
        const val OFFLINE_MODE = "OFFLINE_MODE"
    }

    override fun fragment() = DailyFragment().apply { arguments = intent.extras }


    lateinit var navigationView: View

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    lateinit var userDataCache: UserDataCache

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var authenticator: Authenticator

    lateinit var mobilityViewModel: MobilityViewModel

    lateinit var activityViewModel: ActivityViewModel

    private lateinit var questionnaireDialog: QuestionnaireDialog

    private var countdownTimer: CountdownTimer? = null

    private var refreshTokenTimer: CountdownTimer? = null

    private var retryTime = 0

    private var isAllowRefreshToken: Boolean = true

    private lateinit var refreshTokenViewModel: RefreshTokenViewModel

    private lateinit var splashViewModel: SplashViewModel


    // Creates instance of the manager.
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                        this
                    )
                    else -> AppLog.d("MainActivity", installState.installStatus().toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiRequestCount = 0
        appComponent.inject(this)
        mobilityViewModel = viewModel(viewModelFactory) {
            observe(mobilityStatus, ::onReceiveMobilityStatus)
        }
        activityViewModel = viewModel(viewModelFactory) {
            observe(trackingUserStreak, ::onReceiveTrackingStreak)
            failure(failureDataTracking, ::handleFailureTracking)
        }

        refreshTokenViewModel = viewModel(viewModelFactory) {
            observe(refreshTokenData, ::onRefreshTokenSuccessfully)
            observe(failureRefreshData, ::handleFailureRefresh)
        }

        splashViewModel = viewModel(viewModelFactory) {
            observe(systemConfig, ::onReceiveSystemConfig)
        }
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.navigation_view)
        navigation.setOnNavigationItemSelectedListener(this)
        changeFullScreenMode()
        isOfflineMode = intent.getBooleanExtra(OFFLINE_MODE, false)
        if (isOfflineMode) {
            Handler().postDelayed({ navigator.showDownloaded(this) }, 500)
        }

        splashViewModel.loadSystemConfig()

        //register observer and eventbus
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {

        }
        val pastDate = sharedPreferences.getString("ASK_ME_QUESTION_DATE", "")
        if (isAfterToday(pastDate)) {
            //get mobility status
            userDataCache.get()?.user_token?.userID?.let {
                mobilityViewModel.getMobilityStatus(it)
            }

            questionnaireDialog =
                QuestionnaireDialog().setListener(DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        -1 -> {
                            //open homepage
                            navigator.showHome(this)
                        }
                        1 -> {
                        }
                        2 -> {
                            selectNavigation(R.id.navigation_workout)
                        }
                        3 -> {
                            selectNavigation(R.id.navigation_pain)
                        }
                        else -> {
                            selectNavigation(R.id.navigation_mobility)
                        }
                    }

                })
            questionnaireDialog.show(activity = this)

            val today = Calendar.getInstance()
            sharedPreferences.edit().putString(
                "ASK_ME_QUESTION_DATE",
                "${today.get(Calendar.MONTH)}/${today.get(Calendar.DATE)}/${today.get(Calendar.YEAR)}"
            ).apply()
        }
        onSetupReminderIAP()
        trackingUserStreak()
        //reset index banner
        sharedPreferences.edit().putInt("Index", 1).apply()
        KeyboardUtil(this, findViewById(R.id.fragmentContainer))
    }

    private fun onRefreshTokenSuccessfully(data: UserData?) {

    }

    private fun trackingUserStreak() {
        //tracking streak
        userDataCache.get()?.user_token?.userID?.let {
            activityViewModel.trackingUserStreak(it)
        }
    }

    private fun onReceiveSystemConfig(data: SystemConfigEntity.Data?) {
        if (data == null) {
            return
        }
        val minimumVersion = data.minimum_version_android
        val currentVersion = BuildConfig.VERSION_NAME

        if (CommonUtils.compareVersion(currentVersion, minimumVersion) > 0) {
            //play with force update mode
            checkForUpdate(AppConstants.UPDATE_TYPE_IMMEDIATE)
        } else {
            //optional mode
            checkForUpdate(AppConstants.UPDATE_TYPE_FLEXIBLE)
        }
    }

    private fun handleFailureRefresh(failure: Failure?) {
        forceHide()
        BaseFragment.isShowUnAuthorized = true
        userDataCache.clear()
        navigator.endSession(this)
    }

    private fun handleFailureTracking(failure: Failure?) {
//        when (failure) {
//            is Failure.ServerError -> {
//                if (failure.errorCode == 401 || failure.errorCode == 403) {
//                    //instead of auto call refresh token, will force user login again
//                    DialogAlert()
//                        .setTitle("This account is already logged into on another device.")
//                        .setMessage("You are currently logged in on another device. Please log out of the other device or contact your administrator.")
//                        .setCancel(false)
//                        .setTitlePositive("OK")
//                        .onPositive {
//                            userDataCache.clear()
//                            navigator.endSession(this)
//                        }
//                        .show(activity = this)
//                    return
//                }
//            }
//        }
//
//        retryTime++
//        if (retryTime >= 5) {
//            //show dialog alert
//            DialogAlert().setTitle("Request Tracking Fail").setMessage("Do you want to try again?")
//                .setTitleNegative("Cancel").setTitlePositive("Retry").onPositive {
//                    retryTime = 0
//                    trackingUserStreak()
//                }.show(activity = this)
//        } else {
//            trackingUserStreak()
//        }
    }

    private fun onReceiveMobilityStatus(mobilityStatus: MobilityStatus?) {
        hideProgress()
        //user already do a test
        mobilityStatus?.let {
            if (it.test_date > 0) {
                //check 14 days from the last test
                if (it.test_date + 14 * 24 * 60 * 60 <= System.currentTimeMillis() / 1000) {
                    //enable mobility as popup
                    if (::questionnaireDialog.isInitialized) {
                        questionnaireDialog.setEnableMobility(true)
                    }
                }
            } else {
                //user haven't done a test
                if (::questionnaireDialog.isInitialized) {
                    questionnaireDialog.setEnableMobility(true)
                }
            }
        }
    }


    private fun onSetupReminderIAP() {
        if (userDataCache.get()?.user_profile == null) {
            return
        }
        if (userDataCache.get()!!.user_profile!!.isFreeUser()) {
            return
        }

        //set a reminder to notify for user login
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        notifyIntent.putExtra("type", AppConstants.REMINDER_IAP)

        val notifyPendingIntent = PendingIntent.getBroadcast(
            this,
            AppConstants.REQUEST_CODE_REMINDER_IAP,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Alarm time in 24 hour
        val DELAY_IN_SECOND = 24 * 60 * 60

        val currentTime = Calendar.getInstance().timeInMillis
        val triggerTime =
            userDataCache.get()!!.user_profile?.plan_expire_date!! * 1000 - DELAY_IN_SECOND * 1_000L
        if (currentTime > triggerTime) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, notifyPendingIntent)
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                notifyPendingIntent
            )

        }

    }

    private fun onReceiveTrackingStreak(baseEntities: BaseEntities?) {
        hideProgress()
        retryTime = 0
        //set a reminder to notify for user login
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        notifyIntent.putExtra("type", AppConstants.REMINDER_LOGIN)

        val notifyPendingIntent = PendingIntent.getBroadcast(
            this,
            AppConstants.REQUEST_CODE_REMINDER_LOGIN,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Alarm time in 23 hour
        val DELAY_IN_SECOND = 23 * 60 * 60

        val triggerTime = System.currentTimeMillis() + DELAY_IN_SECOND * 1_000L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, notifyPendingIntent)
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                notifyPendingIntent
            )

        }

        val countdownTime = 23 * 60 * 60 * 1000 //23h
        //start timer to countdown, 23h from last tracking, will call API tracking again
        countdownTimer = object : CountdownTimer(countdownTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                //tracking streak
                trackingUserStreak()
            }

        }
        (countdownTimer as CountdownTimer).start()
    }

    private fun isAfterToday(date: String?): Boolean {
        if (date.isNullOrEmpty()) return true

        val month = date.split("/")[0].toInt()
        val day = date.split("/")[1].toInt()
        val year = date.split("/")[2].toInt()

        val today = Calendar.getInstance()
        if (today.get(Calendar.YEAR) < year) {
            return false
        } else if (today.get(Calendar.YEAR) == year) {
            if (today.get(Calendar.MONTH) < month) {
                return false
            } else if (today.get(Calendar.MONTH) == month) {
                if (today.get(Calendar.DATE) <= day) {
                    return false
                }
            }
        }
        return true
    }


    fun selectNavigation(menuID: Int) {
        navigation.selectedItemId = menuID
    }

    override fun onDestroy() {
        CastContext.getSharedInstance(this)?.sessionManager?.endCurrentSession(true)
        // unregister observer
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        EventBus.getDefault().unregister(this)
        countdownTimer?.cancel()
        refreshTokenTimer?.cancel()
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_daily -> {
                if (!getCurrentFragmentName().equals(
                        DailyFragment::class.java.simpleName,
                        true
                    )
                ) {
                    BaseFragment.setFragment(supportFragmentManager, DailyFragment())
                }
                return true
            }
            R.id.navigation_pain -> {
                if (!getCurrentFragmentName().equals(
                        PainFragment::class.java.simpleName,
                        true
                    )
                ) {
                    BaseFragment.setFragment(supportFragmentManager, PainFragment())
                }
                return true
            }
            R.id.navigation_workout -> {
//                if (!isAllowForFreemium()) return false
                if (!getCurrentFragmentName().equals(
                        WorkoutSportFragment::class.java.simpleName,
                        true
                    )
                ) {
                    BaseFragment.setFragment(supportFragmentManager, WorkoutSportFragment())
                }
                return true
            }
            R.id.navigation_mobility -> {
                if (!getCurrentFragmentName().equals(
                        MobilityFragment::class.java.simpleName,
                        true
                    )
                ) {
                    BaseFragment.setFragment(supportFragmentManager, MobilityFragment())
                }
                return true
            }
            R.id.navigation_bonus -> {
                if (!isAllowForFreemium()) return false
                if (!getCurrentFragmentName().equals(
                        BonusContentFragment::class.java.simpleName,
                        true
                    )
                ) {
                    BaseFragment.setFragment(supportFragmentManager, BonusContentFragment())
                }
                return true
            }
        }
        return false
    }


    fun isAllowForFreemium(show: Boolean = true): Boolean {
        if (userDataCache.get()?.user_profile == null) {
            if (show) DialogAlert().setTitle("Freemium")
                .setMessage("You cannot use this feature with a free account. Please upgrade to experience the full App.")
                .setTitleNegative("Cancel").setTitlePositive("Upgrade").onPositive {
                    navigator.openPriceView(this)
                }.show(context = this)
            return false
        }
        if (userDataCache.get()!!.user_profile!!.isFreeUser()) {
            if (show) DialogAlert().setTitle("Freemium")
                .setMessage("You cannot use this feature with a free account. Please upgrade to experience the full App.")
                .setTitleNegative("Cancel").setTitlePositive("Upgrade").onPositive {
                    navigator.openPriceView(this)
                }.show(context = this)
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        registerNetworkListener()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                try {
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackBarForCompleteUpdate()
                    }

                    //Check if Immediate update is required

                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            APP_UPDATE_REQUEST_CODE
                        )
                    }
                } catch (e: IntentSender.SendIntentException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
    }


    fun refreshToken() {
        userDataCache.get()?.user_token?.refresh_token?.let {
            refreshTokenViewModel.refreshToken(
                it
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() { // app moved to foreground
        AndroidApplication.activityResumed()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() { // app moved to background
        AndroidApplication.activityPaused()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkListener()
    }

    @Subscribe
    fun onFilterUpdateEvent(event: LostConnectionEvent) {
        if (!isShowNoInternet) {
            isShowNoInternet = true

            noInternetDialog = NoInternetDialog().apply {
                isRetry = false
                onDismiss {
                    (activity as BaseActivity).isShowNoInternet = false
                }
            }.onPositive { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
            noInternetDialog?.show(activity = this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onShowPopUpPostVideoEvent(event: ShowPopupPostVideoEvent) {
        DialogAlert().setTitle(getString(R.string.reminder))
            .setMessage(getString(R.string.reminder_message))
            .setTitlePositive("OK").onPositive {
            }.show(context = this)
        EventBus.getDefault().removeStickyEvent(ShowPopupPostVideoEvent::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onShowPopUpReminderIAPEvent(event: ShowPopupReminderIAP) {
        DialogAlert().setTitle(getString(R.string.reminder))
            .setMessage(getString(R.string.reminder_iap_message))
            .setTitlePositive("OK").onPositive {
            }.show(context = this)
        EventBus.getDefault().removeStickyEvent(ShowPopupReminderIAP::class.java)
    }

    @Subscribe
    fun onReloginAgain(event: ReloginAgainEvent) {
        finish()
    }

    private fun registerNetworkListener() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    private fun unregisterNetworkListener() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    var noInternetDialog: DialogAlert? = null

    private var networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                AppLog.e("Duy", "onAvailable")
                isOfflineMode = false
                noInternetDialog?.dismiss()
                isShowNoInternet = false
            }

            override fun onLost(network: Network) {
                AppLog.e("Duy", "onLost")
                isOfflineMode = true
            }
        }

    private fun checkForUpdate(updateType: String) {
        if (TextUtils.isEmpty(updateType)) {
            return
        }
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            try {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    try {
                        val installType = when {
                            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) && updateType == AppConstants.UPDATE_TYPE_FLEXIBLE -> AppUpdateType.FLEXIBLE
                            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) && updateType == AppConstants.UPDATE_TYPE_IMMEDIATE -> AppUpdateType.IMMEDIATE
                            else -> AppUpdateType.FLEXIBLE
                        }
                        if (installType == AppUpdateType.FLEXIBLE) {
                            appUpdateManager.registerListener(
                                appUpdatedListener
                            )
                        }

                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            installType,
                            this,
                            APP_UPDATE_REQUEST_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }.addOnFailureListener {
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(
            findViewById(R.id.root),
            getString(R.string.update_has_just_been_downloaded),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(getString(R.string.restart)) { appUpdateManager.completeUpdate() }
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        snackBar.show()
    }
}