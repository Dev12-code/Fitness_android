package com.cbi.app.trs.features.fragments.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.cache.AchievementBadgeCache
import com.cbi.app.trs.data.cache.DownloadedMovieCache
import com.cbi.app.trs.data.cache.VideoScoreCache
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.eventbus.GetProfileEvent
import com.cbi.app.trs.domain.eventbus.UpdateProfileEvent
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.dialog.EditProfileDialog
import com.cbi.app.trs.features.fragments.mobility.MobilityFragment
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.utils.CommonUtils.getMimeType
import com.cbi.app.trs.features.viewmodel.UserProfileViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.cast.framework.CastContext
import kotlinx.android.synthetic.main.fragment_setting_top.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import javax.inject.Inject


class SettingTopFragment : LightBaseFragment() {
    override fun layoutId() = R.layout.fragment_setting_top

    private var userProfile: UserData.UserProfile? = null

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var downloadMovieCache: DownloadedMovieCache

    @Inject
    lateinit var videoScoreCache: VideoScoreCache

    @Inject
    lateinit var achievementBadgeCache: AchievementBadgeCache

    lateinit var userProfileViewModel: UserProfileViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
//        if (userProfile == null) {
        showProgress()
        userProfileViewModel.getUserProfile(userID)
//        } else {
//            loadProfileInfo()
//        }
    }

    override fun onReloadData() {
        showProgress()
        userProfileViewModel.getUserProfile(userID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        userProfileViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(userProfile, ::onReceiveUserProfile)
            observe(updateUserProfile, ::onUpdateUserProfileSuccess)
        }
    }

    private fun onUpdateUserProfileSuccess(baseEntities: BaseEntities?) {
        hideProgress()
        if (baseEntities == null) {
            CommonUtils.showError(activity, "Edit profile", "Update failed")
            return
        }
        if (baseEntities.isSuccess) {
            CommonUtils.showError(activity, "Edit profile", "Update Successful")
            userProfileViewModel.getUserProfile(userID)
        } else {
            CommonUtils.showError(activity, "Edit profile", "Update failed")
        }
    }

    private fun onReceiveUserProfile(userProfile: UserData.UserProfile?) {
        hideProgress()
        if (userProfile == null) return
        this.userProfile = userProfile
        loadProfileInfo()
    }

    private fun loadProfileInfo() {
        if (userProfile == null) return
        userProfile!!.user_avatar?.let { setting_avatar.loadFromUrl(it) }
        setting_user_name.text = "${userProfile?.first_name} ${userProfile?.last_name}"
//        setting_leaderboard_position.text = "${userProfile?.user_rank?.user_rank_index}"
        user_plan.text = "${userProfile?.user_plan}"
    }

    private fun initView() {
        setting_close.setOnClickListener { pop(activity) }
        favorite_notification.setOnClickListener { if (isAllowForFreemium()) navigator.showFavourite(activity) }
        setting_detail.setOnClickListener { navigator.showUserSetting(activity) }
        setting_notification.setOnClickListener { navigator.showNotification(activity) }
        setting_logout.setOnClickListener {
            DialogAlert()
                    .setTitle(getString(R.string.confirm_logout))
                    .setMessage(getString(R.string.confirm_logout_content))
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .setTitleNegative("Cancel")
                    .onPositive {
                        CastContext.getSharedInstance(requireActivity())?.sessionManager?.endCurrentSession(true)
                        userDataCache.clear()
                        achievementBadgeCache.clear()
//                        downloadMovieCache.clear()
                        sharedPreferences.edit().putLong(AppConstants.WATCH_LATER_KELLY_RECOMMEND, 0).apply()
                        sharedPreferences.edit().putLong(AppConstants.WATCH_LATER_INTRO, 0).apply()
                        sharedPreferences.edit().putBoolean(MobilityFragment.ALREADY_TEST, false).apply()
                        videoScoreCache.clear()
                        navigator.showLogin(activity)
                        activity?.finish()
                    }
                    .show(requireContext())
        }
        setting_subscription.setOnClickListener { navigator.showSubscription(activity, userDataCache) }
        setting_contact.setOnClickListener { navigator.showContactUs(activity) }
        setting_download.setOnClickListener { if (isAllowForFreemium()) navigator.showDownloaded(activity) }
        setting_help.setOnClickListener { navigator.showUpdateNewPassword(activity) }
        setting_privacy.setOnClickListener { navigator.showPolicy(activity) }
        setting_edit_avatar.setOnClickListener {
            ImagePicker.with(this)
//                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .galleryMimeTypes(  //Exclude gif images
                            mimeTypes = arrayOf(
                                    "image/png",
                                    "image/jpg",
                                    "image/jpeg"
                            )
                    )
                    .compress(3096)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(2048, 2048)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
        }
        search_edit_profile.setOnClickListener { EditProfileDialog().show(activity) }
        setting_avatar.extendTouch()
        setting_close.extendTouch()
        setting_edit_avatar.extendTouch()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
//            setting_avatar.setImageURI(fileUri)

            //You can get File object from intent
            val file: File? = ImagePicker.getFile(data)

            //You can also get File Path from intent
            val filePath: String? = ImagePicker.getFilePath(data)
            try {
                val fileRequest = RequestBody.create(getMimeType(filePath)?.toMediaTypeOrNull(), file!!)
                val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, fileRequest)

                showProgress()
                userProfileViewModel.updateUserAvatar(Pair(userID, fileToUpload))
            } catch (e: NullPointerException) {

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    @Subscribe
    fun onUpdateProfileEvent(event: UpdateProfileEvent) {
        showProgress()
        userProfileViewModel.updateUserProfile(Pair(userID, event.data))
    }

    @Subscribe
    fun onReloadProfileEvent(event: GetProfileEvent) {
        Handler().postDelayed({
            onReloadData()
        }, 500)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
}