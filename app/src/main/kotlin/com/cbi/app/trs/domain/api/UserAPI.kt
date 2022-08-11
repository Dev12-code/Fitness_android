package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilityKellyVideoEntity
import com.cbi.app.trs.domain.entities.quiz.QuizStatusEntity
import com.cbi.app.trs.domain.entities.setting.HtmlEntity
import com.cbi.app.trs.domain.entities.user.SignInUpEntity
import com.cbi.app.trs.domain.entities.user.UserProfileEntity
import com.cbi.app.trs.domain.usecases.quiz.PostQuiz
import com.cbi.app.trs.domain.usecases.user.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

internal interface UserAPI {
    companion object {
        private const val UserID = "UserID"
        private const val SIGN_UP = "user/trs_sign_up"
        private const val SIGN_IN = "user/trs_sign_in"
        const val REFRESH_TOKEN = "user/refresh_token"
        private const val CHANGE_PASS = "user/trs_change_pass"
        private const val OTP = "user/get_otp"
        private const val GOOGLE_SIGN = "user/google_authenticate"
        private const val FACEBOOK_SIGN = "user/facebook_authenticate"

        private const val QUIZ_STATUS = "{$UserID}/user/initial_survey_status/"
        private const val QUIZ_UPDATE = "{$UserID}/user/update_initial_survey"

        private const val USER_PROFILE = "{$UserID}/user/profile"
        private const val UPDATE_USER_PROFILE = "{$UserID}/user/update_profile"
        private const val UPDATE_USER_AVATAR = "{$UserID}/user/update_avatar"

        private const val UPDATE_DEVICE_TOKEN = "{$UserID}/notifications/register"

        private const val GET_POLICY = "{$UserID}/user/privacy_policy"
        private const val GET_HELP = "{$UserID}/user/help"

        private const val SIGN_UP_INTRO_VIDEO = "{${UserID}}/app/sign_up_intro_video"
        private const val UPDATE_NEW_PASSWORD = "{$UserID}/user/update_new_password"
    }

    @POST(SIGN_UP)
    @Headers("No-Authentication: true")
    fun signUp(@Body params: PostSignIn.Params): Call<SignInUpEntity>

    @POST(SIGN_IN)
    @Headers("No-Authentication: true")
    fun signIn(@Body params: PostSignIn.Params): Call<SignInUpEntity>

    @POST(REFRESH_TOKEN)
    @Headers("No-Authentication: true")
    fun refreshToken(@Body params: PostRefreshToken.Params): Call<SignInUpEntity>

    @POST(CHANGE_PASS)
    @Headers("No-Authentication: true")
    fun changePass(@Body params: PostChangePassword.Params): Call<BaseEntities>

    @POST(OTP)
    @Headers("No-Authentication: true")
    fun getOTP(@Body params: PostOTP.Params): Call<BaseEntities>

    @POST(GOOGLE_SIGN)
    @Headers("No-Authentication: true")
    fun googleSign(@Body token: PostGoogleSignIn.Params): Call<SignInUpEntity>

    @POST(FACEBOOK_SIGN)
    @Headers("No-Authentication: true")
    fun facebookSign(@Body token: PostFacebookSignIn.Params): Call<SignInUpEntity>

    @GET(QUIZ_STATUS)
    fun getQuizStatus(@Path(UserID) userID: Int): Call<QuizStatusEntity>

    @POST(QUIZ_UPDATE)
    fun postQuiz(@Path(UserID) userID: Int, @Body param: PostQuiz.Param): Call<BaseEntities>

    @GET(USER_PROFILE)
    fun getUserProfile(@Path(UserID) userID: Int): Call<UserProfileEntity>

    @POST(UPDATE_USER_PROFILE)
    fun postUpdateUserProfile(@Path(UserID) userID: Int?, @Body param: PostUserProfile.Param): Call<BaseEntities>

    @POST(UPDATE_DEVICE_TOKEN)
    fun postUpdateDeviceToken(@Path(UserID) userID: Int?, @Body param: PostDeviceToken.Params): Call<BaseEntities>

    @GET(GET_POLICY)
    fun getPolicy(@Path(UserID) userID: Int?): Call<HtmlEntity>

    @GET(GET_HELP)
    fun getHelp(@Path(UserID) userID: Int?): Call<HtmlEntity>

    @Multipart
    @POST(UPDATE_USER_AVATAR)
    fun updateAvatar(@Path(UserID) userID: Int?, @Part file: MultipartBody.Part): Call<BaseEntities>

    @GET(SIGN_UP_INTRO_VIDEO)
    fun getSignUpIntroVideo(@Path(UserID) userID: Int?): Call<MobilityKellyVideoEntity>

    @POST(UPDATE_NEW_PASSWORD)
    fun updateNewPassword(@Path(UserID) userID: Int?, @Body params: PostUpdateNewPassword.Params): Call<BaseEntities>
}