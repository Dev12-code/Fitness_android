package com.cbi.app.trs.features.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.user.PostDeviceToken
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.AuthenticateViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_recovery_pass.back_btn
import kotlinx.android.synthetic.main.fragment_signup.*
import javax.inject.Inject

class SignUpFragment() : LightBaseFragment() {
    private var fcmToken: String = ""

    @Inject
    lateinit var navigator: Navigator

    private lateinit var authenticateViewModel: AuthenticateViewModel

    override fun layoutId(): Int {
        return R.layout.fragment_signup
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        authenticateViewModel = viewModel(viewModelFactory) {
            observe(userSignUpData, ::onReceiveSignUp)
            observe(postDeviceTokenData, ::onReceivePostNotification)
            failure(failureData, ::handleFailure)
        }
        getFCMToken()
    }

    private fun onReceiveSignUp(userData: UserData?) {
        if (userData != null) {
            handleSignUp()
        }
    }

    private fun onReceivePostNotification(baseEntities: BaseEntities?) {
        hideProgress()
    }

    private fun handleSignUp() {
        hideProgress()
        authenticateViewModel.postDeviceToken(
            userDataCache.get()?.user_token?.userID,
            PostDeviceToken.Params(device_token = fcmToken)
        )

        navigator.showSignUpIntroVideo(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        back_btn.setOnClickListener {
            close()
        }
        signup_btn.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        var isError = false
        if (email.text.toString().trim().isEmpty()) {
            CommonUtils.showError(activity, "SignUp Failed", "Please enter your email address.")
            isError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
            CommonUtils.showError(
                activity,
                "SignUp Failed",
                "Please enter a valid email address to proceed."
            )
            isError = true
        } else {
            email.error = null
        }

        if (isError) return

        pass_layout.error = null
        if (pass.text.isNullOrEmpty()) {
            CommonUtils.showError(activity, "SignUp Failed", "Please enter your password.")
            isError = true
        }
        if (isError) return

        authenticateViewModel.signUp(email.text.toString(), pass.text.toString())
        showProgress()
    }

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Duy", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                fcmToken = task.result?.token.toString()
                Log.d("Duy", "token:$fcmToken")
            })
    }
}