package com.cbi.app.trs.features.fragments.movies

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.content.res.Resources
import android.net.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.View.*
import android.widget.FrameLayout
import android.widget.Toast
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.data.cache.DownloadedMovieCache
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.eventbus.FavouriteEvent
import com.cbi.app.trs.domain.usecases.movie.PostAddFavourite
import com.cbi.app.trs.domain.usecases.movie.PostTrackingVideo
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.dialog.NoInternetDialog
import com.cbi.app.trs.features.fragments.bonus_content.BonusContentDetailFragment
import com.cbi.app.trs.features.fragments.mobility.test.MobilityTestFragment
import com.cbi.app.trs.features.fragments.movies.movies_detail.MovieDetailFragment
import com.cbi.app.trs.features.fragments.movies.pain_detail.PainDetailFragment
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.utils.CountdownTimer
import com.cbi.app.trs.features.utils.NetworkMonitorUtil
import com.cbi.app.trs.features.viewmodel.MoviePlayViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.ExoMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.exo_playback_control_view.view.*
import kotlinx.android.synthetic.main.fragment_movie_play.*
import org.greenrobot.eventbus.EventBus
import java.net.CookieManager
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.max

class MoviePlayFragment : DarkBaseFragment(), PlayerControlView.VisibilityListener,
    PlaybackPreparer, DownloadTracker.Listener, RemoteMediaClient.ProgressListener {
    var screenWidth = Resources.getSystem().displayMetrics.widthPixels
    var playHeight = 0

    private var playerEventListener: Player.EventListener? = null
    private var uri: Uri? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var downloadTracker: DownloadTracker

    @Inject
    lateinit var dataSourceFactory: DataSource.Factory

    @Inject
    lateinit var cookieManager: CookieManager

    @Inject
    @Named("UserAgent")
    lateinit var userAgent: String

    @Inject
    lateinit var renderersFactory: RenderersFactory

    @Inject
    lateinit var httpDataSourceFactory: HttpDataSource.Factory

    @Inject
    lateinit var downloadedMovieCache: DownloadedMovieCache

    private lateinit var networkMonitor: NetworkMonitorUtil

    private var castContext: CastContext? = null
    private var castSession: CastSession? = null
    private var mSessionManagerListener: SessionManagerListener<CastSession>? = null

    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null

    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0

    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null

    private var isFullScreen = false

    private var mLocation: PlaybackLocation = PlaybackLocation.LOCAL

    private var contentFragmentType = ContentFragmentType.MOVIE

    private lateinit var moviePlayViewModel: MoviePlayViewModel

    var movieData: MovieData? = null

    var needInitViewChildFragment = true

    var isOfflineMode = false

    var isOpenSetting = false

    var isAllowTracking = true

    private var countdownTimer: CountdownTimer? = null

    companion object {
        const val MOBILITY_STATUS = "MOBILITY_STATUS"

        const val MOBILITY_RETEST = "MOBILITY_RETEST"

        const val CONTENT_FRAGMENT = "content_fragment"

        //For MovieDetail
        const val MOVIE_DETAIL = "MOVIE_DETAIL"
        const val MOVIE_ID = "MOVIE_ID"
        const val MOVIE_THUMBNAIL = "MOVIE_THUMBNAIL"

        // For PainDetail
        const val PAIN_AREA = "PAIN_AREA"

        // For Bonus Content
        const val BONUS_DETAIL = "BONUS_DETAIL"

        const val OFF_LINE = "OFF_LINE"

        // Saved instance state keys.
        const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        const val KEY_WINDOW = "window"
        const val KEY_POSITION = "position"
        const val KEY_AUTO_PLAY = "auto_play"
    }

    /**
     * indicates whether we are doing a local or a remote playback
     */
    enum class PlaybackLocation {
        LOCAL, REMOTE
    }

    enum class ContentFragmentType(val value: String) {
        MOVIE("movie_detail"), PAIN("pain_detail"), MOBILITY("mobility"), BONUS("bonus")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        moviePlayViewModel = viewModel(viewModelFactory) {
            observe(addFavouriteResult, ::onReceiveAddFavourite)
            observe(removeFavouriteResult, ::onReceiveRemoveFavourite)
            observe(movieDetail, ::onReceiveMovieDetail)
            observe(trackingUpdate, ::onReceiveTrackingUpdate)
            failure(failureData, ::handleFailure)
        }
        castContext = CastContext.getSharedInstance(activity!! as MainActivity)
        castSession = castContext?.sessionManager?.currentCastSession
        castContext?.sessionManager?.endCurrentSession(true)

        downloadTracker.addListener(this)
        arguments?.let { isOfflineMode = it.getBoolean(OFF_LINE) }

        if (savedInstanceState != null) {
            trackSelectorParameters =
                savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            val builder = ParametersBuilder(activity!!)
            val tunneling = false
            if (Util.SDK_INT >= 21 && tunneling) {
                builder.setTunnelingAudioSessionId(C.generateAudioSessionIdV21(activity!!))
            }
            trackSelectorParameters = builder.build()
            clearStartPosition()
        }
//        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onDestroy() {
        super.onDestroy()
//        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun onReceiveTrackingUpdate(baseEntities: BaseEntities?) {
        hideProgress()
    }

    private fun onReceiveRemoveFavourite(data: BaseEntities?) {
        hideProgress()
        if (data == null) return
        when (data.isSuccess) {
            true -> {
                movieData?.video_is_favorite = false
                movieDetailsAddFavouriteBtn.isSelected = false
                EventBus.getDefault().post(FavouriteEvent(movieData))

                DialogAlert()
                    .setTitle("Video Removed")
                    .setMessage("You have removed this video from your Favorites.")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
            }
            false -> {
                DialogAlert()
                    .setTitle("Favourite")
                    .setMessage("Remove from your favourite failed!")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
            }
        }
    }

    fun onReceiveMovieDetail(movieData: MovieData?) {
        hideProgress()
        if (movieData == null) return
        updateTrackingVideo()
        this.movieData = movieData
        uri = Uri.parse(movieData.video_play_url)
        initView()
        loadMovieInformation()
        releasePlayer()
        clearStartPosition()
        initializePlayer()
    }

    fun playMovie(movieData: MovieData?) {
        Handler().postDelayed({
            hideProgress()
        }, 500)
        if (movieData == null) return
        updateTrackingVideo()
        this.movieData = movieData
        artCover.loadFromUrl(movieData.image_thumbnail)
        movieCover_image.loadFromUrl(movieData.image_thumbnail)
        try {
            uri = Uri.parse(movieData.video_play_url)
        } catch (e: java.lang.Exception) {

        }
        releasePlayer()
        clearStartPosition()
        initializePlayer()
        loadMovieInformation()
        //release countdown timer
        countdownTimer?.cancel()
        countdownTimer = null
        //reset flag
        isAllowTracking = true
        isAllowTracking = true
    }

    private fun onReceiveAddFavourite(data: BaseEntities?) {
        hideProgress()
        if (data == null) return
        when (data.isSuccess) {
            true -> {
                movieData?.video_is_favorite = true
                movieDetailsAddFavouriteBtn.isSelected = true
                EventBus.getDefault().post(FavouriteEvent(movieData))
                DialogAlert()
                    .setTitle("Video Added!")
                    .setMessage("You have added this video to your Favorites.")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
            }
            false -> {
                DialogAlert()
                    .setTitle("Add video to favorite list")
                    .setMessage("Video added failed!")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(requireContext())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayerView()
        if (movieData == null && arguments?.getInt(MOVIE_ID, -1) != -1) {
            loadData()
        } else {
            initView()
            loadMovieInformation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!::networkMonitor.isInitialized) {
            networkMonitor = NetworkMonitorUtil(context)
        }
        if (::networkMonitor.isInitialized) {
            networkMonitor.register()
            networkMonitor.result = { isAvailable, type ->
                if (!isAvailable && isOpenSetting) {
                    showNetworkDialog()
                }
            }
        }
    }

    override fun onReloadData() {
        loadData()
    }

    fun callMovieInformation(movieID: Int?) {
        arguments?.getString(MOVIE_THUMBNAIL)?.let {
            movieCover.visibility = VISIBLE
            movieCover_image.visibility = VISIBLE
            movieCover_image.loadFromUrl(it)
            artCover.loadFromUrl(it)
        }

        userDataCache.get()?.user_token?.userID?.let {
            showProgress()
            moviePlayViewModel.getMovieDetail(
                kotlin.Pair(
                    it,
                    movieID
                )
            )
        }

    }

    private fun loadData() {
        when {
            arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOVIE.value -> {
                if (arguments?.getInt(MOVIE_ID, -1) != -1) {
                    callMovieInformation(arguments?.getInt(MOVIE_ID, -1))
                }
            }
            arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.PAIN.value -> {
            }
            arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value -> {
            }
            arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.BONUS.value -> {
            }
        }
        //reset countdown timer
        countdownTimer?.cancel()
        countdownTimer = null
        //reset flag
        isAllowTracking = true
    }


    fun updateHeaderText(value: String) {
        //show header
        tv_test_header.visibility = VISIBLE
        tv_test_header.text = value
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustFontScale(activity?.resources?.configuration)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            player_view.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
            movieCover.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            isFullScreen = true
            player_view.exo_fullscreen_button.visibility = VISIBLE
            player_view.back_btn.visibility = View.GONE
            (player_view.layoutParams as FrameLayout.LayoutParams).topMargin = 0
            (movieCover.layoutParams as FrameLayout.LayoutParams).topMargin = 0

            if (arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value) {
                (tv_test_header.layoutParams as FrameLayout.LayoutParams).topMargin = 0
            }
//            player_view.exo_rew.visibility = VISIBLE
//            player_view.exo_ffwd.visibility = VISIBLE
            player_view.exo_bottom.visibility = VISIBLE
        } else {
            if (playHeight > 0) {
                player_view.layoutParams.height = playHeight
                movieCover.layoutParams.height = playHeight
            } else {
                player_view.layoutParams.height =
                    resources.getDimensionPixelSize(R.dimen.movie_play_height)
                movieCover.layoutParams.height =
                    resources.getDimensionPixelSize(R.dimen.movie_play_height)
            }
            (activity as BaseActivity).changeFullScreenMode(true)
            isFullScreen = false
            player_view.exo_fullscreen_button.visibility = View.GONE
            player_view.back_btn.visibility = VISIBLE

            (player_view.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarHeight.dp2px
            (movieCover.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarHeight.dp2px
            if (arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value) {
                (tv_test_header.layoutParams as FrameLayout.LayoutParams).topMargin =
                    statusBarHeight.dp2px + 10.dp2px
                //change top  margin
                (player_view.layoutParams as FrameLayout.LayoutParams).topMargin =
                    statusBarHeight.dp2px + 40.dp2px
                (movieCover.layoutParams as FrameLayout.LayoutParams).topMargin =
                    statusBarHeight.dp2px + 40.dp2px
            }

//            player_view.exo_rew.visibility = GONE
//            player_view.exo_ffwd.visibility = GONE
//            player_view.exo_bottom.visibility = GONE
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun handleFullScreen() {
        if (isFullScreen) {
            activity?.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        } else {
            activity?.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun layoutId() = R.layout.fragment_movie_play

    override fun onBackPressed(): Boolean {
        if (arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value) {
            dialogConfirmCloseTest()
            return true
        }
        return if (isFullScreen) {
            handleFullScreen()
            true
        } else {
            super.onBackPressed()
        }
    }

    private fun initPlayerView() {
        player_view.setControllerVisibilityListener(this)
        player_view.setErrorMessageProvider(PlayerErrorMessageProvider())
        player_view.requestFocus()

        if (playHeight > 0) {
            player_view.layoutParams.height = playHeight
            movieCover.layoutParams.height = playHeight
        } else {
            player_view.layoutParams.height =
                resources.getDimensionPixelSize(R.dimen.movie_play_height)
            movieCover.layoutParams.height =
                resources.getDimensionPixelSize(R.dimen.movie_play_height)
        }
        (activity as BaseActivity).changeFullScreenMode(true)
        isFullScreen = false
        player_view.exo_fullscreen_button.visibility = View.GONE
        player_view.back_btn.visibility = VISIBLE

        (player_view.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarHeight.dp2px
        (movieCover.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarHeight.dp2px
        if (arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value) {
            (tv_test_header.layoutParams as FrameLayout.LayoutParams).topMargin =
                statusBarHeight.dp2px + 10.dp2px
            //change top  margin
            (player_view.layoutParams as FrameLayout.LayoutParams).topMargin =
                statusBarHeight.dp2px + 40.dp2px
            (movieCover.layoutParams as FrameLayout.LayoutParams).topMargin =
                statusBarHeight.dp2px + 40.dp2px
        }
    }

    private fun loadMovieInformation() {
        movieData?.let {
            setupCastListener()
//            castSession?.let { loadCastInformation() }
            movieDetailsAddFavouriteBtn.isSelected = it.video_is_favorite
            artCover.loadFromUrl(it.image_thumbnail)
            movieCover_image.loadFromUrl(it.image_thumbnail)
            movieCover.visibility = VISIBLE
            movieCover_image.visibility = VISIBLE
            movieDetailsFullScreenBtn?.isEnabled = false
        }
    }

    private fun initView() {

        if (needInitViewChildFragment) {
            needInitViewChildFragment = false
            when {
                arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOVIE.value -> {
                    setFragment(childFragmentManager, MovieDetailFragment().apply {
                        arguments = Bundle().apply {
                            putBoolean(OFF_LINE, this@MoviePlayFragment.isOfflineMode)
                            putParcelable(MOVIE_DETAIL, movieData)
                        }
                    }, R.id.movie_play_content_fragment)
                }
                arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.PAIN.value -> {
                    setFragment(
                        childFragmentManager,
                        PainDetailFragment().apply { arguments = this@MoviePlayFragment.arguments },
                        R.id.movie_play_content_fragment
                    )
                }
                arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value -> {
                    setFragment(childFragmentManager, MobilityTestFragment().apply {
                        arguments = this@MoviePlayFragment.arguments
                    }, R.id.movie_play_content_fragment)
                }
                arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.BONUS.value -> {
                    setFragment(
                        childFragmentManager,
                        BonusContentDetailFragment().apply {
                            arguments = this@MoviePlayFragment.arguments
                        },
                        R.id.movie_play_content_fragment
                    )
                }
            }
        }

        CastButtonFactory.setUpMediaRouteButton(
            activity!!.applicationContext,
            movieDetailsCastMediaRouterBtn
        )

        movieDetailsDownload.setOnClickListener {
            if (movieData == null || !isAllowForFreemium()) return@setOnClickListener
            downloadMovie(movieData!!)
        }

        artCover.setOnClickListener { }

        movieDetailsFullScreenBtn.setOnClickListener {
            if (movieData == null) return@setOnClickListener
            handleFullScreen()
        }

        movieDetailsCastBtn.setOnClickListener {
            if (movieData == null) return@setOnClickListener
            movieDetailsCastMediaRouterBtn.performClick()
        }

        movieDetailsExitFullScreen.setOnClickListener {
            if (movieData == null) return@setOnClickListener
            handleFullScreen()
        }
        movieDetailsAddFavouriteBtn.setOnClickListener {
            if (movieData == null || !isAllowForFreemium()) return@setOnClickListener
            userDataCache.get()?.user_token?.userID?.let {
                if (movieData != null) {
                    if (!movieData!!.video_is_favorite) {
                        showProgress()
                        moviePlayViewModel.addFavourite(
                            kotlin.Pair(
                                it,
                                PostAddFavourite.Params(movieData!!.video_id)
                            )
                        )
                    } else {
                        DialogAlert().setTitle("Remove from Favourite?")
                            .setMessage("Are you sure you want to remove this video? ")
                            .setTitleNegative("Cancel").setTitlePositive("OK").onPositive {
                                showProgress()
                                moviePlayViewModel.removeFavorite(
                                    kotlin.Pair(
                                        it,
                                        movieData!!.video_id
                                    )
                                )
                            }.show(requireContext())
                    }
                }
            }
        }

        player_view.back_btn.setOnClickListener {
            if (arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.MOBILITY.value) {
                dialogConfirmCloseTest()
                return@setOnClickListener
            }
            close()
        }
    }

    private fun dialogConfirmCloseTest() {
        //load pre video
        val childFrameLayout =
            childFragmentManager.findFragmentById(R.id.movie_play_content_fragment)
        if (childFrameLayout != null && childFrameLayout is MobilityTestFragment) {
            val currentPosition = childFrameLayout.currentPosition
            if (currentPosition < 1) {
                //close view
                close()
            } else {
                //load pre video
                childFrameLayout.loadPreTestVideo()
            }
        }
    }

    fun downloadMovie(movieData: MovieData) {
        if (downloadedMovieCache.get().list.size >= 5) {
            CommonUtils.showError(activity, "Sorry", "Sorry, Only 5 Video Downloads Allowed.")
            return
        }
        try {
            val uri = Uri.parse(movieData.video_play_url)
            when {
                downloadTracker.getDownloadStatus(uri) == DownloadTracker.DownloadState.DOWNLOADED -> {
                    activity?.let {
                        DialogAlert()
                            .setTitle("Download Video")
                            .setMessage("Video Finished Downloading")
                            .setCancel(false)
                            .setTitlePositive("OK")
                            .onPositive {
//                                downloadTracker.removeDownload(uri)
//                                downloadedMovieCache.put(downloadedMovieCache.get().apply { removeMovie(movieData) })
                            }
                            .show(requireContext())
                    }
                }
                downloadTracker.getDownloadStatus(uri) == DownloadTracker.DownloadState.DOWNLOADING -> {
                    activity?.let {
                        DialogAlert()
                            .setTitle("Download Video")
                            .setMessage("Your video is downloading.\nDo you want to stop?")
                            .setCancel(false)
                            .setTitlePositive("OK")
                            .setTitleNegative("Cancel")
                            .onPositive {
                                downloadTracker.removeDownload(uri)
                                downloadedMovieCache.put(
                                    downloadedMovieCache.get().apply { removeMovie(movieData) })
                            }
                            .show(requireContext())
                    }
                }
                else -> {
                    downloadTracker.makeDownload(
                        fragmentManager,
                        "Download",
                        uri,
                        "",
                        renderersFactory
                    )
                    downloadedMovieCache.put(
                        downloadedMovieCache.get().apply {
                            addOrUpdateMovie(movieData.apply {
                                downloadedDate = System.currentTimeMillis() / 1000
                            })
                        })
                    Toast.makeText(activity, "Your video is downloading..", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: Exception) {

        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            if (player_view != null) {
                player_view.onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        (activity as MainActivity).unchangeFullScreenMode()
        (activity as MainActivity).navigationView.visibility = View.GONE
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            if (player_view != null) {
                player_view.onResume()
            }
        }
        if (mSessionManagerListener != null) {
            castContext?.sessionManager?.addSessionManagerListener(
                mSessionManagerListener, CastSession::class.java
            )
        }
        if (castSession != null && castSession!!.isConnected) {
            updatePlaybackLocation(PlaybackLocation.REMOTE)
        } else {
            updatePlaybackLocation(PlaybackLocation.LOCAL)
        }

        Handler().postDelayed({
            //Check network reality
            registerNetworkListener()
        }, 2000)

        if (::networkMonitor.isInitialized) {
            networkMonitor.register()
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as BaseActivity).changeFullScreenMode()
        (activity as MainActivity).navigationView.visibility = VISIBLE
        countdownTimer?.pause()
        if (Util.SDK_INT <= 23) {
            if (player_view != null) {
                player_view.onPause()
            }
            updateTrackingVideo()
            releasePlayer()
        }
        castContext?.sessionManager?.removeSessionManagerListener(
            mSessionManagerListener, CastSession::class.java
        )

        unregisterNetworkListener()
    }

    private fun registerNetworkListener() {
        unregisterNetworkListener()

        try {
            val connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
                connectivityManager.registerNetworkCallback(request, networkCallback)
            }
        } catch (e: Exception) {

        }
    }

    private fun unregisterNetworkListener() {
        try {
            val connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {

        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (Util.SDK_INT > 23) {
                if (player_view != null) {
                    player_view.onPause()
                }
                updateTrackingVideo()
                releasePlayer()
            }

            if (::networkMonitor.isInitialized) {
                networkMonitor.unregister()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Internal methods
    private fun initializePlayer() {
        if (player == null) {
            mediaSource = arguments?.let { createTopLevelMediaSource(it) }
            if (mediaSource == null) {
                return
            }
            player = activity?.let {
                SimpleExoPlayer.Builder(it, renderersFactory)
                    .build()
            }
            if (playerEventListener == null) playerEventListener = PlayerEventListener()
            player?.addListener(playerEventListener!!)
            player?.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
            player?.playWhenReady = mLocation == PlaybackLocation.LOCAL
            player?.addAnalyticsListener(EventLogger(trackSelector))
            player_view.player = player
            player_view.setPlaybackPreparer(this)
        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(startWindow, startPosition)
        }
        player?.prepare(mediaSource!!, !haveStartPosition, false)
    }

    private fun createTopLevelMediaSource(arg: Bundle): MediaSource? {
        if (uri == null) return null
        val downloadRequest: DownloadRequest? = downloadTracker.getDownloadRequest(uri)
        return if (downloadRequest != null) {
            DownloadHelper.createMediaSource(downloadRequest, dataSourceFactory)
        } else
            uri?.let {
                createLeafMediaSource(
                    it,
                    DrmSessionManager.getDummyDrmSessionManager<ExoMediaCrypto>()
                )
            }
    }

    private fun createLeafMediaSource(
        uri: Uri, drmSessionManager: DrmSessionManager<*>
    ): MediaSource? {
        return when (@C.ContentType val type = Util.inferContentType(uri, "")) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                .setDrmSessionManager(drmSessionManager)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                .setDrmSessionManager(drmSessionManager)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .setDrmSessionManager(drmSessionManager)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .setDrmSessionManager(drmSessionManager)
                .createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }


    private fun showToast(messageId: Int) {
        showToast(getString(messageId))
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun releasePlayer() {
        if (player != null) {
            updateStartPosition()
            player!!.release()
            player = null
            mediaSource = null
            trackSelector = null
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = max(0, player!!.contentPosition)
        }
    }

    private fun updateTrackingVideo() {
        if (player != null && player!!.contentDuration > 0) {
            val watchPercent = player!!.contentPosition / player!!.contentDuration.toDouble()
            if (watchPercent > 0) movieData?.let {
                moviePlayViewModel.trackingVideo(
                    userID,
                    PostTrackingVideo.Params(it.video_id, watchPercent * 100)
                )
            }
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }

    inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            @Player.State playbackState: Int
        ) {
            //prevent screen light off when watching video
            player_view.keepScreenOn =
                !(playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED || !playWhenReady)

            //countdown timer to detect video watch
            if (playbackState == Player.STATE_READY && playWhenReady) {
                //start timer if needed
                if (countdownTimer == null && player != null) {
                    countdownTimer = object : CountdownTimer(player!!.contentDuration, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            //call API tracking/watch when user see 80% total video
                            val percentRemain =
                                (millisUntilFinished.toDouble() / player!!.contentDuration.toDouble()) * 100
                            if (percentRemain <= 18 && isAllowTracking) {
                                updateTrackingVideo()
                                isAllowTracking = false
                            }
                        }

                        override fun onFinish() {

                        }

                    }
                }
                (countdownTimer as CountdownTimer).start()
            } else {
                //stop timer
                countdownTimer?.pause()
            }
            AppLog.e("Duy", "onPlayerStateChanged : $playbackState ")
            movieCover.visibility = GONE
            if (playbackState == Player.STATE_READY && mLocation == PlaybackLocation.LOCAL) {
                player?.videoFormat?.let {
                    val ratio = it.height / it.width.toFloat()
                    if (ratio < 1 && !isFullScreen) {
                        playHeight = (screenWidth * ratio).toInt()
                        player_view.layoutParams.height = playHeight
                        movieCover.layoutParams.height = playHeight
                    }
                }
                movieDetailsFullScreenBtn?.isEnabled = true
            }

            if (playbackState == Player.STATE_BUFFERING && mLocation == PlaybackLocation.LOCAL) {
                movieCover.visibility = VISIBLE
                if (player != null && player!!.contentPosition > 0) {
                    movieCover_image.visibility = INVISIBLE
                }
            }

            if (playbackState == Player.STATE_ENDED) {
                //Watch video QA, 2/3 time will popup message
                if (arguments?.getString(CONTENT_FRAGMENT) == ContentFragmentType.BONUS.value) {
                    DialogAlert()
                        .setTitle("Congratulations!")
                        .setMessage("Video Completed")
                        .setCancel(false)
                        .setTitlePositive("OK")
                        .show(requireContext())
                }
                //Load next video of Pain Detail
                try {
                    if ((isFullScreen)) handleFullScreen()
                    val childFrameLayout =
                        childFragmentManager.findFragmentById(R.id.movie_play_content_fragment)
                    if (childFrameLayout != null) {
                        when (childFrameLayout) {
                            is PainDetailFragment -> {
                                childFrameLayout.loadNextMovie()
                            }
                            is BonusContentDetailFragment -> {
                                childFrameLayout.loadNextMovie()
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                }
            }
        }

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition()
                initializePlayer()
            } else {
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
        }
    }

    inner class PlayerErrorMessageProvider : ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString: String = getString(R.string.error_generic)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    errorString = if (cause.codecInfo == null) {
                        when {
                            cause.cause is DecoderQueryException -> {
                                getString(R.string.error_querying_decoders)
                            }
                            cause.secureDecoderRequired -> {
                                getString(
                                    R.string.error_no_secure_decoder, cause.mimeType
                                )
                            }
                            else -> {
                                getString(R.string.error_no_decoder, cause.mimeType)
                            }
                        }
                    } else {
                        getString(
                            R.string.error_instantiating_decoder,
                            cause.codecInfo!!.name
                        )
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    override fun preparePlayback() {
        player?.retry()
    }

    private fun setupCastListener() {
        castContext?.sessionManager?.endCurrentSession(true)
        mSessionManagerListener?.let {
            castContext?.sessionManager?.removeSessionManagerListener(
                it, CastSession::class.java
            )
        }
        mSessionManagerListener = object : SessionManagerListener<CastSession> {
            override fun onSessionEnded(session: CastSession, error: Int) {
                AppLog.e("Duy", "onSessionEnded")
                onApplicationDisconnected()
            }

            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                AppLog.e("Duy", "onSessionResumed")
                onApplicationConnected(session)
            }

            override fun onSessionResumeFailed(session: CastSession, error: Int) {
                AppLog.e("Duy", "onSessionResumeFailed")
                onApplicationDisconnected()
            }

            override fun onSessionStarted(session: CastSession, sessionId: String) {
                AppLog.e("Duy", "onSessionStarted")
                onApplicationConnected(session)
            }

            override fun onSessionStartFailed(session: CastSession, error: Int) {
                AppLog.e("Duy", "onSessionStartFailed")
                onApplicationDisconnected()
            }

            override fun onSessionStarting(session: CastSession) {
                AppLog.e("Duy", "onSessionStarting")
            }

            override fun onSessionEnding(session: CastSession) {
                AppLog.e("Duy", "onSessionEnding")
                onApplicationDisconnected()
            }

            override fun onSessionResuming(session: CastSession, sessionId: String) {
                AppLog.e("Duy", "onSessionResuming")
            }

            override fun onSessionSuspended(session: CastSession, reason: Int) {
                AppLog.e("Duy", "onSessionSuspended")
            }

            private fun onApplicationConnected(castSession: CastSession) {
                AppLog.e("Duy", "onApplicationConnected")
                this@MoviePlayFragment.castSession = castSession
                loadCastInformation()
            }

            private fun onApplicationDisconnected() {
                updatePlaybackLocation(PlaybackLocation.LOCAL)
            }
        }
        castContext?.sessionManager?.addSessionManagerListener(
            mSessionManagerListener, CastSession::class.java
        )
    }

    private fun loadCastInformation() {
        if (null != movieData && player != null && this@MoviePlayFragment.castSession != null) {
            player?.playWhenReady = false
            loadRemoteMedia(
                activity!!,
                uri!!,
                this@MoviePlayFragment.castSession!!,
                max(0, player!!.contentPosition),
                true
            )
            AppLog.e("Duy", "loadCastInformation:${movieData!!.video_play_url}")
            return
        }
    }

    private fun updatePlaybackLocation(location: PlaybackLocation) {
        AppLog.e("Duy", "updatePlaybackLocation : $location")
        mLocation = location
        if (location == PlaybackLocation.LOCAL) {
            artCover?.visibility = GONE
            movieDetailsFullScreenBtn?.isEnabled = true
            player?.playWhenReady = true
        } else {
            artCover?.visibility = VISIBLE
            movieCover.visibility = GONE
            movieDetailsFullScreenBtn?.isEnabled = false
            player?.playWhenReady = false
        }
    }

    fun loadRemoteMedia(
        context: Context,
        uri: Uri,
        castSession: CastSession,
        position: Long,
        autoPlay: Boolean
    ) {
        if (castSession == null) {
            return
        }
        val remoteMediaClient: RemoteMediaClient = castSession.remoteMediaClient ?: return
        remoteMediaClient.registerCallback(object : RemoteMediaClient.Callback() {
            override fun onStatusUpdated() {
                val intent = Intent(context, ExpandedControlsActivity::class.java)
                context.startActivity(intent)
                remoteMediaClient.unregisterCallback(this)
            }
        })
        remoteMediaClient.addProgressListener(this, 1000)
        remoteMediaClient.load(
            MediaLoadRequestData.Builder()
                .setMediaInfo(buildMediaInfo())
                .setAutoplay(autoPlay)
                .setCurrentTime(position).build()
        )
    }

    private fun buildMediaInfo(): MediaInfo? {
        if (movieData == null) return null
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        movieMetadata.putString(MediaMetadata.KEY_TITLE, movieData!!.video_title)
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, movieData!!.video_description)
        movieMetadata.addImage(WebImage(Uri.parse(movieData!!.image_thumbnail)))

        return MediaInfo.Builder(movieData!!.video_play_url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("application/x-mpegurl")
            .setMetadata(movieMetadata)
//                .setStreamDuration(player!!.contentDuration)
            .build()
    }

    override fun onVisibilityChange(visibility: Int) {
    }

    override fun onDownloadsChanged(uri: Uri, state: Int) {
        AppLog.e("Duy", "onDownloadsChanged: $state")
        when (state) {
            Download.STATE_COMPLETED -> {
                downloadedMovieCache.put(
                    downloadedMovieCache.get().apply { updateDownloadStatusMovie(uri) })
                Handler().postDelayed({
                    try {
                        val childFrameLayout =
                            childFragmentManager.findFragmentById(R.id.movie_play_content_fragment)
                        if ((childFrameLayout != null && childFrameLayout is PainDetailFragment)) {
                            childFrameLayout.understandingAdapter.notifyDataSetChanged()
                            childFrameLayout.mobilityRxAdapter.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {

                    }
                }, 500)
            }
            Download.STATE_FAILED -> {
                downloadedMovieCache.put(downloadedMovieCache.get().apply { removeMovie(uri) })
                showDownloadFail(uri)
            }
            Download.STATE_REMOVING -> {
                downloadedMovieCache.put(downloadedMovieCache.get().apply { removeMovie(uri) })
//                showDownloadFail(uri)
            }
            Download.STATE_STOPPED -> {
                downloadedMovieCache.put(downloadedMovieCache.get().apply { removeMovie(uri) })
//                showDownloadFail(uri)
            }
        }
    }

    private fun showDownloadFail(uri: Uri) {
        DialogAlert()
            .setTitle("Download Video")
            .setMessage("Your video $uri is download failed")
            .setCancel(false)
            .setTitlePositive("OK")
            .show(activity)
    }

    override fun onProgressUpdated(p0: Long, p1: Long) {
        AppLog.e("Duy", "onProgressUpdated : p0:$p0 , p1:$p1")
        if (p0 > 1000) startPosition = p0
        if (p1 > 0 && p1 - p0 < 3000 && p1 - p0 > 2000) {
            Handler().postDelayed({
                castSession?.remoteMediaClient?.removeProgressListener(this)
                castContext?.sessionManager?.endCurrentSession(true)
            }, 3000)
        }
    }

    var noInternetDialog: DialogAlert? = null

    var networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                    noInternetDialog?.dismissAllowingStateLoss()
                    if (activity != null && activity is BaseActivity) {
                        (activity as BaseActivity).isShowNoInternet = false
                    }
                    isOpenSetting = false
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            override fun onLost(network: Network) {
                if (activity != null && activity is BaseActivity) {
                    showNetworkDialog()
                }
            }
        }

    private fun showNetworkDialog() {
        try {
            if (isOfflineMode) return
            if (isFullScreen) handleFullScreen()
            if (!(activity as BaseActivity).isShowNoInternet) {
                noInternetDialog?.dismiss()
                activity?.let {
                    try {
                        noInternetDialog = NoInternetDialog().apply {
                            isRetry = false
                            onDismiss {
                                (activity as BaseActivity).isShowNoInternet = false
                            }
                        }
                            .onPositive { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
                        noInternetDialog!!.show(activity = activity!!)
                        isOpenSetting = true
                        (activity as BaseActivity).isShowNoInternet = true
                        AppLog.e("Duy", "onLost MovieDetail")
                    } catch (e: java.lang.Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}