package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.bonus.BonusMovieEntity
import com.cbi.app.trs.domain.entities.movie.MovieDetailEntity
import com.cbi.app.trs.domain.entities.movie.MovieListEntity
import com.cbi.app.trs.domain.entities.movie.ReferenceMovieEntity
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.MovieService
import com.cbi.app.trs.domain.usecases.movie.PostAddFavourite
import com.cbi.app.trs.domain.usecases.movie.PostTrackingVideo
import javax.inject.Inject

interface MovieRepository {
    fun getDailyMaintenance(userId: Int): Either<Failure, List<MovieData>>
    fun getTopMovie(userId: Int): Either<Failure, List<MovieData>>
    fun getNewMovie(userId: Int): Either<Failure, List<MovieData>>
    fun getFeatureMovie(
        userId: Int,
        minDuration: Int,
        maxDuration: Int,
        limit: Int,
        page: Int,
        ro: String?
    ): Either<Failure, List<MovieData>>

    fun getUpSell(
        userId: Int,
        referenceVideoId: Int?,
        limit: Int,
        page: Int
    ): Either<Failure, UpsellEntity.Data>

    fun getUpSellBestSeller(userId: Int, limit: Int, page: Int): Either<Failure, UpsellEntity.Data>
    fun getSearchResult(
        userId: Int,
        searchKeyword: String?,
        equipmentIds: List<Int>?,
        collection: String?,
        focusAreas: List<Int>?,
        minDuration: Int?,
        maxDuration: Int?,
        limit: Int,
        page: Int
    ): Either<Failure, SearchMovieEntity.Data>

    fun getSearchOldMobilityResult(
        userId: Int,
        searchKeyword: String?,
        equipmentIds: List<Int>?,
        collection: String?,
        focusAreas: List<Int>?,
        minDuration: Int?,
        maxDuration: Int?,
        limit: Int,
        page: Int
    ): Either<Failure, SearchMovieEntity.Data>

    fun getSearchDaily(
        userId: Int,
        searchKeyword: String?,
        focusAreas: List<Int>?,
        focusEquipments: List<Int>?,
        minDuration: Int?,
        maxDuration: Int?,
        limit: Int,
        page: Int
    ): Either<Failure, SearchMovieEntity.Data>

    fun getFavourite(userId: Int, limit: Int, page: Int): Either<Failure, SearchMovieEntity.Data>
    fun addFavourite(userId: Int, params: PostAddFavourite.Params): Either<Failure, BaseEntities>
    fun removeFavourite(userId: Int, movieId: Int): Either<Failure, BaseEntities>
    fun getReferenceMovie(userid: Int, videoId: Int): Either<Failure, ReferenceMovieEntity>
    fun getMovieDetail(userId: Int?, movieId: Int?): Either<Failure, MovieData>

    fun getBonusVideoList(
        userId: Int?,
        bonusId: Int?,
        limit: Int,
        page: Int
    ): Either<Failure, BonusMovieEntity.Data>

    fun trackingVideo(userId: Int?, params: PostTrackingVideo.Params): Either<Failure, BaseEntities>

    class Network
    @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val service: MovieService
    ) : MovieRepository, BaseNetwork() {
        override fun getDailyMaintenance(userId: Int): Either<Failure, List<MovieData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getDailyMaintenance(userId), {
                    it.data
                }, MovieListEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getTopMovie(userId: Int): Either<Failure, List<MovieData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getTopMovie(userId), {
                    it.data
                }, MovieListEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getNewMovie(userId: Int): Either<Failure, List<MovieData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getNewMovie(userId), {
                    it.data
                }, MovieListEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getFeatureMovie(
            userId: Int,
            minDuration: Int,
            maxDuration: Int,
            limit: Int,
            page: Int,
            ro: String?
        ): Either<Failure, List<MovieData>> {
            return when (networkHandler.isConnected) {
                true -> request(
                    service.getFeatureMovie(
                        userId,
                        minDuration,
                        maxDuration,
                        limit,
                        page,
                        ro
                    ), {
                        it.data
                    }, MovieListEntity.empty()
                )
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getUpSell(
            userId: Int,
            referenceVideoId: Int?,
            limit: Int,
            page: Int
        ): Either<Failure, UpsellEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUpsell(userId, referenceVideoId, limit, page), {
                    it.data
                }, UpsellEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getUpSellBestSeller(
            userId: Int,
            limit: Int,
            page: Int
        ): Either<Failure, UpsellEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUpSellBestSeller(userId, limit, page), {
                    it.data
                }, UpsellEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
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
        ): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(
                    service.getSearchResult(
                        userId,
                        searchKeyword,
                        equipmentIds,
                        collection,
                        focusAreas,
                        minDuration,
                        maxDuration,
                        limit,
                        page
                    ), {
                        it.data
                    }, SearchMovieEntity.empty()
                )
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSearchOldMobilityResult(
            userId: Int,
            searchKeyword: String?,
            equipmentIds: List<Int>?,
            collection: String?,
            focusAreas: List<Int>?,
            minDuration: Int?,
            maxDuration: Int?,
            limit: Int,
            page: Int
        ): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(
                    service.getSearchOldMobilityWodResult(
                        userId,
                        searchKeyword,
                        equipmentIds,
                        collection,
                        focusAreas,
                        minDuration,
                        maxDuration,
                        limit,
                        page
                    ), {
                        it.data
                    }, SearchMovieEntity.empty()
                )
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSearchDaily(
            userId: Int,
            searchKeyword: String?,
            focusAreas: List<Int>?,
            focusEquipments: List<Int>?,
            minDuration: Int?,
            maxDuration: Int?,
            limit: Int,
            page: Int
        ): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(
                    service.getSearchDailyResult(
                        userId,
                        searchKeyword,
                        focusAreas,
                        focusEquipments,
                        minDuration,
                        maxDuration,
                        limit,
                        page
                    ), {
                        it.data
                    }, SearchMovieEntity.empty()
                )
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getFavourite(
            userId: Int,
            limit: Int,
            page: Int
        ): Either<Failure, SearchMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getFavourite(userId, limit, page), {
                    it.data
                }, SearchMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun addFavourite(
            userId: Int,
            params: PostAddFavourite.Params
        ): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.addFavourite(userId, params), {
                    it
                }, SearchMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun removeFavourite(userId: Int, movieId: Int): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.removeFavourite(userId, movieId), {
                    it
                }, SearchMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getReferenceMovie(
            userId: Int,
            videoId: Int
        ): Either<Failure, ReferenceMovieEntity> {
            return when (networkHandler.isConnected) {
                true -> request(service.getReferenceMovie(userId, videoId), {
                    it
                }, ReferenceMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMovieDetail(userId: Int?, movieId: Int?): Either<Failure, MovieData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMovieDetail(userId, movieId), {
                    it.data
                }, MovieDetailEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getBonusVideoList(
            userId: Int?,
            bonusId: Int?,
            limit: Int,
            page: Int
        ): Either<Failure, BonusMovieEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getBonusMovie(userId, bonusId, limit, page), {
                    it.data
                }, BonusMovieEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun trackingVideo(
            userId: Int?,
            params: PostTrackingVideo.Params
        ): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.trackMovie(userId, params), {
                    it
                }, BaseEntities())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}