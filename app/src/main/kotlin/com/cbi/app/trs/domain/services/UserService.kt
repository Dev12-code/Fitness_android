package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.UserAPI
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilityKellyVideoEntity
import com.cbi.app.trs.domain.usecases.quiz.PostQuiz
import com.cbi.app.trs.domain.usecases.user.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService
@Inject constructor(retrofit: Retrofit) : UserAPI {
    private val userApi by lazy { retrofit.create(UserAPI::class.java) }

    override fun signUp(params: PostSignIn.Params) = userApi.signUp(params)

    override fun signIn(params: PostSignIn.Params) = userApi.signIn(params)

    override fun refreshToken(params: PostRefreshToken.Params) = userApi.refreshToken(params)

    override fun changePass(params: PostChangePassword.Params) = userApi.changePass(params)

    override fun getOTP(params: PostOTP.Params) = userApi.getOTP(params)

    override fun googleSign(token: PostGoogleSignIn.Params) = userApi.googleSign(token)

    override fun facebookSign(token: PostFacebookSignIn.Params) = userApi.facebookSign(token)

    override fun getQuizStatus(userID: Int) = userApi.getQuizStatus(userID)

    override fun postQuiz(userID: Int, param: PostQuiz.Param) = userApi.postQuiz(userID, param)

    override fun getUserProfile(userID: Int) = userApi.getUserProfile(userID)

    override fun postUpdateUserProfile(userID: Int?, param: PostUserProfile.Param) = userApi.postUpdateUserProfile(userID, param)

    override fun postUpdateDeviceToken(userID: Int?, param: PostDeviceToken.Params) = userApi.postUpdateDeviceToken(userID, param)

    override fun getPolicy(userID: Int?) = userApi.getPolicy(userID)

    override fun getHelp(userID: Int?) = userApi.getHelp(userID)

    override fun updateAvatar(userID: Int?, file: MultipartBody.Part) = userApi.updateAvatar(userID, file)

    override fun getSignUpIntroVideo(userID: Int?): Call<MobilityKellyVideoEntity> {
        return userApi.getSignUpIntroVideo(userID)
    }

    override fun updateNewPassword(userID: Int?, params: PostUpdateNewPassword.Params): Call<BaseEntities> {
        return userApi.updateNewPassword(userID, params)
    }
}