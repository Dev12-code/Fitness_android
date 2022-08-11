package com.cbi.app.trs.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AchievementData(val achievement_id: Int = 0, val achievement_title: String = "", val achievement_active_image: String = "",
                           val achievement_inactive_image: String = "",
                           val achievement_is_active: Boolean = false,
                           val achievement_description: String = "", val achievement_milestone: Int = 0) : Parcelable