package com.cbi.app.trs.domain.network

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.utils.BugFenderLogging
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import okhttp3.Request
import retrofit2.Call
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseNetwork {
    internal fun <T, R> request(
        call: Call<T>,
        transform: (T) -> R,
        default: T
    ): Either<Failure, R> {
        return try {
            BaseActivity.apiRequestCount++
            val response = call.execute()
            BaseActivity.apiRequestCount--
            when (response.isSuccessful) {
                true -> {
                    try {
                        val body = call.request().getBodyAsString()
                        BugFenderLogging.logEventAPI(
                            response.raw().toString(), body, response.body().toString()
                        )
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                    return Either.Right(transform((response.body() ?: default!!)))
                }
                false -> {
                    val errorBody =
                        response.errorBody()?.string()?.replace("\"data\":\"\"", "\"data\":{}")
                    AppLog.e("OkHttpClient - Duy", "$errorBody")
                    try {
                        val body = call.request().getBodyAsString()
                        BugFenderLogging.logEventAPI(
                            response.raw().toString(), body, errorBody
                        )
                    } catch (e: java.lang.Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                    AppLog.e("OkHttpClient - Duy", "$errorBody")
                    var code: Int = response.code()
                    var message: String = ""
//                    try {
//                        Gson().fromJson(errorBody, SignInUpEntity::class.java)?.message_code?.let {
//                            code = it
//                        }
//                    } catch (e: Exception) {
//                    }

                    Gson().fromJson(errorBody, BaseEntities::class.java)
                        ?.let { message = it.message }

                    return Either.Left(Failure.ServerError(message, code))
                }
            }
        } catch (exception: Throwable) {
            try {
                val body = call.request().getBodyAsString()
                BugFenderLogging.logEventAPI(
                    call.request().toString(), body, "Exception: " + exception.message
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            BaseActivity.apiRequestCount--
            if (exception is SocketTimeoutException) {
                Either.Left(
                    Failure.ServerError(
                        "We’re currently experiencing technical difficulties. Please try again.",
                        -1
                    )
                )
                Either.Left(
                    Failure.ServerError(
                        "We’re currently experiencing technical difficulties. Please try again.",
                        -1
                    )
                )
            } else if (exception is UnknownHostException) {
                Either.Left(Failure.ServerError("The server is down.", -1))
            } else {
                Either.Left(
                    Failure.ServerError(
                        "An upgrade is currently in progress. Please try again later",
                        -1
                    )
                )
                Either.Left(
                    Failure.ServerError(
                        "An upgrade is currently in progress. Please try again later",
                        -1
                    )
                )
            }
        }

    }

    private fun Request.getBodyAsString(): String {
        val requestCopy = this.newBuilder().build()
        val buffer = okio.Buffer()
        requestCopy.body?.writeTo(buffer)
        return buffer.readUtf8()
    }
}