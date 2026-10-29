package com.mastimasala.pro

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.widget.Toast
import android.webkit.ValueCallback
import android.webkit.GeolocationPermissions
import android.net.Uri

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var pendingPermissionRequest: PermissionRequest? = null
    private val cameraRequestCode = 701

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowContentAccess = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread {
                    val needed = mutableListOf<String>()
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        needed.add(Manifest.permission.CAMERA)
                    }
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        needed.add(Manifest.permission.RECORD_AUDIO)
                    }
                    if (needed.isEmpty()) {
                        request.grant(request.resources)
                    } else {
                        pendingPermissionRequest = request
                        requestPermissions(needed.toTypedArray(), cameraRequestCode)
                    }
                }
            }
        }

        webView.loadUrl("https://adityarajsingh8d82020-web.github.io/masti-masala-pro/")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraRequestCode) {
            val request = pendingPermissionRequest
            pendingPermissionRequest = null
            if (request != null) {
                val allGranted = grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (allGranted) request.grant(request.resources) else request.deny()
            }
        }
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}
