package com.cbi.app.trs.features.fragments.login

import com.cbi.app.trs.data.cache.UserDataCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Authenticator
@Inject constructor(private val userDataCache: UserDataCache) {

    fun userLoggedIn(): Boolean {
        return !userDataCache.get()?.user_token?.jwt.isNullOrEmpty()
    }
}
