package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.movie.MovieDetailEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.usecases.pain.GetUnderstandingPain
import retrofit2.Call
import retrofit2.http.*

internal interface PainAPI {
    companion object {
        private const val UserID = "UserID"
        private const val GET_STARTED_PAIN = "{$UserID}/pain/getting_started"
        private const val GET_UNDERSTANDING_PAIN = "{$UserID}/pain/understanding_pain"
        private const val GET_MOBILITY_RX_PAIN = "{$UserID}/pain/mobility_rx"
    }

    @GET(GET_STARTED_PAIN)
    fun getStartedPain(@Path(UserID) userID: Int?, @Query("pain_area_id") painAreaId: Int?): Call<MovieDetailEntity>

    @GET(GET_UNDERSTANDING_PAIN)
    fun getUnderstandingPain(@Path(UserID) userID: Int?, @Query("pain_area_id") painAreaId: Int?, @Query("filter") filter: String?,
                             @Query("limit") limit: Int, @Query("page") page: Int): Call<SearchMovieEntity>

    @GET(GET_MOBILITY_RX_PAIN)
    fun getMobilityRxPain(@Path(UserID) userID: Int?, @Query("pain_area_id") painAreaId: Int?, @Query("filter") filter: String?,
                          @Query("limit") limit: Int, @Query("page") page: Int): Call<SearchMovieEntity>
}