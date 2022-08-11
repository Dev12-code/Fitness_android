package com.cbi.app.trs.features.utils

import android.content.Context
import android.graphics.Color
import android.webkit.MimeTypeMap
import android.widget.TextView
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.lang.Exception


object CommonUtils {
    fun showError(context: Context?, title: String?, message: String?) {
        if (context == null) return
        DialogAlert()
                .setTitle(title ?: "ServerError")
                .setMessage(message ?: "")
                .setCancel(false)
                .setTitlePositive("OK")
                .onDismiss { (context as BaseActivity).isShowError = false }
                .show(context)
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun setColorCode(percent: Int, textView: TextView, progressBar: CircularProgressBar, isOnProcess: Boolean) {
        if (isOnProcess) {
            textView.setTextColor(Color.parseColor("#888888"))
            progressBar.progressBarColor = Color.parseColor("#888888")
            return
        }
        when (percent) {
            in 0..33 -> {
                textView.setTextColor(Color.parseColor("#ff5555"))
                progressBar.progressBarColor = Color.parseColor("#ff5555")
            }
            in 3..67 -> {
                textView.setTextColor(Color.parseColor("#ffbd34"))
                progressBar.progressBarColor = Color.parseColor("#ffbd34")
            }
            else -> {
                textView.setTextColor(Color.parseColor("#0bd493"))
                progressBar.progressBarColor = Color.parseColor("#0bd493")
            }
        }
    }

    fun getBeautyDate(month: Int, dayOfMonth: Int, year: Int): String {
        var monthText = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        var dayOfMonthText = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        return "$monthText/$dayOfMonthText/$year"
    }

    fun getRemainingTime(date: Long, limit: Int = 14): String {
        val seconds = limit * 24 * 60 * 60 - (System.currentTimeMillis() / 1000 - date)
        if (seconds <= 0) return "0 day"
        val sec: Long = seconds % 60
        val minutes: Long = seconds % 3600 / 60
        val hours: Long = seconds % 86400 / 3600
        val days: Long = seconds / 86400

        var result = ""
        if (days > 0) result += "$days days"

        if (hours > 0) {
            if (result.isNotEmpty()) result += " "
            result += "$hours hours"
        }
        if (days == 0L && hours == 0L) result = "$minutes minutes $sec seconds"
        return result
    }

    fun compareVersion(currentVersion: String, newVersion: String): Int {
        try {
            if (currentVersion == newVersion) {
                return -1
            }
            val curVer =
                    currentVersion.split("\\.".toRegex()).toTypedArray()
            val newVer = newVersion.split("\\.".toRegex()).toTypedArray()
            for (i in curVer.indices) {
                if (newVer[i].toInt() < curVer[i].toInt()) {
                    return -1
                }
            }
            return 1
        }
        catch (e: Exception){
            return -1
        }
    }
}