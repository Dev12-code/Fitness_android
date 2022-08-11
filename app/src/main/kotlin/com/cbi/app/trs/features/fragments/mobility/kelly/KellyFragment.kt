package com.cbi.app.trs.features.fragments.mobility.kelly

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.viewmodel.AuthenticateViewModel
import com.cbi.app.trs.features.viewmodel.MobilityViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.ExoMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_kelly.*
import javax.inject.Inject
import kotlin.math.max

class KellyFragment : LightBaseFragment(), PlaybackPreparer {
    override fun layoutId() = R.layout.fragment_kelly

    private var movieData: MovieData? = null
    private var uri: Uri? = null
    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var startWindow = 0
    private var startPosition: Long = 0
    private var startAutoPlay = true

    companion object {

        const val TYPE = "TYPE"

        const val INTRO_TYPE = "INTRO_TYPE"

        const val INTRO_FROM_TEST_COMPLETED = "INTRO_FROM_TEST_COMPLETED"

        const val SIGN_UP_INTRO_TYPE = "SIGN_UP_INTRO_TYPE"
    }

    @Inject
    lateinit var renderersFactory: RenderersFactory

    @Inject
    lateinit var dataSourceFactory: DataSource.Factory

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var mobilityViewModel: MobilityViewModel

    lateinit var authenticateViewModel: AuthenticateViewModel

    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        mobilityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(mobilityKellyVideo, ::onReceiveKellyVideo)
            observe(mobilityIntroVideo, ::onReceiveMobilityIntroVideo)
        }

        authenticateViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(signUpIntroVideo, ::handleSignUpIntroVideo)
        }

        arguments?.let {
            type = it.getString(TYPE, "")
        }
    }

    private fun onReceiveKellyVideo(movieData: MovieData?) {
        forceHide()
        if (movieData == null) return
        this.movieData = movieData
        uri = Uri.parse(movieData.video_play_url)
        initializePlayer()
        loadMovieInformation()
    }

    private fun handleSignUpIntroVideo(movieData: MovieData?) {
        //play sign up intro video
        forceHide()
        if (movieData == null) return
        this.movieData = movieData
        uri = Uri.parse(movieData.video_play_url)
        initializePlayer()
        loadMovieInformation()
    }

    private fun onReceiveMobilityIntroVideo(movieData: MovieData?) {
        forceHide()
        if (movieData == null) return
        this.movieData = movieData
        uri = Uri.parse(movieData.video_play_url)
        initializePlayer()
        loadMovieInformation()
    }


    private fun loadMovieInformation() {
//        video_title.text = movieData?.video_title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (movieData == null) {
            when (type) {
                INTRO_TYPE -> {
                    mobilityViewModel.getMobilityIntroVideo(userID)
                }
                SIGN_UP_INTRO_TYPE -> {
                    //call API to get sign up intro video
                    authenticateViewModel.getIntroVideo(userID)
                }
                INTRO_FROM_TEST_COMPLETED -> {
                    mobilityViewModel.getMobilityKellyVideo(userID)
                }
            }
        }
    }

    override fun onReloadData() {
        clearStartPosition()
        when (type) {
            INTRO_TYPE -> {
                mobilityViewModel.getMobilityIntroVideo(userID)
            }
            SIGN_UP_INTRO_TYPE -> {
                //call API to get sign up intro video
                authenticateViewModel.getIntroVideo(userID)
            }
            INTRO_FROM_TEST_COMPLETED -> {
                mobilityViewModel.getMobilityKellyVideo(userID)
            }
        }
    }

    private fun initView() {
        player_view.setErrorMessageProvider(PlayerErrorMessageProvider())
        player_view.requestFocus()

        skip_text.paintFlags = skip_text.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        skip_btn.setOnClickListener {
            when (type) {
                INTRO_TYPE -> {
                    navigator.showMobilityTest(activity, null, false)
                }
                SIGN_UP_INTRO_TYPE -> {
                    navigator.showQuiz(activity)
                }
                INTRO_FROM_TEST_COMPLETED -> {
                    if (activity is MainActivity) {
                        (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
                    }
                }
            }
            close()

        }
        watch_later.setOnClickListener {
            handleDone()
        }
    }

    private fun handleDone() {
        when (type) {
            INTRO_TYPE -> {
                sharedPreferences.edit().putLong(AppConstants.WATCH_LATER_INTRO, System.currentTimeMillis()).apply()
                navigator.showMobilityTest(activity, null, false)
            }
            SIGN_UP_INTRO_TYPE -> {
                //show quiz
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
                navigator.showQuiz(activity)
            }
            INTRO_FROM_TEST_COMPLETED -> {
                sharedPreferences.edit().putLong(AppConstants.WATCH_LATER_KELLY_RECOMMEND, System.currentTimeMillis()).apply()
                if (activity is MainActivity) {
                    (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
                }
            }
        }
        close()
    }

    private fun initializePlayer() {
        if (player == null) {
            mediaSource = createTopLevelMediaSource()
            if (mediaSource == null) {
                return
            }
            player = activity?.let {
                SimpleExoPlayer.Builder(it, renderersFactory)
                        .build()
            }
            player?.addListener(PlayerEventListener())
            player?.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
            player?.playWhenReady = true
            player_view.player = player
            player_view.setPlaybackPreparer(this)
        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(startWindow, startPosition)
        }
        player?.prepare(mediaSource!!, !haveStartPosition, false)
        player_view.hideController()
    }

    private fun createTopLevelMediaSource(): MediaSource? {
        uri?.let {
            return createLeafMediaSource(uri!!, DrmSessionManager.getDummyDrmSessionManager<ExoMediaCrypto>())
        }
        return null
    }

    private fun createLeafMediaSource(
            uri: Uri, drmSessionManager: DrmSessionManager<*>): MediaSource? {
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


    override fun preparePlayback() {
        player?.retry()
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

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = max(0, player!!.contentPosition)
        }
    }

    override fun onStart() {
        super.onStart()
//        if (Util.SDK_INT > 23) {
//            initializePlayer()
//            if (player_view != null) {
//                player_view.onResume()
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).navigationView.visibility = View.GONE
        }
//        if (Util.SDK_INT <= 23 || player == null) {
        initializePlayer()
        if (player_view != null) {
            player_view.onResume()
        }
//        }
    }

    override fun onPause() {
        super.onPause()
        if (activity is MainActivity) {
            (activity as MainActivity).navigationView.visibility = View.VISIBLE
        }
//        if (Util.SDK_INT <= 23) {
        if (player_view != null) {
            player_view.onPause()
        }
        releasePlayer()
//        }
    }

    private fun releasePlayer() {
        if (player != null) {
            updateStartPosition()
            player!!.release()
            player = null
            mediaSource = null
        }
    }

    inner class PlayerErrorMessageProvider : ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString: String = getString(R.string.error_generic)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    errorString = if (cause.codecInfo == null) {
                        when {
                            cause.cause is MediaCodecUtil.DecoderQueryException -> {
                                getString(R.string.error_querying_decoders)
                            }
                            cause.secureDecoderRequired -> {
                                getString(
                                        R.string.error_no_secure_decoder, cause.mimeType)
                            }
                            else -> {
                                getString(R.string.error_no_decoder, cause.mimeType)
                            }
                        }
                    } else {
                        getString(
                                R.string.error_instantiating_decoder,
                                cause.codecInfo!!.name)
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, @Player.State playbackState: Int) {
            //prevent screen light off when watching video
            player_view.keepScreenOn = !(playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED || !playWhenReady)

            when (playbackState) {
                Player.STATE_READY -> {
                    movieCover.visibility = View.GONE
                }
                Player.STATE_BUFFERING -> {
                    movieCover.visibility = View.VISIBLE
                    if (player != null && player!!.contentPosition > 0) {
                        movieCover_image.visibility = View.INVISIBLE
                    }
                }

                Player.STATE_ENDED -> {
                    handleDone()
                }
                else -> {
                    movieCover.visibility = View.GONE
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

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
        }
    }
}