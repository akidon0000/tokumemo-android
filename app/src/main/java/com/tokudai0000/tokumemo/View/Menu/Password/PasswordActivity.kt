package com.tokudai0000.tokumemo.View.Menu.Password

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tokudai0000.tokumemo.R

class PasswordActivity : AppCompatActivity() {
    companion object {
        const val PREF_NAME = "encrypted_prefs"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // Outlet
        val backButton = findViewById<Button>(R.id.backButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        val cAccountTextField = findViewById<EditText>(R.id.cAccountTextField)
        val cAccountTextSizeLabel = findViewById<TextView>(R.id.cAccountTextSizeLabel)
        val cAccountMessageLabel = findViewById<TextView>(R.id.cAccountMessageLabel)

        val passwordTextField = findViewById<EditText>(R.id.passwordTextField)
        val passwordTextSizeLabel = findViewById<TextView>(R.id.passwordTextSizeLabel)
        val passwordMessageLabel = findViewById<TextView>(R.id.passwordMessageLabel)

        // Action
        backButton.setOnClickListener { v: View? ->
            finish()
        }

        registerButton.setOnClickListener { v: View? ->
            val cAccountText = cAccountTextField.text.toString()
            val passwordText = passwordTextField.text.toString()
            encryptedSave("KEY_cAccount", cAccountText)
            encryptedSave("KEY_password", passwordText)
        }

        cAccountTextField.setText(encryptedLoad("KEY_cAccount"))
        passwordTextField.setText(encryptedLoad("KEY_password"))
    }

    private fun encryptedSave(KEY: String, text: String) {
        val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        val prefs = EncryptedSharedPreferences.create(
                applicationContext,
                PasswordActivity.PREF_NAME,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        with (prefs.edit()) {
            putString(KEY, text)
            apply()
        }
    }

    private fun encryptedLoad(KEY: String): String {
        val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        val prefs = EncryptedSharedPreferences.create(
                applicationContext,
                PasswordActivity.PREF_NAME,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        return prefs.getString(KEY, "")!!
    }

}