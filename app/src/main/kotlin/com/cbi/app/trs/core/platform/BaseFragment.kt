package com.cbi.app.trs.core.platform

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.R
import com.cbi.app.trs.core.di.ApplicationComponent
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.data.cache.PaymentCache
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.eventbus.ReloginAgainEvent
import com.cbi.app.trs.features.activities.LoginActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.dialog.NoInternetDialog
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.RefreshTokenViewModel
import com.google.android.gms.cast.framework.CastContext
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int

    var statusBarHeight: Int = 0

    private var isExpired: Boolean = false


    var mFragmentLevel = 1

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as AndroidApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var userDataCache: UserDataCache

    @Inject
    lateinit var paymentCache: PaymentCache

    @Inject
    internal lateinit var mNavigator: Navigator

    var userID = 0

    private lateinit var refreshTokenViewModel: RefreshTokenViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(layoutId(), container, false)

    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        refreshTokenViewModel = viewModel(viewModelFactory) {

        }
        getStatusBarHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userDataCache.get()?.user_token?.userID?.let { userID = it }
        super.onViewCreated(view, savedInstanceState)
        forceHide()
    }

    fun adjustFontScale(configuration: Configuration?) {
        configuration?.let {
            it.fontScale = 1.0F
            val metrics: DisplayMetrics = resources.displayMetrics
            val wm: WindowManager =
                activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density

            activity?.let { activity ->
                activity.applicationContext.createConfigurationContext(it)
                activity.resources.displayMetrics.setTo(metrics)
            }
        }
    }

    private fun getStatusBarHeight() {
        val typeValue = TypedValue()
        resources.getValue(
            resources.getIdentifier(
                "status_bar_height", "dimen", "android"
            ), typeValue, true
        )
        statusBarHeight = TypedValue.complexToFloat(typeValue.data).toInt()
        statusBarHeight = 40
    }

    private fun onReceiveRefreshToken(userData: UserData?) {
        hideProgress()
        onReloadData()
    }

    internal fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    internal fun showProgress() = activity?.let { (it as BaseActivity).showProgress() }

    internal fun hideProgress() =
        activity?.let { (it as BaseActivity).hideProgress() }

    internal fun forceShowProgress() = activity?.let { (it as BaseActivity).forceShowProgress() }

    internal fun forceHide() =
        activity?.let { (it as BaseActivity).forceHide() }


    fun getFragmentLevel(): Int {
        return mFragmentLevel
    }

    fun setFragmentLevel(fragmentLevel: Int) {
        this.mFragmentLevel = fragmentLevel
    }

    private fun getTransactionName(): String {
        return "Level$mFragmentLevel"
    }

    companion object {
        var isShowUnAuthorized: Boolean = false

        private const val RC_EXPIRED = 34421
        private const val FRAGMENT_LEVEL_1 = "Level1"
        const val TRANSITION_DELAY = 100L

        @SuppressLint("StaticFieldLeak")
        var performClickLogin: View? = null //For resume the user action after login
        var isPopBackStack = false
        var isSetFragmentInProcess = false
        var disableFragmentAnimation = false


        fun addFragmentByActivity(
            activity: FragmentActivity?,
            fragment: BaseFragment,
            containerViewId: Int = R.id.fragmentContainer,
            fromBottomAnimation: Boolean = false
        ) {
            if (activity == null) {
                return
            }
            addFragment(
                activity.supportFragmentManager,
                fragment,
                containerViewId,
                fromBottomAnimation
            )
        }

        /**
         * Add fragment with higher level
         * @param fromBottomAnimation default is FALSE: fragment will appear from right to left; TRUE: fragment will appear from bottom to top
         */
        fun addFragment(
            fragmentManager: FragmentManager,
            fragment: BaseFragment,
            containerViewId: Int = R.id.fragmentContainer,
            fromBottomAnimation: Boolean = false
        ) {
            //Get current fragment level
            val currentFragment = fragmentManager.findFragmentById(containerViewId)
            var currentLevel = 1
            if (currentFragment != null) {
                currentFragment as BaseFragment
                currentLevel = currentFragment.getFragmentLevel() + 1
            }
            fragment.setFragmentLevel(currentLevel)

            if (fromBottomAnimation) {
                setFragment(
                    fragmentManager,
                    fragment,
                    R.id.fragmentContainer,
                    R.anim.slide_up,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_down
                )
            } else {
                setFragment(fragmentManager, fragment, containerViewId)
            }
        }

        fun setFragment(
            fragmentManager: FragmentManager,
            fragment: BaseFragment,
            containerViewId: Int = R.id.fragmentContainer,
            animInEnter: Int = R.anim.enter_from_right,
            animInExit: Int = R.anim.exit_to_left,
            animOutEnter: Int = R.anim.enter_from_left,
            animOutExit: Int = R.anim.exit_to_right
        ) {
            isSetFragmentInProcess = true
            Handler().postDelayed({
                popBackStack(fragmentManager, fragment)

                val fragmentTransaction = fragmentManager.beginTransaction()
                if (FRAGMENT_LEVEL_1 != fragment.getTransactionName()) {
                    fragmentTransaction.setCustomAnimations(
                        animInEnter,
                        animInExit,
                        animOutEnter,
                        animOutExit
                    )
                } else {
                    fragmentTransaction.setCustomAnimations(0, animInExit, 0, 0)
                }
                fragmentTransaction.replace(containerViewId, fragment)
                try {
                    fragmentTransaction.addToBackStack(fragment.getTransactionName())
                        .commitAllowingStateLoss()
                } catch (e: Exception) {
                }
                isPopBackStack = false
                isSetFragmentInProcess = false
            }, TRANSITION_DELAY)
        }

        // Avoid reloading parent when try to replace same level fragment
        private fun handlePopBackStack(
            fragmentManager: FragmentManager,
            destinationFragment: BaseFragment
        ) {
            try {
                val currentLevel =
                    (fragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment).getFragmentLevel()
                if (destinationFragment.getFragmentLevel() <= currentLevel) {
                    isPopBackStack = true
                }
            } catch (e: Exception) {
            }
        }

        @SuppressLint("CommitTransaction")
        fun popBackStack(
            fragmentManager: FragmentManager,
            fragment: BaseFragment,
            disableAnimation: Boolean = false
        ) {
            handlePopBackStack(fragmentManager, fragment)
            if (fragmentManager.backStackEntryCount == 0) {
                return
            }
            try {
                if (disableAnimation) {
                    disableFragmentAnimation = true
                    fragmentManager.popBackStackImmediate(
                        fragment.getTransactionName(),
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    disableFragmentAnimation = false
                } else {
                    fragmentManager.popBackStackImmediate(
                        fragment.getTransactionName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    if (FRAGMENT_LEVEL_1 == fragment.getTransactionName()) {
                        fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, 0, 0, 0)
                    }
                }
            } catch (e: Exception) {
            }
        }

        fun pop(activity: FragmentActivity?) {
            if (activity == null) {
                return
            }
            (activity as BaseActivity).getCurrentFragment()?.let {
                popBackStack(
                    activity.supportFragmentManager,
                    it
                )
            }
            isPopBackStack = false
        }
    }


    open fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> {
                if (!(activity as BaseActivity).isShowNoInternet) {
                    (activity as BaseActivity).isShowNoInternet = true
                    activity?.let {
                        NoInternetDialog().apply {
                            onDismiss {
                                (activity as BaseActivity).isShowNoInternet = false
                            }
                        }
                            .onPositive { onReloadData() }.show(requireContext())
                    }
                }
            }
            is Failure.ServerError -> {
                AppLog.e("Duy", "${failure.reason} : ${(activity as BaseActivity).isShowError}")
                if (failure.errorCode == 401 || failure.errorCode == 403) {
                    forceHide()

                    DialogAlert()
                        .setTitle("This account is already logged into on another device.")
                        .setMessage("You are currently logged in on another device. Please log out of the other device or contact your administrator.")
                        .setCancel(false)
                        .setTitlePositive("OK")
                        .onPositive {
                            isShowUnAuthorized = false
                            userDataCache.clear()
                            context?.let {
                                startActivityForResult(
                                    LoginActivity.callingIntentForExpired(
                                        it
                                    ), RC_EXPIRED
                                )
                                CastContext.getSharedInstance(it).sessionManager?.endCurrentSession(
                                    true
                                )
                            }
                            EventBus.getDefault().post(ReloginAgainEvent())
                            activity?.finish()
                        }
                        .show(requireContext())


                } else {
                    if (failure.reason.contains("The server is down")) {
                        if (!(activity as BaseActivity).isShowNoInternet) {
                            (activity as BaseActivity).isShowNoInternet = true
                            activity?.let {
                                NoInternetDialog().apply {
                                    onDismiss {
                                        (activity as BaseActivity).isShowNoInternet = false
                                    }
                                }
                                    .onPositive { onReloadData() }.show(requireContext())
                            }
                        }
                    } else if (!(activity as BaseActivity).isShowError) {
                        (activity as BaseActivity).isShowError = true
                        if (failure.reason.contains("The server is busy. Please try again.")) {
                            DialogAlert().setTitle("Sorry!")
                                .setMessage("${failure.reason}")
                                .setTitlePositive("Retry")
                                .onPositive { onReloadData() }
                                .onDismiss { (activity as BaseActivity).isShowError = false }
                                .show(requireContext())
                        } else {
                            CommonUtils.showError(activity, "Sorry!", "${failure.reason}")
                        }
                    }
                }
            }
        }
        BaseActivity.apiRequestCount = 0
        forceHide()
    }

    fun refreshToken() {
        if (!isExpired) {
            isExpired = true
            userDataCache.get()?.user_token?.jwt = ""
            userDataCache.get()?.user_token?.refresh_token?.let {
                refreshTokenViewModel.refreshToken(
                    it
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_EXPIRED) {
            if (resultCode == RESULT_OK) {
                isExpired = false
                userDataCache.get()?.user_token?.userID?.let { userID = it }
                Handler().postDelayed({
                    onReloadData()
                }, 500)
            }
        }
    }

    open fun onReloadData() {}

    fun isAllowForFreemium(show: Boolean = true): Boolean {
        if (userDataCache.get()?.user_profile == null) {
            if (show) DialogAlert().setTitle("Freemium")
                .setMessage("You cannot use this feature with a free account. Please upgrade to experience the full App.")
                .setTitleNegative("Cancel").setTitlePositive("Upgrade").onPositive {
                    mNavigator.openPriceView(activity)
                }.show(activity)
            return false
        }
        if (userDataCache.get()!!.user_profile!!.isFreeUser()) {
            if (show) DialogAlert().setTitle("Freemium")
                .setMessage("You cannot use this feature with a free account. Please upgrade to experience the full App.")
                .setTitleNegative("Cancel").setTitlePositive("Upgrade").onPositive {
                    mNavigator.openPriceView(activity)
                }.show(activity)
            return false
        }
        return true
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}
