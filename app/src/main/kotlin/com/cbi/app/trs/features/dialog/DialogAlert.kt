package com.cbi.app.trs.features.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentActivity
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.BaseDialog
import kotlinx.android.synthetic.main.dialog_alert.*


typealias OnPositive = () -> Unit

typealias OnNegative = () -> Unit

typealias OnDismiss = () -> Unit

open class DialogAlert : BaseDialog() {

    private val TAG = DialogAlert::class.java.simpleName

    private var title: String? = null
    private var message: String? = null
    private var titleNegative: String? = null
    private var titlePositive: String = "OK"

    private var onNegative: OnNegative? = null
    private var onPositive: OnPositive? = null
    private var onDismiss: OnDismiss? = null
    private var isDim: Boolean = true

    fun show(context: Context) {
        super.show(context, TAG)
    }

    fun show(activity: FragmentActivity?) {
        if (activity == null) return
        super.show(activity, TAG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.DialogDimTheme)
//        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        if (isDim) {
            dialog.window?.setDimAmount(0.5f)
        }
        dialog.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
        return dialog
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.dialog_alert, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (title.isNullOrEmpty()) {
            txtTitle.visibility = View.GONE
        } else {
            txtTitle.visibility = View.VISIBLE
            txtTitle.text = title
        }

        if (message.isNullOrEmpty()) {
            txtMessage.visibility = View.GONE
        } else {
            txtMessage.visibility = View.VISIBLE
            txtMessage.text = message
        }

        if (titleNegative.isNullOrEmpty()) {
            left_btn.visibility = View.GONE
        } else {
            left_btn.visibility = View.VISIBLE
            left_btn.text = titleNegative
            left_btn.setOnClickListener {
                onNegative?.invoke()
                dismiss()
            }
        }

        right_btn.text = titlePositive
        right_btn.setOnClickListener {
            onPositive?.invoke()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }


    fun setTitle(title: String): DialogAlert {
        this.title = title
        return this
    }

    fun setMessage(message: String): DialogAlert {
        this.message = message
        return this
    }

    fun setTitlePositive(positive: String): DialogAlert {
        this.titlePositive = positive
        return this
    }

    fun setTitleNegative(negative: String): DialogAlert {
        this.titleNegative = negative
        return this
    }

    fun setCancel(isCancelable: Boolean): DialogAlert {
        this.isCancelable = isCancelable
        return this
    }

    fun onPositive(onPositive: OnPositive?): DialogAlert {
        this.onPositive = onPositive
        return this
    }

    fun onNegative(onNegative: OnNegative?): DialogAlert {
        this.onNegative = onNegative
        return this
    }

    fun onDismiss(onDismiss: OnDismiss?): DialogAlert {
        this.onDismiss = onDismiss
        return this
    }

    fun setDim(isDim: Boolean): DialogAlert {
        this.isDim = isDim
        return this
    }

}