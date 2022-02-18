package com.tokudai0000.tokumemo.View.Main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tokudai0000.tokumemo.AKLibrary.guard
import com.tokudai0000.tokumemo.View.Menu.MenuActivity
import com.tokudai0000.tokumemo.MenuLists
import com.tokudai0000.tokumemo.Model.DataManager
import com.tokudai0000.tokumemo.R
import com.tokudai0000.tokumemo.View.Menu.Password.PasswordActivity

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Outlet
        webView = findViewById<WebView>(R.id.webView)
        val webViewGoBackButton = findViewById<Button>(R.id.webViewGoBackButton)
        val webViewGoForwardButton = findViewById<Button>(R.id.webViewGoForwardButton)
        val showMenuListsButton = findViewById<Button>(R.id.showMenuListsButton)

        // Action
        webViewGoBackButton.setOnClickListener { webView?.goBack() }
        webViewGoForwardButton.setOnClickListener { webView?.goForward() }
        showMenuListsButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startForMenuActivity.launch(intent)
        }

        // WebViewの初期設定を行う
        webViewSetup()

        // パスワード登録者、非登録者によって表示するURLを変更する
        login()

    }


    // Private
    // 子(MenuActivity)から戻ってきた時、データを子から受けとり処理を行う
    private val startForMenuActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // 呼び出し先のActivityを閉じた時に呼び出されるコールバックを登録
        // (呼び出し先で埋め込んだデータを取り出して処理する)
        if (result.resultCode == Activity.RESULT_OK) {
            // RESULT_OK時の処理
            val resultIntent = result.data
            val menuID = resultIntent?.getStringExtra("MenuID_KEY")
            val menuUrl = resultIntent?.getStringExtra("MenuUrl_KEY")
            when {
                menuID == MenuLists.currentTermPerformance.toString() -> {

                }
                menuID == MenuLists.libraryCalendar.toString() -> {

                }
                menuID == MenuLists.syllabus.toString() -> {

                }
                menuID == MenuLists.customize.toString() -> {

                }
                menuID == MenuLists.firstViewSetting.toString() -> {

                }
                menuID == MenuLists.password.toString() -> {
                    val intent = Intent(this, PasswordActivity::class.java)
                    startForPasswordActivity.launch(intent)
                }
                menuID == MenuLists.aboutThisApp.toString() -> {

                }
                else -> {
                    webView?.loadUrl(menuUrl!!) // URLが無い場合は上記で除けているので強制アンラップ
                }
            }
        }
    }
    // 子(PasswordActivity)で登録ボタンを押した場合、再度ログイン処理を行う(backButtonではログイン処理を行わない)
    private val startForPasswordActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            login()
        }
    }

    // 教務事務システムのみ、別のログイン方法をとっている？ため、初回に教務事務システムにログインし、キャッシュで別のサイトもログインしていく
    private fun login() {
        // 次に読み込まれるURLはJavaScriptを動かすことを許可する(ログイン用)
        DataManager.isExecuteJavascript = true
//        viewModel.isInitFinishLogin = true
//        val urlString = getString(R.string.universityTransitionLogin)
        val urlString = "https://eweb.stud.tokushima-u.ac.jp/Portal/"
        webView?.loadUrl(urlString)
    }

    private fun webViewSetup() {
        // javascriptを有効化
        webView!!.settings.javaScriptEnabled = true // 強制アンラップしないとエラーが出る
        //ウェブページがクロム（または、その他の検索アプリ）に開かなくてアプリのwebviewに開かるような設定
        webView?.webViewClient = object : WebViewClient(){
            // MARK: - 読み込み設定（リクエスト前）
            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                Log.d("--- ログ --->", "タップされたリンクのurl:$url")
            }

            // MARK: - 読み込み完了
            override fun onPageFinished(view: WebView?, urlString: String?) {
                val urlString = guard(urlString) {
                    throw IllegalStateException("urlStringがnull")
                }

                when {
                    // JavaScriptを実行するフラグが立っていない場合は抜ける
                    !DataManager.isExecuteJavascript -> {

                    }
                    // 大学サイト、ログイン画面 && JavaScriptを動かしcアカウント、パスワードを自動入力する必要があるのか判定
                    urlString.startsWith("https://localidp.ait230.tokushima-u.ac.jp/idp/profile/SAML2/Redirect/SSO?execution=") -> {
                        DataManager.isExecuteJavascript = false
                        val cAccount = encryptedLoad("KEY_cAccount")
                        val password = encryptedLoad("KEY_password")
                        webView?.evaluateJavascript("document.getElementById('username').value= '" + "$cAccount" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('password').value= '" + "$password" + "'", null)
                        webView?.evaluateJavascript("document.getElementsByClassName('form-element form-button')[0].click();", null)
                    }
                    // シラバス
                    urlString == "http://eweb.stud.tokushima-u.ac.jp/Portal/Public/Syllabus/" -> {

                    }
                    // outlook(メール) && 登録者判定
                    urlString.startsWith("https://wa.tokushima-u.ac.jp/adfs/ls") -> {

                    }
                    else -> {

                    }
                }


                

            }
        }
    }

    // 以下、暗号化してデバイスに保存する(PasswordActivityにも存在するので今後、統一)
    companion object {
        const val PREF_NAME = "encrypted_prefs"
    }
    // 読み込み
    private fun encryptedLoad(KEY: String): String {
        val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        val prefs = EncryptedSharedPreferences.create(
                applicationContext,
                MainActivity.PREF_NAME,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        return prefs.getString(KEY, "")!! // nilの場合は空白を返す
    }
    // 保存
    private fun encryptedSave(KEY: String, text: String) {
        val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        val prefs = EncryptedSharedPreferences.create(
                applicationContext,
                MainActivity.PREF_NAME,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        with (prefs.edit()) {
            putString(KEY, text)
            apply()
        }
    }

}