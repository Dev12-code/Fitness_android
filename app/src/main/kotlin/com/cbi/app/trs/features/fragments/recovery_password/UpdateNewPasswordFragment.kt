package com.cbi.app.trs.features.fragments.recovery_password

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.cache.AchievementBadgeCache
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.mobility.MobilityFragment
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.AuthenticateViewModel
import com.google.android.gms.cast.framework.CastContext
import kotlinx.android.synthetic.main.fragment_recovery_pass.back_btn
import kotlinx.android.synthetic.main.fragment_update_new_password.*
import javax.inject.Inject

class UpdateNewPasswordFragment : LightBaseFragment() {

    private lateinit var authenticateViewModel: AuthenticateViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var achievementBadgeCache: AchievementBadgeCache

    override fun layoutId(): Int {
        return R.layout.fragment_update_new_password
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        authenticateViewModel = viewModel(viewModelFactory) {
            observe(updateNewPasswordData, ::onUpdateNewPasswordSuccessfully)
            observe(failureData, ::handleFailure)
        }
    }

    private fun onUpdateNewPasswordSuccessfully(data: BaseEntities?) {
        hideProgress()
        DialogAlert().setTitle(getString(R.string.change_password))
                .setMessage(getString(R.string.change_password_successfully))
                .setTitlePositive(getString(R.string.ok))
                .onPositive {
                    CastContext.getSharedInstance(requireActivity())?.sessionManager?.endCurrentSession(true)
                    userDataCache.clear()
                    achievementBadgeCache.clear()
                    sharedPreferences.edit().putLong(AppConstants.WATCH_LATER_KELLY_RECOMMEND, 0).apply()
                    sharedPreferences.edit().putLong(AppConstants.WATCH_LATER_INTRO, 0).apply()
                    sharedPreferences.edit().putBoolean(MobilityFragment.ALREADY_TEST, false).apply()
                    mNavigator.showLogin(activity)
                }
                .show(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        back_btn.setOnClickListener {
            close()
        }

        change_pass_btn.setOnClickListener {
            val currentPassword = et_current_password.text.toString()
            val newPassword = et_new_password.text.toString()
            val confirmPassword = et_confirm_password.text.toString()

            if (TextUtils.isEmpty(currentPassword)) {
                CommonUtils.showError(context, getString(R.string.change_password), "Please enter your current password")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(newPassword)) {
                CommonUtils.showError(context, getString(R.string.change_password), "Please enter your new password")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                CommonUtils.showError(context, getString(R.string.change_password), "Please enter your confirm password")
                return@setOnClickListener
            }

            showProgress()
            authenticateViewModel.updateNewPassword(userDataCache.get()?.user_token?.userID, currentPassword, newPassword, confirmPassword)

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

}