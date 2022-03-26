package com.tokudai0000.tokumemo.ui.agreement

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    }

}