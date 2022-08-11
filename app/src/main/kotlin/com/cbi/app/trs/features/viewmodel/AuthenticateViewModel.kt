package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.user.*
import javax.inject.Inject

class AuthenticateViewModel
@Inject constructor(private val postSignIn: PostSignIn,
                    private val postSignUp: PostSignUp,
                    private val postGoogleSignIn: PostGoogleSignIn,
                    private val postFacebookSignIn: PostFacebookSignIn,
                    private val getOTP: PostOTP,
                    private val changePassword: PostChangePassword,
                    private val getSignUpIntroVideo: GetSignUpIntroVideo,
                    private val postUpdateNewPassword: PostUpdateNewPassword,
                    private val postDeviceToken: PostDeviceToken) : BaseViewModel() {
    var userData: MutableLiveData<UserData> = MutableLiveData()
    var userSignUpData: MutableLiveData<UserData> = MutableLiveData()
    var otpData: MutableLiveData<BaseEntities> = MutableLiveData()
    var changePasswordData: MutableLiveData<BaseEntities> = MutableLiveData()
    var postDeviceTokenData: MutableLiveData<BaseEntities> = MutableLiveData()
    var updateNewPasswordData: MutableLiveData<BaseEntities> = MutableLiveData()
    var signUpIntroVideo: MutableLiveData<MovieData> = MutableLiveData()

    fun signIn(email: String, password: String) = postSignIn(PostSignIn.Params(email, password)) { it.fold(::handleFailure, ::handleSignIn) }
    fun signUp(email: String, password: String) = postSignUp(PostSignIn.Params(email, password)) { it.fold(::handleFailure, ::handleSignUp) }

    private fun handleSignUp(userData: UserData) {
        this.userSignUpData.value = userData
    }

    fun googleSignIn(token: String) = postGoogleSignIn(PostGoogleSignIn.Params(token)) { it.fold(::handleFailure, ::handleSignIn) }
    fun facebookSignIn(token: String) = postFacebookSignIn(PostFacebookSignIn.Params(token)) { it.fold(::handleFailure, ::handleSignIn) }

    fun getOTP(email: String) = getOTP(PostOTP.Params(email)) { it.fold(::handleFailure, ::handleOTP) }
    fun changePassword(password: String, otp: String, email: String) = changePassword(PostChangePassword.Params(password, otp, email)) { it.fold(::handleFailure, ::handleChangePassword) }
    fun postDeviceToken(userId: Int?, param: PostDeviceToken.Params) = postDeviceToken(Pair(userId, param)) { it.fold(::handleFailure, ::handleDeviceToken) }
    fun getIntroVideo(userId: Int?) = getSignUpIntroVideo(userId) {
        it.fold(::handleFailure, ::handleSignUpIntroVideo)
    }

    private fun handleSignUpIntroVideo(movieData: MovieData) {
        this.signUpIntroVideo.value = movieData
    }

    private fun handleDeviceToken(baseEntities: BaseEntities) {
        postDeviceTokenData.value = baseEntities
    }

    private fun handleSignIn(userData: UserData) {
        this.userData.value = userData
    }

    private fun handleOTP(otpData: BaseEntities) {
        this.otpData.value = otpData
    }

    private fun handleChangePassword(passwordData: BaseEntities) {
        this.changePasswordData.value = passwordData
    }

    fun updateNewPassword(userId: Int?, oldPassword: String, newPassword: String, rematchPassword: String) = postUpdateNewPassword(Pair(userId,
            PostUpdateNewPassword.Params(oldPassword, newPassword, rematchPassword))) { it.fold(::handleFailure, ::handleRematchPassword) }

    private fun handleRematchPassword(otpData: BaseEntities) {
        this.updateNewPasswordData.value = otpData
    }
}