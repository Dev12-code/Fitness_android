package com.cbi.app.trs.features.fragments.login

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.data.entities.QuizStatus
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.usecases.user.PostDeviceToken
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.utils.CommonUtils
import com.cbi.app.trs.features.viewmodel.AuthenticateViewModel
import com.cbi.app.trs.features.viewmodel.QuizViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_quiz_gender.*
import javax.inject.Inject

class LoginFragment : LightBaseFragment() {
    override fun layoutId() = R.layout.fragment_login

    private var fcmToken: String = ""

    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 32313

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var authenticateViewModel: AuthenticateViewModel

    private lateinit var quizViewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        authenticateViewModel = viewModel(viewModelFactory) {
            observe(userData, ::onReceiveSignIn)
            observe(userSignUpData, ::onReceiveSignUp)
            failure(failureData, ::handleFailure)
        }

        quizViewModel = viewModel(viewModelFactory) {
            observe(quizStatus, ::onReceiveQuizStatus)
            failure(failureData, ::handleFailure)
        }

        getFCMToken()
        initFacebookCallback()
    }

    private fun initFacebookCallback() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    handleFacebookSignInResult(loginResult)
                }

                override fun onCancel() {
                    Log.d("Duy", "FacebookCallback:onCancel")
                }

                override fun onError(exception: FacebookException) {
                    Log.d("Duy", "FacebookCallback:onError ${exception.message}")
                }
            })
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

    private fun onReceiveQuizStatus(quizStatus: QuizStatus?) {
        hideProgress()
        if (quizStatus == null) return
        if (quizStatus.initial_quiz == 0) {//Not yet update quiz
            navigator.showQuiz(activity)
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        } else {
            navigator.showTrial(activity, userDataCache)
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
    }

    private fun onReceiveSignIn(userData: UserData?) {
        //save last login email
        sharedPreferences.edit().putString(AppConstants.LAST_LOGIN_EMAIL, email.text.toString())
            .apply()
        if (userData != null) {
            handleSignIn()
        }
        authenticateViewModel.postDeviceToken(
            userDataCache.get()?.user_token?.userID,
            PostDeviceToken.Params(device_token = fcmToken)
        )
    }

    private fun onReceiveSignUp(userData: UserData?) {
        if (userData != null) {
            handleSignUp()
        }
        authenticateViewModel.postDeviceToken(
            userDataCache.get()?.user_token?.userID,
            PostDeviceToken.Params(device_token = fcmToken)
        )
    }

    private fun handleSignIn() {
//        if (arguments != null && arguments!!.getBoolean(HAS_EXPIRED, false)) {
//            hideProgress()
//            activity?.setResult(Activity.RESULT_OK)
//            activity?.finish()
//        } else {
        userDataCache.get()?.user_token?.userID?.let {
            quizViewModel.getQuizStatus(it)
        }
//        }
    }

    private fun handleSignUp() {
        hideProgress()
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
        navigator.showQuiz(activity)
    }

    override fun handleFailure(failure: Failure?) {
        if (failure is Failure.ServerError) {
            CommonUtils.showError(context, "Login Fail", "${failure.reason}")
        } else {
            super.handleFailure(failure)
        }
        hideProgress()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_bg.loadFromLocal(
            R.drawable.login_bg_new,
            isAnimation = false,
            isPlaceHolder = true,
            resourceIdHolder = R.color.color_white
        )
        initView()
        //update last login email
        val lastLoginEmail = sharedPreferences.getString(AppConstants.LAST_LOGIN_EMAIL, "")
        if (!TextUtils.isEmpty(lastLoginEmail)) {
            email.setText(lastLoginEmail)
        }
    }

    private fun initView() {
        let_take_a_look.paintFlags = let_take_a_look.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                email.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pass_layout.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        signin_btn.setOnClickListener {
            var isError = false
            if (email.text.toString().trim().isEmpty()) {
//                email.error = "Email is not empty"
                CommonUtils.showError(activity, "Login Failed", "Please enter your email address.")
                isError = true
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
                CommonUtils.showError(
                    activity,
                    "Login Failed",
                    "Please enter a valid email address to proceed."
                )
                isError = true
            } else {
                email.error = null
            }

            if (isError) return@setOnClickListener

            pass_layout.error = null
            if (pass.text.isNullOrEmpty()) {
                CommonUtils.showError(activity, "Login Failed", "Please enter your password.")
                isError = true
            }

            if (isError) return@setOnClickListener

            authenticateViewModel.signIn(email.text.toString(), pass.text.toString())
            showProgress()
        }
        signup_btn.setOnClickListener { navigator.showSignUp(activity, arguments) }
        forgot_password.setOnClickListener {
            navigator.showRecoveryPassword(activity)
        }
        let_take_a_look.setOnClickListener { navigator.showIntro(activity) }
        let_take_a_look.extendTouch()

        google_sign_in_button.setOnClickListener {
            if (GoogleSignIn.getLastSignedInAccount(activity) != null) {
                mGoogleSignInClient.signOut()
            } else {
                signInGoogle()
            }
        }
        google_sign_in_button.extendTouch()

        facebook_sign_in_fake_button.setOnClickListener { facebook_sign_in_button.performClick() }
        facebook_sign_in_fake_button.extendTouch()

        facebook_sign_in_button.setPermissions("email")
        facebook_sign_in_button.fragment = this
        facebook_sign_in_button.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookSignInResult(result)
                }

                override fun onCancel() {
                    Log.d("Duy", "FacebookCallback:onCancel")
                }

                override fun onError(exception: FacebookException) {
                    Log.d("Duy", "FacebookCallback:onError ${exception.message}")
                }

            })
    }

    private fun handleGoogleSignIn(idToken: String?) {
        if (idToken.isNullOrEmpty()) {
            DialogAlert().setTitle("Google Sign-in").setMessage("Token is null or empty")
                .setCancel(false).show(activity)
            return
        }
        showProgress()
        authenticateViewModel.googleSignIn(idToken)
    }

    private fun handleFacebookSignIn(accessToken: AccessToken) {
        showProgress()
        authenticateViewModel.facebookSignIn(accessToken.token)
    }

    private fun handleFacebookSignInResult(result: LoginResult?) {
        if (result?.accessToken != null && !result.accessToken.isExpired) {
            handleFacebookSignIn(result.accessToken)
        } else {
            DialogAlert().setTitle("Facebook Sign-in").setMessage("Token is null/empty or expired")
                .setCancel(false).show(activity)
        }
    }

    private fun signInGoogle() {
        startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
                mGoogleSignInClient.signOut()
            } catch (e: ApiException) {
                AppLog.e("Duy", "GoogleSignInResult : fail code=${e.statusCode}")
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            LoginManager.getInstance().logOut()
        }
    }

    private fun handleGoogleSignInResult(signedInAccountFromIntent: Task<GoogleSignInAccount>) {
        val googleAccount = signedInAccountFromIntent?.getResult(ApiException::class.java)
        handleGoogleSignIn(googleAccount?.idToken)
    }
}
