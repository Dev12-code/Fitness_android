package com.cbi.app.trs.features.utils

import com.bugfender.sdk.Bugfender

object BugFenderLogging {
    fun logEventAPI(request: String?, body: String?, response: String?) {
        Bugfender.d(
            "API",
            String.format(
                "REQUEST: %s---------- BODY: %s-------- RESPONSE: %s",
                request,
                body,
                response
            )
        )
    }

    fun logEventWebex(
        functionName: String,
        isSuccess: Boolean,
        request: String?,
        response: String?
    ) {
        Bugfender.d(
            "WEBEX",
            String.format(
                "FUNCTION: %s ---------- SUCCESS: %s ---------- REQUEST: %s --------- ERROR: %s",
                functionName,
                isSuccess.toString(),
                request,
                response
            )
        )
    }
}