package com.cbi.app.trs.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SystemData(
        var achievement: List<Achievement>?,
        var area: List<Area>?,
        var areaFiltered: List<Area>?,
        var config: Config?,
        var equipment: List<Equipment>?,
        var collection: List<Collection>?,
        var painArea: List<PainArea>?,
        var postWorkout: List<PostWorkout>?,
        var preWorkout: List<PreWorkout>?,
        var bonus: List<Bonus>?
) {
    data class Achievement(
            val achievement_id: Int,
            val achievement_title: String,
            val achievement_description: String?,
            val achievement_milestone: Int?
    )

    data class Area(
            val area_title: String,
            val area_id: Int
    )

    data class Config(
            val minimum_version_ios: String,
            val minimum_version_android: String,
            val force_update_message: String
    )

    data class Equipment(
            val equipment_title: String,
            val equipment_id: Int
    )

    data class Collection(
            val collection_title: String,
            val collection_id: Int
    )

    @Parcelize
    data class PainArea(
            val pain_area_title: String,
            val pain_area_key: String,
            val pain_area_id: Int,
            val pain_area_type: String
    ) : Parcelable

    data class PostWorkout(
            val post_title: String,
            val post_id: Int
    )

    data class PreWorkout(
            val pre_title: String,
            val pre_id: Int
    )

    @Parcelize
    data class Bonus(
            val bonus_title: String,
            val bonus_id: Int,
            val bonus_image: String?
    ) : Parcelable
}