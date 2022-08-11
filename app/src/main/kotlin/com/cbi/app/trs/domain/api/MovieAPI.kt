package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.bonus.BonusMovieEntity
import com.cbi.app.trs.domain.entities.movie.MovieDetailEntity
import com.cbi.app.trs.domain.entities.movie.MovieListEntity
import com.cbi.app.trs.domain.entities.movie.ReferenceMovieEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.usecases.movie.PostAddFavourite
import com.cbi.app.trs.domain.usecases.movie.PostTrackingVideo
import retrofit2.Call
import retrofit2.http.*

internal interface MovieAPI {
    companion object {
        private const val UserID = "UserID"
        private const val MovieID = "MovieID"
        private const val TOP_VIDEO = "{$UserID}/home/topview"
        private const val NEW_VIDEO = "{$UserID}/home/new_videos"
        private const val FEATURE_VIDEO = "{$UserID}/home/feature_video"
        private const val UP_SELL = "{$UserID}/upsell"
        private const val UP_SELL_BEST_SELLER = "{$UserID}/upsell/best_seller"
        private const val SEARCH = "{$UserID}/search"
        private const val SEARCH_OLD_MOBILITY_WOD = "{$UserID}/home/old_mobility_wod"
        private const val SEARCH_DAILY = "{$UserID}/search_daily"
        private const val FAVOURITE = "{$UserID}/favorite/list"
        private const val FAVOURITE_ADD = "{$UserID}/favorite/add"
        private const val FAVOURITE_REMOVE = "{$UserID}/favorite/remove/{$MovieID}"
        private const val REFERENCE_MOVIE = "{$UserID}/video/reference_videos"
        private const val VIDEO_DETAIL = "{$UserID}/video/info/{$MovieID}"
        private const val BONUS_VIDEO = "{$UserID}/bonus"
        private const val TRACKING_VIDEO = "{$UserID}/tracking/watch"
        private const val DAILY_MAINTENANCE = "{$UserID}/home/daily_maintenance"
    }

    @GET(DAILY_MAINTENANCE)
    fun getDailyMaintenance(@Path(UserID) userID: Int): Call<MovieListEntity>

    @GET(TOP_VIDEO)
    fun getTopMovie(@Path(UserID) userID: Int): Call<MovieListEntity>

    @GET(NEW_VIDEO)
    fun getNewMovie(@Path(UserID) userID: Int): Call<MovieListEntity>

    @GET(FEATURE_VIDEO)
    fun getFeatureMovie(
        @Path(UserID) userID: Int, @Query("min_duration") minDuration: Int,
        @Query("max_duration") maxDuration: Int,
        @Query("limit") limit: Int, @Query("page") page: Int, @Query("ro") ro: String?
    ): Call<MovieListEntity>

    @GET(UP_SELL)
    fun getUpsell(
        @Path(UserID) userID: Int,
        @Query("reference_video_id") referenceVideoId: Int?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<UpsellEntity>

    @GET(UP_SELL_BEST_SELLER)
    fun getUpSellBestSeller(
        @Path(UserID) userID: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<UpsellEntity>

    @GET(SEARCH)
    fun getSearchResult(
        @Path(UserID) userID: Int,
        @Query("search_keyword") searchKeyword: String?,
        @Query("equipment_ids[]") equipmentIds: List<Int>?,
        @Query("collection") collection: String?,
        @Query("focus_areas[]") focusAreas: List<Int>?,
        @Query("min_duration") minDuration: Int?,
        @Query("max_duration") maxDuration: Int?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<SearchMovieEntity>

    @GET(SEARCH_OLD_MOBILITY_WOD)
    fun getSearchOldMobilityWodResult(
        @Path(UserID) userID: Int,
        @Query("search_keyword") searchKeyword: String?,
        @Query("equipment_ids[]") equipmentIds: List<Int>?,
        @Query("collection") collection: String?,
        @Query("focus_areas[]") focusAreas: List<Int>?,
        @Query("min_duration") minDuration: Int?,
        @Query("max_duration") maxDuration: Int?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<SearchMovieEntity>

    @GET(SEARCH_DAILY)
    fun getSearchDailyResult(
        @Path(UserID) userID: Int, @Query("search_keyword") searchKeyword: String?,
        @Query("focus_areas[]") focusAreas: List<Int>?,
        @Query("focus_equipment[]") focusEquipments: List<Int>?,
        @Query("min_duration") minDuration: Int?,
        @Query("max_duration") maxDuration: Int?,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<SearchMovieEntity>

    @GET(FAVOURITE)
    fun getFavourite(
        @Path(UserID) userID: Int, @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<SearchMovieEntity>

    @POST(FAVOURITE_ADD)
    fun addFavourite(
        @Path(UserID) userID: Int,
        @Body params: PostAddFavourite.Params
    ): Call<BaseEntities>

    @DELETE(FAVOURITE_REMOVE)
    fun removeFavourite(@Path(UserID) userID: Int, @Path(MovieID) movieID: Int): Call<BaseEntities>

    @GET(REFERENCE_MOVIE)
    fun getReferenceMovie(
        @Path(UserID) userID: Int,
        @Query("video_id") videoId: Int
    ): Call<ReferenceMovieEntity>

    @GET(VIDEO_DETAIL)
    fun getMovieDetail(
        @Path(UserID) userID: Int?,
        @Path(MovieID) movieID: Int?
    ): Call<MovieDetailEntity>

    @GET(BONUS_VIDEO)
    fun getBonusMovie(
        @Path(UserID) userId: Int?, @Query("bonus_id") bonusId: Int?, @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<BonusMovieEntity>

    @POST(TRACKING_VIDEO)
    fun trackMovie(
        @Path(UserID) userId: Int?,
        @Body param: PostTrackingVideo.Params
    ): Call<BaseEntities>
}