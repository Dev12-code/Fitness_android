package com.cbi.app.trs.features.fragments.webview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.data.entities.HtmlData
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.viewmodel.SettingViewModel
import kotlinx.android.synthetic.main.fragment_webview.*
import java.net.MalformedURLException
import java.net.URL


class WebviewFragment : DarkBaseFragment() {
    override fun layoutId() = R.layout.fragment_webview

    companion object {
        const val URI_EXTRA = "URI_EXTRA"
        const val WEBVIEW_TYPE = "WEBVIEW_TYPE"
    }

    lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        settingViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(policyData, ::onReceiveHtml)
            observe(helpData, ::onReceiveHtml)
        }
    }

    override fun onBackPressed(): Boolean {
        if (webview.canGoBack()) {
            webview.goBack()
            return true
        }
        return false
    }

    private fun onReceiveHtml(htmlData: HtmlData?) {
        hideProgress()
        if (htmlData == null) return
        webview.loadDataWithBaseURL(null, htmlData.html_code, "text/html", "UTF-8", null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
    }

    private fun loadData() {
        arguments?.let {
            when (it.getString(WEBVIEW_TYPE)) {
                "help" -> {
                    showProgress()
                    webview_bg.setImageResource(R.drawable.help_bg)
                    //load help html
                    webview.loadUrl("https://thereadystate.com/help")
//                    settingViewModel.getHelpData(userID)
                }
                "policy" -> {
                    showProgress()
                    webview_bg.setImageResource(R.drawable.policy_bg)
                    webview.loadUrl("https://thereadystate.com/privacy_policy/")
//                    settingViewModel.getPolicyData(userID)
                }
            }
        }
    }

    override fun onReloadData() {
        loadData()
    }

    private fun initView() {
        back_btn.setOnClickListener {
            if (!onBackPressed()) close()
        }
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = MyWebViewClient()
        webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress >= 50) {
                    hideProgress()
                }
            }
        })
        arguments?.getString(URI_EXTRA, null)?.let { webview.loadUrl(it) }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//            if (isValidURL(url)) return false

            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                activity?.startActivity(this)
            }
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }
    }

    fun isValidURL(urlStr: String?): Boolean {
        return try {
            val url = URL(urlStr)
            true
        } catch (e: MalformedURLException) {
            false
        }
    }
}