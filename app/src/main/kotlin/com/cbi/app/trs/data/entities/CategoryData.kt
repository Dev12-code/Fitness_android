package com.cbi.app.trs.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryData(val category_title: String = "", val category_thumbnail: String = "", val category_id: Int = 0, val video_count: Int = 0) : Parcelable