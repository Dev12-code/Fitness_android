package com.cbi.app.trs.core.extension

import java.text.SimpleDateFormat
import java.util.*

fun Long.getMonthDate(): String {
    if (this == 0L) return ""
    return SimpleDateFormat("MMM dd", Locale.ENGLISH).format(Date(this * 1000))
}

fun Long.getMonthYearDate(): String {
    if (this == 0L) return ""
    return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(this * 1000))
}

fun Long.getMonthYearDate(pattern: String): String {
    if (this == 0L) return ""
    return SimpleDateFormat(pattern, Locale.ENGLISH).format(Date(this * 1000))
}

