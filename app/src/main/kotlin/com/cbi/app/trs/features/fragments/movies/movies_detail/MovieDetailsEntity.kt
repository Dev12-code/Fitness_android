package com.cbi.app.trs.features.fragments.movies.movies_detail

import com.cbi.app.trs.core.extension.empty

data class MovieDetailsEntity(private val id: Int,
                              private val title: String,
                              private val poster: String,
                              private val summary: String,
                              private val cast: String,
                              private val director: String,
                              private val year: Int,
                              private val url: String) {

    companion object {
        fun empty() = MovieDetailsEntity(0, String.empty(), String.empty(), String.empty(),
                String.empty(), String.empty(), 0, String.empty())
    }

    fun toMovieDetails() = MovieDetails(id, title, poster, summary, cast, director, year, url)
}
