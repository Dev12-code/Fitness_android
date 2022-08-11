package com.cbi.app.trs.features.fragments.mobility.test

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.cache.VideoScoreCache
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.MobilityTestVideoData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.VideoScore
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilityTestVideoEntity
import com.cbi.app.trs.domain.eventbus.AnswerEvent
import com.cbi.app.trs.domain.eventbus.OnExitTheTestEvent
import com.cbi.app.trs.domain.usecases.mobility.UpdateMobilityResult
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.movies.MoviePlayFragment
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.MobilityViewModel
import kotlinx.android.synthetic.main.compensation_view.view.*
import kotlinx.android.synthetic.main.instructions_view.view.*
import kotlinx.android.synthetic.main.mobility_test_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class MobilityTestFragment : BaseFragment() {
    override fun layoutId() = R.layout.mobility_test_fragment

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var mobilityViewModel: MobilityViewModel

    @Inject
    lateinit var sharePreferences: SharedPreferences

    @Inject
    lateinit var videoScoreCache: VideoScoreCache

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    var testVideoData: ArrayList<MobilityTestVideoData> = ArrayList()
    var currentVideo: MobilityTestVideoData? = null

    var currentPosition: Int = 0

    var mobilityStatus: MobilityStatus? = null

    var isExitTheTest: Boolean = false

    var isRetest: Boolean = false

    var dropIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        arguments?.let {
            mobilityStatus = it.getParcelable(MoviePlayFragment.MOBILITY_STATUS)
            isRetest = it.getBoolean(MoviePlayFragment.MOBILITY_RETEST)
        }

        mobilityViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(mobilityTestVideo, ::onReceiveTestVideo)
            observe(updateResult, ::onReceiveUpdateScore)
        }
    }

    private fun onReceiveUpdateScore(baseEntities: BaseEntities?) {
        if (isRetest) {
            isRetest = false
            return
        }
        //if user exit the test, back to mobility list
        if (isExitTheTest) {
            (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
            return
        }
        sharePreferences.edit().putInt(AppConstants.SAVED_POSITION, -1).apply()
        videoScoreCache.clear()
        hideProgress()
        if (baseEntities == null) return
        if (baseEntities.isSuccess) {
            val watchLaterKellyRecommend =
                sharedPreferences.getLong(AppConstants.WATCH_LATER_KELLY_RECOMMEND, 0)
            //change from 3h to 1d
            if (watchLaterKellyRecommend == 0L || System.currentTimeMillis() - watchLaterKellyRecommend > 24 * 60 * 60 * 1000) {
                navigator.showKellyRecommendationFromMobilityTestComplete(activity)
            } else {
                (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
            }
        } else {
            CommonUtils.showError(activity, "Update Score", "Fail : ${baseEntities.message}")
        }
    }

    private fun onReceiveTestVideo(data: MobilityTestVideoEntity.Data?) {
        hideProgress()
        if (data == null) return

        setVideoType(data.trunk_videos, 1)
        testVideoData.addAll(data.trunk_videos)

        setVideoType(data.shoulder_videos, 0)
        testVideoData.addAll(data.shoulder_videos)

        setVideoType(data.hip_videos, 2)
        testVideoData.addAll(data.hip_videos)

        setVideoType(data.ankle_videos, 3)
        testVideoData.addAll(data.ankle_videos)

        //get saved last position
        val savedPosition = sharePreferences.getInt(AppConstants.SAVED_POSITION, -1)
        if (savedPosition != -1 && mobilityStatus?.on_process == true) {
            //get group of position
            if (savedPosition + 1 in 1..3) {
                //trunk
                dropIndex = 0
            } else if (savedPosition + 1 in 4..8) {
                //shoulder
                dropIndex = 3
            } else if (savedPosition + 1 in 9..14) {
                //hip
                dropIndex = 8
            } else {
                //ankle
                dropIndex = 14
            }

            //drop list
            val dropList = testVideoData.drop(dropIndex) as ArrayList<MobilityTestVideoData>
            testVideoData.clear()
            testVideoData.addAll(dropList)
//            //update test score
//            for (item in testVideoData) {
//                for (videoScore in videoScoreCache.get()?.videos!!) {
//                    if (item.video_id == videoScore.videoId) {
//                        item.score = videoScore.videoScore
//                    }
//                }
//            }
//
//            //reset score from first position
//            for (i in firstPosition..testVideoData.size - 1) {
//                testVideoData[i].score = 0
//            }

            //load from first
            jumpToVideo(0)

        } else {
            dropIndex = 0
            loadNextTestVideo()
        }
        loadTestDetail(currentVideo)
    }

    override fun onReloadData() {
        testVideoData.clear()
        currentVideo = null
        showProgress()
        mobilityViewModel.getMobilityTestVideo(userID)
    }

    private fun setVideoType(list: List<MobilityTestVideoData>, type: Int) {
        for (item in list) {
            item.type = type
        }
    }

    private fun jumpToVideo(position: Int) {
        if (testVideoData.isEmpty()) return
        if (position != 0) {
            currentVideo = testVideoData.get(position)
            currentPosition = position
        }

        if (currentVideo == null) {
            currentVideo = testVideoData[0]
            currentPosition = 0
        } else {
            val position = testVideoData.indexOf(currentVideo!!)
            if (position == testVideoData.size - 1) {
                mobility_test_btn.setOnClickListener { }
                calculateScore(false)
            }
        }
        loadTestDetail(currentVideo)
        (parentFragment as MoviePlayFragment).playMovie(
            MovieData(
                currentVideo!!.video_id,
                currentVideo!!.image_thumbnail,
                currentVideo!!.video_title,
                "",
                emptyList(),
                emptyList(),
                emptyList(),
                currentVideo!!.video_play_url,
                currentVideo!!.view_count,
                currentVideo!!.video_duration,
                currentVideo!!.video_is_favorite
            )
        )
        //update header text
        updateHeader()
    }

    private fun loadNextTestVideo() {
        if (testVideoData.isEmpty()) return
        if (currentVideo == null) {
            currentVideo = testVideoData[0]
            currentPosition = 0
        } else {
            val position = testVideoData.indexOf(currentVideo!!)
            if (position == testVideoData.size - 1) {
                mobility_test_btn.setOnClickListener { }
                calculateScore(false)
            } else {
                currentVideo = testVideoData[position + 1]
                currentPosition++
            }
        }
        loadTestDetail(currentVideo)
        (parentFragment as MoviePlayFragment).playMovie(
            MovieData(
                currentVideo!!.video_id,
                currentVideo!!.image_thumbnail,
                currentVideo!!.video_title,
                "",
                emptyList(),
                emptyList(),
                emptyList(),
                currentVideo!!.video_play_url,
                currentVideo!!.view_count,
                currentVideo!!.video_duration,
                currentVideo!!.video_is_favorite
            )
        )
        //update header text
        updateHeader()
    }

    fun loadPreTestVideo() {
        if (testVideoData.isEmpty()) return
        currentPosition -= 1
        currentVideo = testVideoData[currentPosition]
        loadTestDetail(currentVideo)
        (parentFragment as MoviePlayFragment).playMovie(
            MovieData(
                currentVideo!!.video_id,
                currentVideo!!.image_thumbnail,
                currentVideo!!.video_title,
                "",
                emptyList(),
                emptyList(),
                emptyList(),
                currentVideo!!.video_play_url,
                currentVideo!!.view_count,
                currentVideo!!.video_duration,
                currentVideo!!.video_is_favorite
            )
        )
        //update header text
        updateHeader()

        navigator.showMobilityTestList(
            activity,
            currentVideo,
            String.format("%d Out Of %d", currentPosition + 1, testVideoData.size),
            currentVideo?.score
        )
    }

    private fun isAnswerAllTrunk(): Boolean {
        if (testVideoData.isEmpty()) return false

        for (video in testVideoData) {
            when (video.type) {
                1 -> {
                    if (video.score == 0) {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun calculateScore(onProcess: Boolean) {
        if (testVideoData.isEmpty()) return

        var shoulderScore: Double = 0.0
        var trunkScore: Double = 0.0
        var hipScore: Double = 0.0
        var ankleScore: Double = 0.0

        var shoulderTotal: Int = 0
        var trunkTotal: Int = 0
        var hipTotal: Int = 0
        var ankleTotal: Int = 0

        var isCompletedShoulder: Boolean = true
        var isCompletedTrunk: Boolean = true
        var isCompletedHip: Boolean = true
        var isCompletedAnkle: Boolean = true

        for (video in testVideoData) {
            when (video.type) {
                0 -> {
                    shoulderScore += video.score
                    shoulderTotal += 3

                    if (video.score == 0) {
                        isCompletedShoulder = false
                    }
                }
                1 -> {
                    trunkScore += video.score
                    trunkTotal += 3

                    if (video.score == 0) {
                        isCompletedTrunk = false
                    }
                }
                2 -> {
                    hipScore += video.score
                    hipTotal += 3

                    if (video.score == 0) {
                        isCompletedHip = false
                    }
                }
                3 -> {
                    ankleScore += video.score
                    ankleTotal += 3

                    if (video.score == 0) {
                        isCompletedAnkle = false
                    }
                }
            }
        }

        //if havent complete all test, reset score = 0
        if (!isCompletedShoulder) {
            shoulderTotal = 0
        }

        if (!isCompletedTrunk) {
            trunkTotal = 0
        }

        if (!isCompletedHip) {
            hipTotal = 0
        }

        if (!isCompletedAnkle) {
            ankleTotal = 0
        }

        var shoulderScoreAvg: Double =
            if (shoulderTotal == 0) 0.0 else shoulderScore / shoulderTotal
        var trunkScoreAvg: Double = if (trunkTotal == 0) 0.0 else trunkScore / trunkTotal
        var hipScoreAvg: Double = if (hipTotal == 0) 0.0 else hipScore / hipTotal
        var ankleScoreAvg: Double = if (ankleTotal == 0) 0.0 else ankleScore / ankleTotal

        mobilityStatus?.let {
            if (shoulderScoreAvg == 0.0 && it.shoulder_point_avg >= 0.0) {
                shoulderScoreAvg = it.shoulder_point_avg
            }

            if (trunkScoreAvg == 0.0 && it.trunk_point_avg >= 0.0) {
                trunkScoreAvg = it.trunk_point_avg
            }

            if (hipScoreAvg == 0.0 && it.hip_point_avg >= 0.0) {
                hipScoreAvg = it.hip_point_avg
            }

            if (ankleScoreAvg == 0.0 && it.ankle_point_avg >= 0.0) {
                ankleScoreAvg = it.ankle_point_avg
            }
        }

        showProgress()
        mobilityViewModel.updateMobilityResult(
            userID,
            UpdateMobilityResult.Param(
                shoulderScoreAvg,
                trunkScoreAvg,
                hipScoreAvg,
                ankleScoreAvg,
                onProcess
            )
        )

        //calculate saved position
        if (trunkScoreAvg == 0.0) {
            sharePreferences.edit().putInt(AppConstants.SAVED_POSITION, 0).apply()
            return
        }

        if (shoulderScoreAvg == 0.0) {
            sharePreferences.edit().putInt(AppConstants.SAVED_POSITION, 3).apply()
            return
        }

        if (hipScoreAvg == 0.0) {
            sharePreferences.edit().putInt(AppConstants.SAVED_POSITION, 8).apply()
            return
        }

        if (ankleScoreAvg == 0.0) {
            sharePreferences.edit().putInt(AppConstants.SAVED_POSITION, 14).apply()
            return
        }
    }

    private fun loadTestDetail(currentVideo: MobilityTestVideoData?) {
        if (currentVideo == null) return

        test_name.text = currentVideo.video_title

        instruction_area.removeAllViews()
        for (i in currentVideo.video_instruction) {
            val view = layoutInflater.inflate(R.layout.instructions_view, null)
            view.instruction_title.text = i
            instruction_area.addView(view)
        }

        compensation_area.removeAllViews()
        for (i in currentVideo.video_compensations) {
            val view = layoutInflater.inflate(R.layout.compensation_view, null)
            view.compensation_title.text = i
            compensation_area.addView(view)
        }

        updateHeader()
    }

    private fun updateHeader() {
        //update header text
        (parentFragment as MoviePlayFragment).updateHeaderText(
            String.format(
                "%d Out Of %d",
                currentPosition + 1,
                testVideoData.size
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (testVideoData.isEmpty()) {
            showProgress()
            mobilityViewModel.getMobilityTestVideo(userID)
        } else {
            loadTestDetail(currentVideo)
        }
        mobility_test_btn.setOnClickListener {
            navigator.showMobilityTestList(
                activity,
                currentVideo,
                String.format("%d Out Of %d", currentPosition + 1, testVideoData.size),
                null
            )
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onAnswerEvent(event: AnswerEvent) {
        currentVideo?.score = event.score
        currentVideo?.let {
            videoScoreCache.put(VideoScore(it.video_id, event.score))
        }
        loadNextTestVideo()

        //in case retest, if user choose full score trunk, will reset score
        if (isRetest && isAnswerAllTrunk()) {
            mobilityViewModel.updateMobilityResult(
                userID,
                UpdateMobilityResult.Param(0.0, 0.0, 0.0, 0.0, true)
            )
        }
    }

    @Subscribe
    fun onExitTheTestEvent(event: OnExitTheTestEvent) {
        isExitTheTest = true
        if (isRetest) {
            isRetest = false
            //in case retest, keep score
            (activity as MainActivity).selectNavigation(R.id.navigation_mobility)
            return
        }
        calculateScore(true)
        EventBus.getDefault().removeStickyEvent(OnExitTheTestEvent::class.java)
    }
}