package com.tokudai0000.tokumemo.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProviders
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tokudai0000.tokumemo.AKLibrary.guard
import com.tokudai0000.tokumemo.ui.menu.MenuActivity
import com.tokudai0000.tokumemo.MenuLists
import com.tokudai0000.tokumemo.Model.DataManager
import com.tokudai0000.tokumemo.R
import com.tokudai0000.tokumemo.ui.menu.Password.PasswordActivity
import com.tokudai0000.tokumemo.ui.menu.Syllabus.SyllabusActivity

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    // ログイン用　アンケート催促が出ないユーザー用
    private var isInitFinishLogin = true

    private var viewModel: MainModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // WebViewの初期設定を行う
        webViewSetup()

        initActivitySetup()
    }

    // Private func
    /// MainActivityの初期セットアップ
    private fun initActivitySetup() {
        // Outlet
        viewModel = ViewModelProviders.of(this).get(MainModel::class.java)
        webView = findViewById<WebView>(R.id.webView)

        val webViewGoBackButton = findViewById<Button>(R.id.webViewGoBackButton)
        val webViewGoForwardButton = findViewById<Button>(R.id.webViewGoForwardButton)
        val showMenuListsButton = findViewById<Button>(R.id.showMenuListsButton)

        // Action
        webViewGoBackButton.setOnClickListener { webView!!.goBack() }
        webViewGoForwardButton.setOnClickListener { webView!!.goForward() }
        showMenuListsButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            // 戻ってきた時、startForMenuActivityを呼び出す
            startForMenuActivity.launch(intent)
        }

        // パスワード登録者、非登録者によって表示するURLを変更する
        loadLoginPage()
    }

    // 子(MenuActivity)から戻ってきた時、データを子から受けとり処理を行う
    // backButtonではここは通らない
    private val startForMenuActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            throw Exception("[ERROR]: MenuActivityからMainActivityへの遷移時エラー")
        }

        // 呼び出し先のActivityを閉じた時に呼び出されるコールバックを登録
        // (呼び出し先で埋め込んだデータを取り出して処理する)
        // RESULT_OK時の処理
        val resultIntent = result.data
        // MenuActivityで、どのセルが選択されたかを取得
        val menuID = resultIntent?.getStringExtra("MenuID_KEY")
        val menuUrl = resultIntent?.getStringExtra("MenuUrl_KEY")
        // menuIDによる条件分岐
        when {
            // リマークしているのは後日実装予定の機能
//                menuID == MenuLists.libraryCalendar.toString() -> {
//                }

            menuID == MenuLists.syllabus.toString() -> {
                // シラバス検索画面を表示
                val intent = Intent(this, SyllabusActivity::class.java)
                // 戻ってきた時、startForSyllabusActivityを呼び出す
                startForSyllabusActivity.launch(intent)
            }

//                menuID == MenuLists.customize.toString() -> {
//                }

//                menuID == MenuLists.firstViewSetting.toString() -> {
//                }

            menuID == MenuLists.password.toString() -> {
                // パスワード登録画面を表示
                val intent = Intent(this, PasswordActivity::class.java)
                // 戻ってきた時、startForPasswordActivityを呼び出す
                startForPasswordActivity.launch(intent)
            }

//                menuID == MenuLists.aboutThisApp.toString() -> {
//                }

            else -> {
                webView!!.loadUrl(menuUrl!!) // URLが無い場合は上記で除けているので強制アンラップ
            }
        }
    }

    // 子(SyllabusActivity)で検索ボタンを押した場合、検索処理を行う
    // backButtonではここは通らない
    private val startForSyllabusActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            throw Exception("[ERROR]: PasswordActivityからMainActivityへの遷移時エラー")
        }

        DataManager.canExecuteJavascript = true

        // RESULT_OK時の処理
        val resultIntent = result.data
        // シラバスをJavaScriptで自動入力する際、参照変数
        viewModel!!.subjectName = guard(resultIntent?.getStringExtra("Subject_KEY")) {} // nilの場合は代入しないだけ
        viewModel!!.teacherName = guard(resultIntent?.getStringExtra("Teacher_KEY")) {}
        webView!!.loadUrl("http://eweb.stud.tokushima-u.ac.jp/Portal/Public/Syllabus/")
    }

    // 子(PasswordActivity)で登録ボタンを押した場合、再度ログイン処理を行う
    // backButtonではここは通らない
    private val startForPasswordActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            throw Exception("[ERROR]: PasswordActivityからMainActivityへの遷移時エラー")
        }
        // ログイン処理を行う
        loadLoginPage()
    }

    // 教務事務システムのみ、別のログイン方法をとっている？ため、初回に教務事務システムにログインし、キャッシュで別のサイトもログインしていく
    private fun loadLoginPage() {
        // 次に読み込まれるURLはJavaScriptを動かすことを許可する(ログイン用)
        DataManager.canExecuteJavascript = true
        val urlString = "https://eweb.stud.tokushima-u.ac.jp/Portal/"
        webView!!.loadUrl(urlString)
    }

    private fun webViewSetup() {
        // javascriptを有効化
        webView!!.settings.javaScriptEnabled = true // 強制アンラップしないとエラーが出る
        //ウェブページがGoogleChrome（または、その他の検索アプリ）で開かなくてアプリのwebviewに開かるような設定
        webView?.webViewClient = object : WebViewClient(){

            /// 読み込み設定（リクエスト前）
            ///
            /// 以下2つの状態であったら読み込みを開始する。
            ///  1. 読み込み前のURLがnilでないこと
            ///  2. 許可されたドメインであること
            override fun onPageStarted(view: WebView?, urlString: String, favicon: Bitmap?) {
                Log.d("--- ログ --->", "タップされたリンクのurl:$urlString")

                // 許可されたドメインか判定
                if (viewModel!!.isAllowedDomainCheck(urlString) == false) {
                    // 許可外のURLが来た場合は、Chromeで開く
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                    intent.setPackage("com.android.chrome")
                    startActivity(intent)
                    return
                }

                // お気に入り画面のためにURLを保持
                viewModel!!.urlString = urlString

                // タイムアウトした場合
                if (viewModel!!.isTimeout(urlString)) {
                    // ログイン処理を始める
                    loadLoginPage()
                }

                // 問題ない場合読み込みを許可
                return
            }

            // MARK: - 読み込み完了
            override fun onPageFinished(view: WebView?, urlString: String?) {
                val urlString = guard(urlString) {
                    throw IllegalStateException("urlStringがnull")
                }

                val cAccount = encryptedLoad("KEY_cAccount")
                val password = encryptedLoad("KEY_password")

                // 大学統合認証システムのログイン処理が終了した場合
                if (viewModel!!.isLoggedin(urlString)) {
                    // 初期設定画面がメール(Outlook)の場合用
                    DataManager.canExecuteJavascript = true
                    // ユーザが設定した初期画面を読み込む
                    webView!!.loadUrl(viewModel!!.searchInitPageUrl())
                }

                when {

                    // JavaScriptを実行するフラグが立っていない場合は抜ける
                    !DataManager.canExecuteJavascript -> {

                    }

                    // 大学サイト、ログイン画面 && JavaScriptを動かしcアカウント、パスワードを自動入力する必要があるのか判定
                    urlString.startsWith("https://localidp.ait230.tokushima-u.ac.jp/idp/profile/SAML2/Redirect/SSO?execution=") -> {
                        webView?.evaluateJavascript("document.getElementById('username').value= '" + "$cAccount" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('password').value= '" + "$password" + "'", null)
                        webView?.evaluateJavascript("document.getElementsByClassName('form-element form-button')[0].click();", null)
                        // フラグを下ろす
                        DataManager.canExecuteJavascript = false
                    }

                    // シラバス
                    urlString == "http://eweb.stud.tokushima-u.ac.jp/Portal/Public/Syllabus/" -> {
                        // シラバスの検索画面
                        // ネイティブでの検索内容をWebに反映したのち、検索を行う
                        webView?.evaluateJavascript("document.getElementById('ctl00_phContents_txt_sbj_Search').value= '" + "${viewModel!!.subjectName}" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('ctl00_phContents_txt_staff_Search').value= '" + "${viewModel!!.teacherName}" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('ctl00_phContents_ctl06_btnSearch').click();", null)

                        viewModel!!.subjectName = "" // 初期化
                        viewModel!!.teacherName = ""
                        // フラグを下ろす
                        DataManager.canExecuteJavascript = false
                    }

                    // outlook(メール) && 登録者判定
                    urlString.startsWith("https://wa.tokushima-u.ac.jp/adfs/ls") -> {
                        // outlook(メール)へのログイン画面
                        // cアカウントを登録していなければ自動ログインは効果がないため
                        // 自動ログインを行う
                        webView?.evaluateJavascript("document.getElementById('userNameInput').value= '" + "$cAccount" + "@tokushima-u.ac.jp'", null)
                        webView?.evaluateJavascript("document.getElementById('passwordInput').value= '" + "$password" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('submitButton').click();", null)
                        // フラグを下ろす
                        DataManager.canExecuteJavascript = false
                    }

                    // キャリア支援室
                    urlString.startsWith("https://www.tokudai-syusyoku.com/index.php") -> {
                        // 徳島大学キャリアセンター室
                        // 自動入力を行う(cアカウントは同じ、パスワードは異なる可能性あり)
                        // ログインボタンは自動にしない(キャリアセンターと大学パスワードは人によるが同じではないから)
                        webView?.evaluateJavascript("document.getElementsByName('user_id')[0].value= '" + "$cAccount" + "'", null)
                        webView?.evaluateJavascript("document.getElementsByName('user_password')[0].value= '" + "$password" + "'", null)
                        // フラグを下ろす
                        DataManager.canExecuteJavascript = false
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