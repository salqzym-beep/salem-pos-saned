package com.mokh.sanad

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.webkit.*
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var status: TextView
    private val home = "https://possanad-sah-buyervercelapp.vercel.app/"

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 59, 99)
        window.navigationBarColor = Color.rgb(18, 59, 99)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.WHITE) }
        status = TextView(this).apply {
            text = "سند للمحاسبة"
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(18, 59, 99))
            setPadding(16, 10, 16, 10)
        }
        web = WebView(this)
        root.addView(status, LinearLayout.LayoutParams(-1, -2))
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)

        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.databaseEnabled = true
        web.settings.allowFileAccess = false
        web.settings.loadsImagesAutomatically = true
        web.settings.javaScriptCanOpenWindowsAutomatically = true
        web.settings.setSupportMultipleWindows(false)
        web.settings.cacheMode = if (isOnline()) WebSettings.LOAD_DEFAULT else WebSettings.LOAD_CACHE_ELSE_NETWORK
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)
        web.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                status.text = if (isOnline()) "سند للمحاسبة • متصل" else "سند للمحاسبة • وضع دون إنترنت (المحتوى المحفوظ)"
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                status.text = if (isOnline()) "سند للمحاسبة • متصل" else "سند للمحاسبة • دون إنترنت"
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    status.text = "لا يوجد اتصال • حاول إعادة التحميل عند توفر الشبكة"
                }
            }
        }
        web.webChromeClient = WebChromeClient()
        if (savedInstanceState != null) web.restoreState(savedInstanceState) else web.loadUrl(home)
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        web.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }
}
