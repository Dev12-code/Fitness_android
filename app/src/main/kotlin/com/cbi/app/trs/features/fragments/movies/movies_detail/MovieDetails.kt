package com.cbi.app.trs.features.fragments.movies.movies_detail

import android.os.Parcelable
import com.cbi.app.trs.core.extension.empty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieDetails(val id: Int,
                        val title: String? = null,
                        val poster: String? = null,
                        val summary: String? = null,
                        val cast: String? = null,
                        val director: String? = null,
                        val year: Int? = null,
                        val url: String? = null) : Parcelable {

    companion object {
        fun empty() = MovieDetails(0, String.empty(), String.empty(), String.empty(),
                String.empty(), String.empty(), 0, String.empty())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MovieDetails) return false
        if (id != other.id) return false
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


