package com.cbi.app.trs.data.entities

import android.os.Parcelable
import com.cbi.app.trs.features.utils.AppConstants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieData(val video_id: Int = 0, val image_thumbnail: String = "", val video_title: String = "",
                     val video_description: String = "", val required_equipment_ids: List<Int> = emptyList(),
                     val area_ids: List<Int> = emptyList(),
                     val coach_tips: List<String> = emptyList(), val video_play_url: String = "", val view_count: Int = 0,
                     val video_duration: Int = 0, var video_is_favorite: Boolean = false, val pre_post_type: Int? = 0,
                     var is_playing: Boolean = false,
                     var downloadedDate: Long = 0,
                     var isDownloaded: Boolean = false) : Parcelable {
    companion object {
        fun empty() = MovieData(0, "", "", "", ArrayList(), ArrayList(), ArrayList(), "", 0, 0)
        fun emptySeeMore() = MovieData(AppConstants.VIDEO_SEE_MORE_ID, "", "", "", ArrayList(), ArrayList(), ArrayList(), "", 0, 0)
    }
}