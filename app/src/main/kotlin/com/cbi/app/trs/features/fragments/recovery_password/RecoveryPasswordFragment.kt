package com.cbi.app.trs.features.fragments.recovery_password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.isValidPassword
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.AuthenticateViewModel
import kotlinx.android.synthetic.main.fragment_recovery_pass.*
import javax.inject.Inject

class RecoveryPasswordFragment() : LightBaseFragment() {
    var state = RecoveryState.STATE_EMAIL

    private lateinit var authenticateViewModel: AuthenticateViewModel

    @Inject
    lateinit var navigator: Navigator

    override fun layoutId(): Int {
        return R.layout.fragment_recovery_pass
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        authenticateViewModel = viewModel(viewModelFactory) {
            observe(otpData, ::onReceiveOTP)
            observe(changePasswordData, ::onReceiveChangePassword)
            failure(failureData, ::handleFailure)
        }
    }

    private fun onReceiveOTP(otp: BaseEntities?) {
        hideProgress()
        if (otp == null) return
        if (otp.isSuccess) {
            toggleNextState()
        }
    }

    private fun onReceiveChangePassword(changePass: BaseEntities?) {
        hideProgress()
        if (changePass == null) return
        if (changePass.isSuccess) {
            DialogAlert()
                    .setTitle("Password Changed!")
                    .setMessage("Password changed successfully.")
                    .setCancel(false)
                    .setTitlePositive("OK")
//                    .setTitleNegative("Cancel")
                    .onPositive {
                        navigator.showLogin(activity)
                        activity?.finish()
                    }
                    .show(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        recovery_email_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                recovery_email_edt.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        recovery_otp_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                recovery_otp_edt.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        recovery_new_password_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                recovery_new_password_edt.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        recovery_new_password_confirm_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                recovery_new_password_confirm_edt.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        recovery_btn.setOnClickListener {
            when (state) {
                RecoveryState.STATE_EMAIL -> {
                    if (recovery_email_edt.text.toString().trim().isEmpty()) {
                        CommonUtils.showError(context, "Failed Password Reset", "Please enter your email address.")
                        return@setOnClickListener
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(recovery_email_edt.text).matches()) {
                        CommonUtils.showError(context, "Failed Password Reset", "Invalid email. Please enter correct email.")
                        return@setOnClickListener
                    } else {
                        recovery_email_edt.error = null
                    }
                    showProgress()
                    authenticateViewModel.getOTP(recovery_email_edt.text.toString())
                }
                RecoveryState.STATE_NEW_PASSWORD -> {
                    recovery_new_password_edt.error = null
                    if (recovery_new_password_edt.text.toString().trim().isEmpty()) {
                        CommonUtils.showError(context, "Failed Password Reset", "Please enter your password")
                        return@setOnClickListener
                    }

                    if (recovery_new_password_confirm_edt.text.toString().trim().isEmpty()) {
                        CommonUtils.showError(context, "Failed Password Reset", "Please confirm your password")
                        return@setOnClickListener
                    }

                    if (recovery_new_password_edt.text.toString() != recovery_new_password_confirm_edt.text.toString()) {
                        CommonUtils.showError(context, "Failed Password Reset", "Passwords do not match. Please check again.")
                        return@setOnClickListener
                    } else {
                        recovery_new_password_confirm_edt.error = null
                    }

                    showProgress()
                    authenticateViewModel.changePassword(recovery_new_password_edt.text.toString(), recovery_otp_edt.text.toString(), recovery_email_edt.text.toString())
                }
                else -> {
                    if (recovery_otp_edt.text.isNullOrBlank()) {
                        CommonUtils.showError(context, "Failed Password Reset", "Please enter OTP (One Time Password) sent to your email.")
                        return@setOnClickListener
                    } else {
                        recovery_otp_edt.error = null
                    }
                    toggleNextState()
                }
            }
        }
        back_btn.setOnClickListener {
            togglePreviousState()
        }
    }

    override fun onBackPressed(): Boolean {
        togglePreviousState()
        return true
    }

    private fun togglePreviousState() {
        when (state) {
            RecoveryState.STATE_EMAIL -> {
                pop(activity)
            }
            RecoveryState.STATE_OTP -> {
                state = RecoveryState.STATE_EMAIL
                recovery_email.visibility = View.VISIBLE
                recovery_otp.visibility = View.GONE
                recovery_new_password.visibility = View.GONE
                recovery_new_password_confirm.visibility = View.GONE
                recovery_title.text = "Recover Password"
                recovery_message.text = "Please enter your registered email to reset your password."
                recovery_btn.text = getString(R.string.btn_next)
            }
            RecoveryState.STATE_NEW_PASSWORD -> {
                state = RecoveryState.STATE_OTP
                recovery_email.visibility = View.GONE
                recovery_otp.visibility = View.VISIBLE
                recovery_new_password.visibility = View.GONE
                recovery_new_password_confirm.visibility = View.GONE
                recovery_title.text = "Recover Password"
                recovery_message.text = "We have sent a temporary passcode to your registered email address."
                recovery_btn.text = getString(R.string.btn_next)
            }
        }
    }

    private fun toggleNextState() {
        when (state) {
            RecoveryState.STATE_EMAIL -> {
                state = RecoveryState.STATE_OTP
                recovery_email.visibility = View.GONE
                recovery_otp.visibility = View.VISIBLE
                recovery_new_password.visibility = View.GONE
                recovery_new_password_confirm.visibility = View.GONE
                recovery_title.text = "Recover Password"
                recovery_message.text = "We have sent a temporary passcode to your registered email address."
                recovery_btn.text = getString(R.string.btn_next)
            }

            RecoveryState.STATE_OTP -> {
                state = RecoveryState.STATE_NEW_PASSWORD
                recovery_email.visibility = View.GONE
                recovery_otp.visibility = View.GONE
                recovery_new_password.visibility = View.VISIBLE
                recovery_new_password_confirm.visibility = View.VISIBLE
                recovery_title.text = "Reset Password"
                recovery_message.text = "Please enter your new password."
                recovery_btn.text = getString(R.string.btn_confirm)
            }
        }
    }

    enum class RecoveryState {
        STATE_EMAIL,
        STATE_OTP,
        STATE_NEW_PASSWORD
    }
}