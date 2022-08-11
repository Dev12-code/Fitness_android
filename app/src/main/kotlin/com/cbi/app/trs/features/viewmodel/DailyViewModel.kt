package com.cbi.app.trs.features.viewmodel

import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.cache.DownloadedMovieCache
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.entities.workout.WorkoutEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.mobility.GetMobilityStatus
import com.cbi.app.trs.domain.usecases.movie.*
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import com.cbi.app.trs.domain.usecases.workout.GetWorkoutArchetype
import com.cbi.app.trs.domain.usecases.workout.GetWorkoutEquipment
import com.cbi.app.trs.domain.usecases.workout.GetWorkoutPrePost
import com.cbi.app.trs.domain.usecases.workout.GetWorkoutSport
import com.cbi.app.trs.features.fragments.movies.DownloadTracker
import com.google.android.exoplayer2.RenderersFactory
import javax.inject.Inject

class DailyViewModel
@Inject constructor(private val getNewMovie: GetNewMovie,
                    private val getFeatureMovie: GetFeatureMovie,
                    private val getMobilityStatus: GetMobilityStatus,
                    private val getWorkoutEquipment: GetWorkoutEquipment,
                    private val getWorkoutPrePost: GetWorkoutPrePost,
                    private val getWorkoutSport: GetWorkoutSport,
                    private val getWorkoutArchetype: GetWorkoutArchetype,
                    private val postUpSell: PostUpSell,
                    private val upSellBestSellerUseCase: UpSellBestSellerUseCase,
                    private val postPurchaseToken: PostPurchaseToken,
                    private var downloadedMovieCache: DownloadedMovieCache,
                    private var downloadTracker: DownloadTracker,
                    private var renderersFactory: RenderersFactory,
                    private val dailyMaintenanceUseCase: DailyMaintenanceUseCase,
                    private val getUserProfile: GetUserProfile) : BaseViewModel() {
    var dailyMaintenance: MutableLiveData<List<MovieData>> = MutableLiveData()
    var topMovie: MutableLiveData<List<MovieData>> = MutableLiveData()
    var newMovie: MutableLiveData<List<MovieData>> = MutableLiveData()
    var featureMovie: MutableLiveData<List<MovieData>> = MutableLiveData()
    var mobilityStatus: MutableLiveData<MobilityStatus> = MutableLiveData()
    var purchaseTokenData: MutableLiveData<BaseEntities> = MutableLiveData()

    var workoutEquipment: MutableLiveData<WorkoutEntity.Data> = MutableLiveData()

    var workoutPrePost: MutableLiveData<WorkoutEntity.Data> = MutableLiveData()
    var workoutSport: MutableLiveData<WorkoutEntity.Data> = MutableLiveData()
    var workoutArchetype: MutableLiveData<WorkoutEntity.Data> = MutableLiveData()
    var upsell: MutableLiveData<UpsellEntity.Data> = MutableLiveData()
    var upSellBestSeller: MutableLiveData<UpsellEntity.Data> = MutableLiveData()
    var userProfile: MutableLiveData<UserData.UserProfile> = MutableLiveData()

    fun sendPurchaseToken(userId: Int?, param: PostPurchaseToken.Params) = postPurchaseToken(Pair(userId, param)) { it.fold(::handleFailure, ::handleSendPurchaseToken) }

    private fun handleSendPurchaseToken(baseEntities: BaseEntities) {
        this.purchaseTokenData.value = baseEntities
    }

    fun getNewMovie(userId: Int) = getNewMovie(userId) { it.fold(::handleFailure, ::handleNewMovie) }
    fun getFeatureMovie(param: Pair<Int, GetFeatureMovie.Params>) = getFeatureMovie(param) { it.fold(::handleFailure, ::handleFeatureMovie) }
    fun getMobilityStatus(userId: Int) = getMobilityStatus(userId) { it.fold(::handleFailure, ::handleMobilityStatus) }
    fun getWorkoutEquipment(userId: Int, paging: PagingParam) = getWorkoutEquipment(Pair(userId, paging)) { it.fold(::handleFailure, ::handleWorkoutEquipment) }
    fun getWorkoutArchetype(userId: Int, paging: PagingParam) = getWorkoutArchetype(Pair(userId, paging)) { it.fold(::handleFailure, ::handleWorkoutArchetype) }
    fun getWorkoutPrePost(userId: Int, paging: PagingParam) = getWorkoutPrePost(Pair(userId, paging)) { it.fold(::handleFailure, ::handleWorkoutPrePost) }
    fun getWorkoutSport(userId: Int, paging: PagingParam) = getWorkoutSport(Pair(userId, paging)) { it.fold(::handleFailure, ::handleWorkoutSport) }
    fun getUpSell(pair: Pair<Int, PostUpSell.Params>) = postUpSell(pair) { it.fold(::handleFailure, ::handleUpsell) }
    fun getUpSellBestSeller(pair: Pair<Int, PagingParam>) = upSellBestSellerUseCase(pair) { it.fold(::handleFailure, ::handleUpSellBestSeller) }
    fun getUserProfile(userID: Int) = getUserProfile(userID) { it.fold(::handleFailure, ::handleUserProfile) }
    fun getDailyMaintenance(userId: Int) = dailyMaintenanceUseCase(userId) {
        it.fold(::handleFailure, ::handleDailyMaintenance)
    }

    private fun handleDailyMaintenance(list: List<MovieData>) {
        this.dailyMaintenance.value = list
    }

    private fun handleUpsell(data: UpsellEntity.Data) {
        this.upsell.value = data
    }

    private fun handleUpSellBestSeller(data: UpsellEntity.Data) {
        this.upSellBestSeller.value = data
    }

    private fun handleWorkoutSport(data: WorkoutEntity.Data) {
        this.workoutSport.value = data
    }

    private fun handleWorkoutPrePost(data: WorkoutEntity.Data) {
        this.workoutPrePost.value = data
    }

    private fun handleWorkoutEquipment(data: WorkoutEntity.Data) {
        this.workoutEquipment.value = data
    }

    private fun handleWorkoutArchetype(data: WorkoutEntity.Data) {
        this.workoutArchetype.value = data
    }

    private fun handleNewMovie(list: List<MovieData>) {
        this.newMovie.value = list
    }

    private fun handleTopMovie(list: List<MovieData>) {
        this.topMovie.value = list
    }

    private fun handleFeatureMovie(list: List<MovieData>) {
        this.featureMovie.value = list
    }

    private fun handleMobilityStatus(item: MobilityStatus) {
        this.mobilityStatus.value = item
    }

    private fun handleUserProfile(userProfile: UserData.UserProfile) {
        this.userProfile.value = userProfile
    }

    fun resumeDownloadVideo(fragmentManager: FragmentManager) {
        for (movie in downloadedMovieCache.get().list) {
            val uri = Uri.parse(movie.video_play_url)
            if (downloadTracker.getDownloadStatus(uri) != DownloadTracker.DownloadState.DOWNLOADED
                    && downloadTracker.getDownloadStatus(uri) != DownloadTracker.DownloadState.DOWNLOADING) {
                downloadTracker.makeDownload(fragmentManager, "Download", uri, "", renderersFactory)
            }
        }
    }
}