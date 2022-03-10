package com.tokudai0000.tokumemo.ui.main

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
import androidx.lifecycle.ViewModelProviders
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tokudai0000.tokumemo.AKLibrary.guard
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.ui.menu.MenuActivity
import com.tokudai0000.tokumemo.MenuLists
import com.tokudai0000.tokumemo.Model.DataManager
import com.tokudai0000.tokumemo.R
import com.tokudai0000.tokumemo.ui.menu.Password.PasswordActivity
import com.tokudai0000.tokumemo.ui.menu.Syllabus.SyllabusActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private var webView: WebView? = null
    // ログイン用　アンケート催促が出ないユーザー用
    private var isInitFinishLogin = true

    // シラバスをJavaScriptで自動入力する際、参照変数
    private var subjectName = ""
    private var teacherName = ""

    private val viewModel: MainModel = ViewModelProviders.of(this).get(MainModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureMainActivity()

        // WebViewの初期設定を行う
        webViewSetup()

        // パスワード登録者、非登録者によって表示するURLを変更する
        login()

    }

    // Private
    private fun configureMainActivity() {
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
    }

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
                    // 2020年4月〜2021年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2020
                    // 2021年4月〜2022年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2021

                    // 現在時刻の取得
                    // Dateを作成すると現在日時が入るし、CalenderをgetInstanceでも現在日時が入る
                    val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN);
                    var year: Int = calendar.get(Calendar.YEAR)
                    val month: Int = calendar.get(Calendar.MONTH) + 1 // 1月が0、12月が11であることに注意

                    // 1月から3月までは前年の成績
                    if (month <= 3) {
                        year -= 1
                    }
                    val urlString = "https://eweb.stud.tokushima-u.ac.jp/Portal/StudentApp/Sp/ReferResults/SubDetail/Results_Get_YearTerm.aspx?year=" + "${year.toString()}"
                    webView?.loadUrl(urlString)
                }

                menuID == MenuLists.libraryCalendar.toString() -> {

                }

                menuID == MenuLists.syllabus.toString() -> {
                    val intent = Intent(this, SyllabusActivity::class.java)
                    startForSyllabusActivity.launch(intent)
                }

                menuID == MenuLists.customize.toString() -> {

                }

                menuID == MenuLists.firstViewSetting.toString() -> {

                }

                menuID == MenuLists.password.toString() -> {
                    val intent = Intent(this, PasswordActivity::class.java)
                    startForPasswordActivity.launch(intent)
                }

//                menuID == MenuLists.aboutThisApp.toString() -> {
//
//                }

                else -> {
                    webView?.loadUrl(menuUrl!!) // URLが無い場合は上記で除けているので強制アンラップ
                }
            }
        }
    }

    // 子(PasswordActivity)で登録ボタンを押した場合、再度ログイン処理を行う(backButtonではログイン処理を行わない)
    private val startForSyllabusActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            DataManager.isExecuteJavascript = true

            // RESULT_OK時の処理
            val resultIntent = result.data
            // シラバスをJavaScriptで自動入力する際、参照変数
            subjectName = guard(resultIntent?.getStringExtra("Subject_KEY")) {} // nilの場合は代入しないだけ
            teacherName = guard(resultIntent?.getStringExtra("Teacher_KEY")) {}
            webView?.loadUrl("http://eweb.stud.tokushima-u.ac.jp/Portal/Public/Syllabus/")
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
        // 登録者か非登録者か判定
        if (encryptedLoad("KEY_cAccount").isEmpty()) {
            val urlString = "https://www.tokushima-u.ac.jp/"
            webView?.loadUrl(urlString)
        }else{
            val urlString = "https://eweb.stud.tokushima-u.ac.jp/Portal/"
            webView?.loadUrl(urlString)
        }
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

                val cAccount = encryptedLoad("KEY_cAccount")
                val password = encryptedLoad("KEY_password")

                when {

                    // 大学サービスにログイン完了後、どのページを読み込むか
                    isFinishLogin(urlString) -> {
                        // フラグを立てる
                        DataManager.isExecuteJavascript = true
                        // 初期設定画面のURLを読み込む
                        for (menuList in Constant.menuLists) {
                            // ユーザーが指定した初期画面を探す
                            if (menuList.isInitView) {
                                // メニューリストの内1つだけ、isInitView=trueが存在する
                                val urlString = menuList.url.toString()
                                webView?.loadUrl(urlString)
                            }
                        }
                    }

                    // JavaScriptを実行するフラグが立っていない場合は抜ける
                    !DataManager.isExecuteJavascript -> {

                    }

                    // 大学サイト、ログイン画面 && JavaScriptを動かしcアカウント、パスワードを自動入力する必要があるのか判定
                    urlString.startsWith("https://localidp.ait230.tokushima-u.ac.jp/idp/profile/SAML2/Redirect/SSO?execution=") -> {
                        webView?.evaluateJavascript("document.getElementById('username').value= '" + "$cAccount" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('password').value= '" + "$password" + "'", null)
                        webView?.evaluateJavascript("document.getElementsByClassName('form-element form-button')[0].click();", null)
                        // フラグを下ろす
                        DataManager.isExecuteJavascript = false
                    }

                    // シラバス
                    urlString == "http://eweb.stud.tokushima-u.ac.jp/Portal/Public/Syllabus/" -> {
                        // シラバスの検索画面
                        // ネイティブでの検索内容をWebに反映したのち、検索を行う
                        webView?.evaluateJavascript("document.getElementById('ctl00_phContents_txt_sbj_Search').value= '" + "$subjectName" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('ctl00_phContents_txt_staff_Search').value= '" + "$teacherName" + "'", null)
                        webView?.evaluateJavascript("document.getElementById('ctl00_phContents_ctl06_btnSearch').click();", null)

                        subjectName = "" // 初期化
                        teacherName = ""
                        // フラグを下ろす
                        DataManager.isExecuteJavascript = false
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
                        DataManager.isExecuteJavascript = false
                    }

                    // キャリア支援室
                    urlString.startsWith("https://www.tokudai-syusyoku.com/index.php") -> {
                        // 徳島大学キャリアセンター室
                        // 自動入力を行う(cアカウントは同じ、パスワードは異なる可能性あり)
                        // ログインボタンは自動にしない(キャリアセンターと大学パスワードは人によるが同じではないから)
                        webView?.evaluateJavascript("document.getElementsByName('user_id')[0].value= '" + "$cAccount" + "'", null)
                        webView?.evaluateJavascript("document.getElementsByName('user_password')[0].value= '" + "$password" + "'", null)
                        // フラグを下ろす
                        DataManager.isExecuteJavascript = false
                    }

                    else -> {

                    }
                }
            }
        }
    }

    /// 初回ログイン後すぐか判定
    /// - Parameter urlString: 現在表示しているURL
    /// - Returns: 判定結果
    private fun isFinishLogin(urlString: String): Boolean {

        // アンケート催促画面が出た == ログイン後すぐ
        if (urlString == "https://eweb.stud.tokushima-u.ac.jp/Portal/StudentApp/TopEnqCheck.aspx") {
            isInitFinishLogin = false
            return true
        }

        // モバイル画面かつisInitFinishLoginがtrue つまり　アンケート催促が出ず(アンケート全て完了してるユーザー)そのままモバイル画面へ遷移した人
        if (urlString.startsWith("https://eweb.stud.tokushima-u.ac.jp/Portal/StudentApp/") && isInitFinishLogin) {
            isInitFinishLogin = false
            return true
        }

        return false
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