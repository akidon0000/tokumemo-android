package com.tokudai0000.tokumemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webView = findViewById<WebView>(R.id.webView)
        //ウェブページがクロム（または、その他の検索アプリ）に開かなくてアプリのwebviewに開かるような設定
        webView.webViewClient = object : WebViewClient(){}
        //webviewに入力された値に対するウェブページを表示
        webView.loadUrl("https://www.tokushima-u.ac.jp/")

    }
}