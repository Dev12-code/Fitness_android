package com.cbi.app.trs.features.fragments.contact_us

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.platform.LightBaseFragment
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import kotlinx.android.synthetic.main.fragmen_contact_us.*

class ContactUsFragment : LightBaseFragment() {
    override fun layoutId() = R.layout.fragmen_contact_us

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        back_btn.setOnClickListener { close() }
        send_btn.setOnClickListener {
            if (content.text.isNullOrEmpty()) {
                DialogAlert().setCancel(false).setMessage("Please enter message to send")
                    .setTitle("Contact us").show(activity)
            } else {
                latestExampleEmailCreation(
                    arrayOf("info@thereadystate.com"),
                    "[Virtual Mobility Coach] - Contact us - userID #${userID}",
                    content.text.toString()
                )
            }
        }
    }

    private fun latestExampleEmailCreation(
        addresses: Array<String>, subject: String, text: String
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_BCC, arrayOf("cbi.thereadystate@gmail.com"))
        }
        if (intent.resolveActivity(context!!.packageManager) != null) {
            startActivity(intent)
        } else {
            DialogAlert().setCancel(false)
                .setMessage("Please connect your email account in order to send emails.")
                .setTitle("Email failed to send.").show(activity)
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
