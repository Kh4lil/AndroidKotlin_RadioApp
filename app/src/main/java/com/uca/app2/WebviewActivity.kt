package com.uca.app2

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebviewActivity : AppCompatActivity() {
    private lateinit var url: String
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = intent.extras?.getString("EXTRA_URL").takeIf { !it.isNullOrBlank() }
            ?: throw IllegalArgumentException("Url cannot be null!")
        setContentView(R.layout.layout_webview)
        webView = findViewById(R.id.web_view)
        if (webView != null) {
            webView?.webViewClient = WebViewClient()
            setWebSettings(webView!!)
            webView!!.loadUrl(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSettings(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
    }

    override fun onPause() {
        webView?.onPause()
        webView?.pauseTimers()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.resumeTimers()
        webView?.onResume()
    }

    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}