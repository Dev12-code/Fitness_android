package com.cbi.app.trs.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MobilityStatus(val shoulder_point_avg: Double, val trunk_point_avg: Double, val hip_point_avg: Double, val ankle_point_avg: Double, val test_date: Long,
                          val on_process: Boolean) : Parcelable