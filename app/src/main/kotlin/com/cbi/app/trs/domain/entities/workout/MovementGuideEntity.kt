package com.cbi.app.trs.domain.entities.workout

import android.os.Parcelable
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovementGuideEntity(val data: MovieData = MovieData()) : BaseEntities(), Parcelable {
    companion object {
        fun empty() = MovementGuideEntity(MovieData.empty())
    }
}