package com.cbi.app.trs.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MobilityTestVideoData(val video_id: Int = 0, val image_thumbnail: String = "", val video_title: String = "", val video_play_url: String = "", val video_duration: Int = 0,
                                 val video_description: String = "", val video_is_favorite: Boolean = false, val view_count: Int = 0,
                                 val video_instruction: List<String> = emptyList(), val video_compensations: List<String> = emptyList(), val pose_image: PoseImage = PoseImage(),
                                 var type: Int = -1, var score: Int = 0) : Parcelable {
    @Parcelize
    data class PoseImage(val green_pose: String = "", val yellow_pose: String = "", val red_pose: String = "", val green_text: String = "",
                         val yellow_text: String = "", val red_text: String = "") : Parcelable
}