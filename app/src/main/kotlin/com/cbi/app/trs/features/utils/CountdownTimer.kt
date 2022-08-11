package com.cbi.app.trs.features.utils

import android.os.CountDownTimer
import com.google.firebase.crashlytics.FirebaseCrashlytics


/**
 * This class uses the native CountDownTimer to
 * create a timer which could be paused and then
 * started again from the previous point. You can
 * provide implementation for onTick() and onFinish()
 * then use it in your projects.
 */
abstract class CountdownTimer(millisInFuture: Long, countDownInterval: Long) {
    var millisInFuture: Long = 0
    var countDownInterval: Long = 0
    var millisRemaining: Long = 0
    var countDownTimer: CountDownTimer? = null
    var isPaused = true

    private fun createCountDownTimer() {
        try {
            countDownTimer = object : CountDownTimer(millisRemaining, countDownInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
                        millisRemaining = millisUntilFinished
                        this@CountdownTimer.onTick(millisUntilFinished)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }

                override fun onFinish() {
                    try {
                        this@CountdownTimer.onFinish()
                    } catch (e: java.lang.Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish()

    /**
     * Cancel the countdown.
     */
    fun cancel() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        millisRemaining = 0
    }

    /**
     * Start or Resume the countdown.
     * @return CountDownTimerPausable current instance
     */
    @Synchronized
    fun start(): CountdownTimer {
        if (isPaused) {
            createCountDownTimer()
            countDownTimer!!.start()
            isPaused = false
        }
        return this
    }

    /**
     * Pauses the CountDownTimerPausable, so it could be resumed(start)
     * later from the same point where it was paused.
     */
    @Throws(IllegalStateException::class)
    fun pause() {
        if (!isPaused) {
            countDownTimer!!.cancel()
        } else {
            return
        }
        isPaused = true
    }

    init {
        this.millisInFuture = millisInFuture
        this.countDownInterval = countDownInterval
        millisRemaining = this.millisInFuture
    }
}