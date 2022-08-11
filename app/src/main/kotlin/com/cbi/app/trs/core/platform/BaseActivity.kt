package com.cbi.app.trs.core.platform

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.cbi.app.trs.R
import com.cbi.app.trs.R.layout
import com.cbi.app.trs.features.dialog.DialogProgress

/**
 * Base Activity class with helper methods for handling fragment transactions and back button
 * events.
 *
 * @see AppCompatActivity
 */
abstract class BaseActivity : AppCompatActivity() {
    var disableDispatchTouchEvent: Boolean = false
    var isTouchDisable: Boolean = false
    var isShowProgress = false
    var isShowNoInternet = false
    var isShowError = false
    var isOfflineMode = false

    companion object {
        var apiRequestCount = 0
    }


    private fun adjustFontScale(configuration: Configuration?) {
        configuration?.let {
            it.fontScale = 1.0F
            val metrics: DisplayMetrics = resources.displayMetrics
            val wm: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density

            baseContext.applicationContext.createConfigurationContext(it)
            baseContext.resources.displayMetrics.setTo(metrics)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_layout)
        addFragment()
        adjustFontScale(resources.configuration)
    }

    override fun onDestroy() {
        super.onDestroy()
        apiRequestCount = 0
    }

    override fun onBackPressed() {
        getCurrentFragment()?.let {
            if (!it.onBackPressed())
                super.onBackPressed()
        }
    }

    private fun addFragment() =
            fragment()?.let {
                BaseFragment.addFragmentByActivity(this, it)
            }

    abstract fun fragment(): BaseFragment?

    fun showProgress() {
        if (!isShowProgress) {
            isShowProgress = true
            DialogProgress().show(this)
        }
    }

    fun forceShowProgress() {
        isShowProgress = true
        DialogProgress().show(this)
    }

    fun hideProgress() {
        if (apiRequestCount <= 0) {
            apiRequestCount = 0
            isShowProgress = false
            DialogProgress.hide(this)
        }
    }

    fun forceHide() {
        apiRequestCount = 0
        isShowProgress = false
        DialogProgress.hide(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && !disableDispatchTouchEvent) {
            val view = currentFocus
            if (view != null && (view is AppCompatEditText || view is EditText)) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                    view.clearFocus()
                    val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return isTouchDisable || super.dispatchTouchEvent(event)
    }

    fun changeFullScreenMode(isLightTheme: Boolean = true) {
        if (isLightTheme)
            window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else {
            window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        changeStatusBarColor(Color.TRANSPARENT)
    }


    fun unchangeFullScreenMode() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun changeStatusBarColor(color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragment is BaseFragment && fragment.onBackPressed()) {
                return true
            }
            if (!defaultBack()) {
                return super.onKeyDown(keyCode, event)
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    fun getCurrentFragmentLevel(): Int {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment
        return currentFragment.getFragmentLevel()
    }

    fun getCurrentFragmentName(): String {
        try {
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment
            return currentFragment.javaClass.simpleName
        } catch (e: Exception) {
            return ""
        }
    }

    fun getCurrentFragment(): BaseFragment? {
        return try {
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment
        } catch (e: Exception) {
            null
        }
    }

    fun defaultBack(): Boolean {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
            return true
        }
        return false
    }

}

