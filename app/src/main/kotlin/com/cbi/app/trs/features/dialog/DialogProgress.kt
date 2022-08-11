package com.cbi.app.trs.features.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.core.platform.BaseDialog
import com.google.android.exoplayer2.util.RepeatModeUtil
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.dialog_progress.view.*


open class DialogProgress : BaseDialog() {

    private var isDim: Boolean = false

    fun show(context: Context) {
        super.show(context, TAG)
    }


    companion object {
        private val TAG = DialogProgress::class.java.simpleName
        fun hide(context: BaseActivity) {
            try {
                context.supportFragmentManager.findFragmentByTag(TAG)
                    ?.let { (it as DialogFragment).dismiss() }
            } catch (e: Exception) {

            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.DialogDimTheme)
//        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        if (isDim) {
            dialog.window?.setDimAmount(0.5f)
        } else {
            dialog.window?.setDimAmount(0f)
        }
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                BaseActivity.apiRequestCount = 0
                (activity as BaseActivity).isShowProgress = false
                dialog.dismiss()
                if (!(activity as BaseActivity).defaultBack()) {
                    (activity as BaseActivity).getCurrentFragment()?.close()
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_progress, container, false)
        context?.let {
            try {
                Glide.with(it)
                    .asGif()
                    .load(R.drawable.loading_logo_new)
                    .placeholder(R.drawable.loading_logo_new)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view.iv_loading)

                //animate
                val animation = AnimationUtils.loadAnimation(it, R.anim.animation_loading)
                animation.repeatMode = Animation.REVERSE
                view.iv_loading.startAnimation(animation)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
        return view
    }
}