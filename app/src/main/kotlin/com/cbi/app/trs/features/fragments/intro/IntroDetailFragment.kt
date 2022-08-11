package com.cbi.app.trs.features.fragments.intro

import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.features.utils.CountdownTimer
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
import kotlinx.android.synthetic.main.fragment_intro_detail.*
import javax.inject.Inject
import kotlin.math.max

class IntroDetailFragment : LightBaseFragment(), PlaybackPreparer {
    override fun layoutId() = R.layout.fragment_intro_detail

    var movieData: MovieData? = null
    private var uri: Uri? = null
    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var startWindow = 0
    private var startPosition: Long = 0
    private var startAutoPlay = true
    var position = 0
    var sizeList = 0

    private var countdownTimer: CountdownTimer? = null

    @Inject
    lateinit var renderersFactory: RenderersFactory

    @Inject
    lateinit var dataSourceFactory: DataSource.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (movieData !== null) {
            uri = Uri.parse(movieData!!.video_play_url)
            initializePlayer()
        }
    }

    private fun initView() {
        player_view.setErrorMessageProvider(PlayerErrorMessageProvider())
        player_view.requestFocus()
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
            return createLeafMediaSource(
                uri!!,
                DrmSessionManager.getDummyDrmSessionManager<ExoMediaCrypto>()
            )
        }
        return null
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

    override fun onDetach() {
        super.onDetach()
        releasePlayer()
        countdownTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
//        if (Util.SDK_INT <= 23 || player == null) {
        initializePlayer()
        if (player_view != null) {
            player_view.onResume()
        }
        player?.playWhenReady = true
//        }
    }

    override fun onPause() {
        super.onPause()
//        if (Util.SDK_INT <= 23) {
        if (player_view != null) {
            player_view.onPause()
        }
        updateStartPosition()
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
            var errorString = try {
                getString(R.string.error_generic)
            } catch (e: Exception) {
                "" + e.message
            }
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

    inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            @Player.State playbackState: Int
        ) {
            //prevent screen light off when watching video
            if (player_view != null) {
                player_view.keepScreenOn =
                    !(playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED || !playWhenReady)
            }
            when (playbackState) {
                Player.STATE_READY -> {
                    movieCover.visibility = View.GONE

                    //start timer if needed
                    if (countdownTimer == null && player != null) {
                        countdownTimer = object : CountdownTimer(player!!.contentDuration, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                //reduce audio
                                val percentRemain =
                                    millisUntilFinished.toDouble() / player!!.contentDuration.toDouble()
                                player?.let {
                                    it.volume = percentRemain.toFloat()
                                }
                            }

                            override fun onFinish() {

                            }

                        }
                    }
                    (countdownTimer as CountdownTimer).start()
                }
                Player.STATE_BUFFERING -> {
                    movieCover.visibility = View.VISIBLE
                    if (player != null && player!!.contentPosition > 0) {
                        movieCover_image.visibility = View.INVISIBLE
                    }
                }
                Player.STATE_ENDED -> {
                    if (position == sizeList - 1) {
                        if (parentFragment is IntroFragment) {
                            (parentFragment as IntroFragment).close()
                        }
                    }
                    (countdownTimer as CountdownTimer).cancel()
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

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
        }
    }
}