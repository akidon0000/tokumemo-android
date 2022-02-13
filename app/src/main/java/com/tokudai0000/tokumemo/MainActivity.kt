package com.tokudai0000.tokumemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import com.tokudai0000.tokumemo.menu.MenuActivity

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Outlet
        webView = findViewById<WebView>(R.id.webView)
        val webViewGoBackButton = findViewById<Button>(R.id.webViewGoBackButton)
        val webViewGoForwardButton = findViewById<Button>(R.id.webViewGoForwardButton)
        val showServiceListsButton = findViewById<Button>(R.id.showServiceListsButton)

        // Action
        webViewGoBackButton.setOnClickListener { v: View? -> webView?.goBack() }
        webViewGoForwardButton.setOnClickListener { v: View? -> webView?.goForward() }
        showServiceListsButton.setOnClickListener { v: View? ->
            val intent = Intent(this, MenuActivity::class.java).run {
                putExtra("PARENT_KEY", "親からのデータです！")
            }
            startActivity(intent)
        }


        webViewSetup()
        login()
    }

    // MARK: - Private func
    // 教務事務システムのみ、別のログイン方法をとっている？ため、初回に教務事務システムにログインし、キャッシュで別のサイトもログインしていく
    private fun login() {
        // 次に読み込まれるURLはJavaScriptを動かすことを許可する(ログイン用)
//        DataManager.instance.isExecuteJavascript = true
//        viewModel.isInitFinishLogin = true
//        val urlString = getString(R.string.universityTransitionLogin)
        val urlString = "https://www.tokushima-u.ac.jp/"
        webView!!.loadUrl(urlString)
    }

    private fun webViewSetup() {
        // javascriptを有効化
        webView!!.settings.javaScriptEnabled = true
        //ウェブページがクロム（または、その他の検索アプリ）に開かなくてアプリのwebviewに開かるような設定
        webView!!.webViewClient = object : WebViewClient(){}

    }


}