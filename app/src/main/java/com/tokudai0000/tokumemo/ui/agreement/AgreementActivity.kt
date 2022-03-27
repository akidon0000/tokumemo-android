package com.tokudai0000.tokumemo.ui.agreement

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.R
import com.tokudai0000.tokumemo.ui.main.MainModel
import com.tokudai0000.tokumemo.ui.menu.MenuActivity

class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)

        initActivitySetup()

    }

    // Private func
    /// AgreementActivityの初期セットアップ
    private fun initActivitySetup() {
        // Outlet

        val textView = findViewById<TextView>(R.id.agreementTextView)
        val termsOfServiceButton = findViewById<Button>(R.id.termsOfServiceButton)
        val privacyPolicyButton = findViewById<Button>(R.id.privacyPolicyButton)
        val agreementButton = findViewById<Button>(R.id.agreementButton)

        // Action

        termsOfServiceButton.setOnClickListener {
            val urlString = "https://github.com/tokudai0000/document/blob/main/tokumemo/terms/TermsOfService.txt"
            // Chromeで開く
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            startActivity(intent)
        }

        privacyPolicyButton.setOnClickListener {
            val urlString = "https://github.com/tokudai0000/document/blob/main/tokumemo/terms/PrivacyPolicy.txt"
            // Chromeで開く
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            startActivity(intent)
        }

        agreementButton.setOnClickListener {
            getSharedPreferences("latestTermsVersion", Context.MODE_PRIVATE).edit().apply {
                putString("version", Constant.latestTermsVersion)
                commit()
            }
            finish()
        }
        textView.setAutoLinkMask(Linkify.ALL)
        textView.text = "トクメモのご利用規約またはプライバシーポリシーが更新されています。\n" +
                "サービスを継続してご利用するには、新しいご利用規約とプライバシーポリシーに同意する必要があります。\n" +
                "\n" +
                "最終改定日\n" +
                "2022年3月22日(火)\n" +
                "\n" +
                "変更点\n" +
                "https://github.com/tokudai0000/document/commit/660ab5db89994d1f52c3ed47df597a8513bc12ca"
    }

}