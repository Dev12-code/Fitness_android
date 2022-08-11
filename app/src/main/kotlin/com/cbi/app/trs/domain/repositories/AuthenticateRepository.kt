package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.HtmlData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilityKellyVideoEntity
import com.cbi.app.trs.domain.entities.setting.HtmlEntity
import com.cbi.app.trs.domain.entities.user.SignInUpEntity
import com.cbi.app.trs.domain.entities.user.UserProfileEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.UserService
import com.cbi.app.trs.domain.usecases.user.*
import okhttp3.MultipartBody
import javax.inject.Inject

interface AuthenticateRepository {
    fun postSignUp(params: PostSignIn.Params): Either<Failure, UserData>
    fun postSignIn(params: PostSignIn.Params): Either<Failure, UserData>
    fun postRefreshToken(params: PostRefreshToken.Params): Either<Failure, UserData>
    fun postOTP(params: PostOTP.Params): Either<Failure, BaseEntities>
    fun postChangePassword(params: PostChangePassword.Params): Either<Failure, BaseEntities>
    fun getUserProfile(userID: Int): Either<Failure, UserData.UserProfile>
    fun postUserProfile(userId: Int?, params: PostUserProfile.Param): Either<Failure, BaseEntities>
    fun postDeviceToken(userId: Int?, params: PostDeviceToken.Params): Either<Failure, BaseEntities>
    fun getHelp(userID: Int?): Either<Failure, HtmlData>
    fun getPolicy(userID: Int?): Either<Failure, HtmlData>
    fun postGoogleSignIn(token: PostGoogleSignIn.Params): Either<Failure, UserData>
    fun postFacebookSignIn(token: PostFacebookSignIn.Params): Either<Failure, UserData>
    fun postUserAvatar(userID: Int?, file: MultipartBody.Part): Either<Failure, BaseEntities>
    fun getSignUpIntroVideo(userID: Int?): Either<Failure, MovieData>
    fun postUpdateNewPassword(userID: Int?, params: PostUpdateNewPassword.Params): Either<Failure, BaseEntities>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: UserService,
                        private val userDataCache: UserDataCache) : AuthenticateRepository, BaseNetwork() {
        override fun postSignUp(params: PostSignIn.Params): Either<Failure, UserData> {
            return when (networkHandler.isConnected) {
                true -> request(service.signUp(params), { it ->
                    UserData(it.data?.user_token, it.data?.user_profile).also { userDataCache.put(it) }
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postSignIn(params: PostSignIn.Params): Either<Failure, UserData> {
            return when (networkHandler.isConnected) {
                true -> request(service.signIn(params), { it ->
                    UserData(it.data?.user_token, it.data?.user_profile).also { userDataCache.put(it) }
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postRefreshToken(params: PostRefreshToken.Params): Either<Failure, UserData> {
            return when (networkHandler.isConnected) {
                true -> request(service.refreshToken(params), { it ->
                    UserData(it.data?.user_token, it.data?.user_profile).also { userDataCache.put(it) }
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postOTP(params: PostOTP.Params): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.getOTP(params), {
                    it
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postChangePassword(params: PostChangePassword.Params): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.changePass(params), {
                    it
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getUserProfile(userID: Int): Either<Failure, UserData.UserProfile> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUserProfile(userID), {
                    it.data!!.also { it1 ->
                        userDataCache.get()?.apply { user_profile = it1 }?.let { it2 -> userDataCache.put(it2) }
                    }
                }, UserProfileEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postUserProfile(userId: Int?, params: PostUserProfile.Param): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.postUpdateUserProfile(userId, params), {
                    it
                }, BaseEntities())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postDeviceToken(userId: Int?, params: PostDeviceToken.Params): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.postUpdateDeviceToken(userId, params), {
                    it
                }, BaseEntities())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getHelp(userID: Int?): Either<Failure, HtmlData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getHelp(userID), {
                    it.data
                }, HtmlEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getPolicy(userID: Int?): Either<Failure, HtmlData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getPolicy(userID), {
                    it.data
                }, HtmlEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postGoogleSignIn(token: PostGoogleSignIn.Params): Either<Failure, UserData> {
            return when (networkHandler.isConnected) {
                true -> request(service.googleSign(token), { it ->
                    UserData(it.data?.user_token, it.data?.user_profile).also { userDataCache.put(it) }
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postFacebookSignIn(token: PostFacebookSignIn.Params): Either<Failure, UserData> {
            return when (networkHandler.isConnected) {
                true -> request(service.facebookSign(token), { it ->
                    UserData(it.data?.user_token, it.data?.user_profile).also { userDataCache.put(it) }
                }, SignInUpEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postUserAvatar(userID: Int?, file: MultipartBody.Part): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.updateAvatar(userID, file), {
                    it
                }, BaseEntities())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSignUpIntroVideo(userID: Int?): Either<Failure, MovieData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getSignUpIntroVideo(userID), {
                    it.data
                }, MobilityKellyVideoEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun postUpdateNewPassword(userID: Int?, params: PostUpdateNewPassword.Params): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.updateNewPassword(userID, params), {
                    it
                }, BaseEntities())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}