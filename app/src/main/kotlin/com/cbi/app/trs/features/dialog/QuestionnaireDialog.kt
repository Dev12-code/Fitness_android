package com.cbi.app.trs.features.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import androidx.fragment.app.FragmentActivity
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.BaseDialog
import kotlinx.android.synthetic.main.dialog_questionnaire.*

class QuestionnaireDialog : BaseDialog() {
    private val TAG: String = "QuestionnaireDialog"

    private var listener: DialogInterface.OnClickListener? = null

    fun show(context: Context) {
        super.show(context, TAG)
    }

    fun show(activity: FragmentActivity?) {
        if (activity == null) return
        super.show(activity, TAG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.DialogDimTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setDimAmount(0.5f)
        return dialog
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        //user click outside, open home
        listener?.onClick(dialog, -1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.dialog_questionnaire, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        answer_1.setOnClickListener {
            listener?.onClick(dialog, 1)
            dialog?.dismiss()
        }
        answer_2.setOnClickListener {
            listener?.onClick(dialog, 2)
            dialog?.dismiss()
        }
        answer_3.setOnClickListener {
            listener?.onClick(dialog, 3)
            dialog?.dismiss()
        }
        answer_4.setOnClickListener {
            listener?.onClick(dialog, 4)
            dialog?.dismiss()
        }
    }

    fun setListener(listener: DialogInterface.OnClickListener): QuestionnaireDialog {
        this.listener = listener
        return this
    }

    fun setEnableMobility(enable: Boolean): QuestionnaireDialog {
        if (answer_4 == null){
            return this
        }
        if (enable) {
            answer_4.visibility = View.VISIBLE
            line_4.visibility = View.VISIBLE
        } else {
            answer_4.visibility = View.GONE
            line_4.visibility = View.GONE
        }
        return this
    }

}
