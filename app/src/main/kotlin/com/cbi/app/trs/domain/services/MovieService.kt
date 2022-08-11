package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.MovieAPI
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.movie.MovieDetailEntity
import com.cbi.app.trs.domain.entities.movie.MovieListEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.usecases.bonus.GetBonusDetail
import com.cbi.app.trs.domain.usecases.movie.*
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieService
@Inject constructor(retrofit: Retrofit) : MovieAPI {
    private val movieApi by lazy { retrofit.create(MovieAPI::class.java) }
    override fun getDailyMaintenance(userID: Int): Call<MovieListEntity> {
        return movieApi.getDailyMaintenance(userID)
    }

    override fun getTopMovie(userId: Int) = movieApi.getTopMovie(userId)

    override fun getNewMovie(userId: Int) = movieApi.getNewMovie(userId)

    override fun getFeatureMovie(
        userId: Int,
        minDuration: Int,
        maxDuration: Int,
        limit: Int,
        page: Int,
        ro: String?
    ) = movieApi.getFeatureMovie(userId, minDuration, maxDuration, limit, page, ro)

    override fun getUpsell(userId: Int, referenceVideoId: Int?, limit: Int, page: Int) =
        movieApi.getUpsell(userId, referenceVideoId, limit, page)

    override fun getUpSellBestSeller(userID: Int, limit: Int, page: Int): Call<UpsellEntity> {
        return movieApi.getUpSellBestSeller(userID, limit, page)
    }

    override fun getSearchResult(
        userId: Int,
        searchKeyword: String?,
        equipmentIds: List<Int>?,
        collection: String?,
        focusAreas: List<Int>?,
        minDuration: Int?,
        maxDuration: Int?,
        limit: Int,
        page: Int
    ) = movieApi.getSearchResult(
        userId, searchKeyword, equipmentIds, collection, focusAreas,
        minDuration, maxDuration, limit, page
    )

    override fun getSearchOldMobilityWodResult(
        userID: Int,
        searchKeyword: String?,
        equipmentIds: List<Int>?,
        collection: String?,
        focusAreas: List<Int>?,
        minDuration: Int?,
        maxDuration: Int?,
        limit: Int,
        page: Int
    ): Call<SearchMovieEntity> {
        return movieApi.getSearchOldMobilityWodResult(
            userID,
            searchKeyword,
            equipmentIds,
            collection,
            focusAreas,
            minDuration,
            maxDuration,
            limit,
            page
        )
    }

    override fun getSearchDailyResult(
        userID: Int,
        searchKeyword: String?,
        focusAreas: List<Int>?,
        focusEquipments: List<Int>?,
        minDuration: Int?,
        maxDuration: Int?,
        limit: Int,
        page: Int
    ): Call<SearchMovieEntity> {
        return movieApi.getSearchDailyResult(
            userID,
            searchKeyword,
            focusAreas,
            focusEquipments,
            minDuration,
            maxDuration,
            limit,
            page
        )
    }

    override fun getFavourite(userID: Int, limit: Int, page: Int) =
        movieApi.getFavourite(userID, limit, page)

    override fun addFavourite(userID: Int, params: PostAddFavourite.Params) =
        movieApi.addFavourite(userID, params)

    override fun removeFavourite(userID: Int, movieID: Int) =
        movieApi.removeFavourite(userID, movieID)

    override fun getReferenceMovie(userID: Int, videoId: Int) =
        movieApi.getReferenceMovie(userID, videoId)

    override fun getMovieDetail(userID: Int?, movieID: Int?): Call<MovieDetailEntity> =
        movieApi.getMovieDetail(userID, movieID)

    override fun getBonusMovie(userId: Int?, bonusId: Int?, limit: Int, page: Int) =
        movieApi.getBonusMovie(userId, bonusId, limit, page)

    override fun trackMovie(userId: Int?, param: PostTrackingVideo.Params) =
        movieApi.trackMovie(userId, param)
}