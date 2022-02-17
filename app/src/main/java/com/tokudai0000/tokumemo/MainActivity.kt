package com.tokudai0000.tokumemo

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.tokudai0000.tokumemo.menu.MenuActivity

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//                if (result?.resultCode == Activity.RESULT_OK) {
//                    result.data?.let { data: Intent ->
//                        val value = data.getIntExtra("CHILD_KEY", 0)
//                        Toast.makeText(this, "$value", Toast.LENGTH_LONG).show()
//                    }
//                }
        // 呼び出し先のActivityを閉じた時に呼び出されるコールバックを登録
        // (呼び出し先で埋め込んだデータを取り出して処理する)
        if (result.resultCode == Activity.RESULT_OK) {
            // RESULT_OK時の処理
            val resultIntent = result.data
            val message = resultIntent?.getStringExtra("CHILD_KEY")
            webView!!.loadUrl(message!!)
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
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
            val intent = Intent(this, MenuActivity::class.java)
//            startActivity(intent)
            startForResult.launch(intent)

        }

        webViewSetup()
        login()

    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val urlString = data?.getStringExtra("CHILD_KEY")
//
//        webView!!.loadUrl(urlString!!)
//
//    }

    // MARK: - Private func
    // 教務事務システムのみ、別のログイン方法をとっている？ため、初回に教務事務システムにログインし、キャッシュで別のサイトもログインしていく
    private fun login() {
        // 次に読み込まれるURLはJavaScriptを動かすことを許可する(ログイン用)
//        DataManager.instance.isExecuteJavascript = true
//        viewModel.isInitFinishLogin = true
//        val urlString = getString(R.string.universityTransitionLogin)
        val urlString = "https://eweb.stud.tokushima-u.ac.jp/Portal/"
        webView!!.loadUrl(urlString)
    }

    private fun webViewSetup() {
        // javascriptを有効化
        webView!!.settings.javaScriptEnabled = true
        //ウェブページがクロム（または、その他の検索アプリ）に開かなくてアプリのwebviewに開かるような設定
        webView!!.webViewClient = object : WebViewClient(){
            // MARK: - 読み込み設定（リクエスト前）
            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                Log.d("--- ログ --->", "タップされたリンクのurl:$url")
            }

            // MARK: - 読み込み完了
            override fun onPageFinished(view: WebView?, urlString: String?) {
                webView!!.evaluateJavascript("document.getElementById('username').value= '" + "c611821006" + "'", null)
                webView!!.evaluateJavascript("document.getElementById('password').value= '" + "" + "'", null)
                webView!!.evaluateJavascript("document.getElementsByClassName('form-element form-button')[0].click();", null)
            }
        }


    }


}