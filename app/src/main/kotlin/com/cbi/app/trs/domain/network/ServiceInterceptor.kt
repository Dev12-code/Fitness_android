package com.cbi.app.trs.domain.network

import android.text.TextUtils
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.domain.api.UserAPI
import com.cbi.app.trs.domain.entities.user.SignInUpEntity
import com.cbi.app.trs.features.utils.AppConstants
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock


private const val REFRESH_TOKEN_TIME_BEFORE = 5 * 60

class ServiceInterceptor constructor(
    private val userDataCache: UserDataCache,
    private val gson: Gson
) : Interceptor {

    private val lock: ReentrantLock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.header("No-Authentication") == null) {
            userDataCache.get()?.user_token?.let { userToken ->
                if (!TextUtils.isEmpty(userToken.jwt)) {
                    var finalToken = "Bearer ${userToken.jwt}"
                    //check expire time from refresh token
                    val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    currentTime.set(Calendar.ZONE_OFFSET, TimeZone.getTimeZone("UTC").rawOffset)
                    val expireTime =
                        (userToken.expire_date) - currentTime.timeInMillis / 1000 - REFRESH_TOKEN_TIME_BEFORE
                    if (expireTime <= 0) {
                        //starting to lock old API to call API refresh token first
                        if (lock.tryLock()) {
                            try {
                                //call API refresh token
                                val jsonObject = JSONObject()
                                try {
                                    jsonObject.put("refresh_token", userToken.refresh_token)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                val mediaType = "application/json; charset=utf-8".toMediaType()
                                val body = jsonObject.toString().toRequestBody(mediaType)
                                val refreshTokenRequest = request.newBuilder()
                                    .url(AppConstants.BASE_URL + UserAPI.REFRESH_TOKEN)
                                    .post(body)

                                val response = chain.proceed(refreshTokenRequest.build())
                                //if API call successfully, upload new token to local
                                if (response.isSuccessful) {
                                    val responseEntity =
                                        gson.fromJson(
                                            response.body?.string(),
                                            SignInUpEntity::class.java
                                        )
                                    responseEntity.data?.let {
                                        userDataCache.put(it)
                                    }
                                    //get new token
                                    finalToken = "Bearer ${responseEntity.data?.user_token?.jwt}"
                                }
                                response.close()

                                request = generateNewRequest(request, finalToken)
                                return chain.proceed(request)

                            } catch (e: Exception) {
                                request = generateNewRequest(request, finalToken)
                                return chain.proceed(request)
                            } finally {
                                if (lock.isHeldByCurrentThread) {
                                    lock.unlock()
                                }
                            }
                        } else {
                            lock.lock() // this will block the thread until the thread that is refreshing
                            // the token will call .unlock() method
                            if (lock.isHeldByCurrentThread) {
                                lock.unlock()
                            }

                            //request refresh token done, retry now
                            finalToken = "Bearer ${userDataCache.get()?.user_token?.jwt}"
                            request = generateNewRequest(request, finalToken)
                            return chain.proceed(request)
                        }
                    } else {
                        request = generateNewRequest(request, finalToken)
                    }
                } else {
                    request = request.newBuilder()
                        .build()
                }
            }
        } else {
            request = request.newBuilder()
                .build()
        }

        return chain.proceed(request)
    }

    private fun generateNewRequest(request: Request, token: String): Request {
        return request.newBuilder()
            .addHeader("Authorization", token)
            .build()
    }
}