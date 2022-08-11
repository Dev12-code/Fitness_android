package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.movie.GetMovieDetail
import com.cbi.app.trs.domain.usecases.movie.PosRemoveFavourite
import com.cbi.app.trs.domain.usecases.movie.PostAddFavourite
import com.cbi.app.trs.domain.usecases.movie.PostTrackingVideo
import javax.inject.Inject

class MoviePlayViewModel @Inject constructor(val postAddFavourite: PostAddFavourite, private val getMovieDetail: GetMovieDetail,
                                             private val postRemoveFavourite: PosRemoveFavourite,
                                             private val postTrackingVideo: PostTrackingVideo) : BaseViewModel() {
    var addFavouriteResult: MutableLiveData<BaseEntities> = MutableLiveData()
    var movieDetail: MutableLiveData<MovieData> = MutableLiveData()
    var removeFavouriteResult: MutableLiveData<BaseEntities> = MutableLiveData()
    var trackingUpdate: MutableLiveData<BaseEntities> = MutableLiveData()

    fun addFavourite(pair: Pair<Int, PostAddFavourite.Params>) = postAddFavourite(pair) { it.fold(::handleFailure, ::handleAddFavourite) }
    fun removeFavorite(pair: Pair<Int, Int>) = postRemoveFavourite(pair) { it.fold(::handleFailure, ::handleRemoveFavourite) }
    fun getMovieDetail(pair: Pair<Int, Int?>) = getMovieDetail(pair) { it.fold(::handleFailure, ::handleMovieDetail) }
    fun trackingVideo(userId: Int?, param: PostTrackingVideo.Params) = postTrackingVideo(Pair(userId, param)) { it.fold(::handleFailureNothing, ::handleTracking) }

    private fun handleFailureNothing(failure: Failure) {

    }

    private fun handleTracking(baseEntities: BaseEntities) {
        trackingUpdate.value = baseEntities
    }

    private fun handleRemoveFavourite(data: BaseEntities) {
        this.removeFavouriteResult.value = data
    }

    private fun handleMovieDetail(movieData: MovieData) {
        this.movieDetail.value = movieData
    }

    private fun handleAddFavourite(data: BaseEntities) {
        this.addFavouriteResult.value = data
    }
}