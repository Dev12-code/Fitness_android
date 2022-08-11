package com.cbi.app.trs.domain.usecases.user

import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.UserData
import javax.inject.Inject

data class GetUserData
@Inject constructor(private val userDataCache: UserDataCache) {
    fun getUserData(): UserData? {
        return userDataCache.get()
    }
}