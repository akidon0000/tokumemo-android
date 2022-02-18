package com.tokudai0000.tokumemo.menu.password

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.R
import com.tokudai0000.tokumemo.menu.MenuActivity

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // Outlet
        val backButton = findViewById<Button>(R.id.backButton)

        // Action
        backButton.setOnClickListener { v: View? -> finish() }
    }

}