package com.tokudai0000.tokumemo.menu.password

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tokudai0000.tokumemo.R
import javax.crypto.EncryptedPrivateKeyInfo

class PasswordActivity : AppCompatActivity() {
    companion object {
        const val PREF_NAME = "encrypted_prefs"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // "DataStore"という名前でインスタンスを生成
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
//        val dataStore1: EncryptedPrivateKeyInfoSharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

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
//            finish()
            // "Input"から読み出す
//            val str = dataStore.getString("Input", "NoData")
//
//            passwordMessageLabel.text = str

            val mainKey = MasterKey.Builder(applicationContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            val prefs = EncryptedSharedPreferences.create(
                    applicationContext,
                    PREF_NAME,
                    mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            with (prefs.edit()) {
                putString("foo", "bar")
                apply()
            }

            val savedValue = prefs.getString("foo", "Nothing")
            Toast.makeText(this, """the saved value of the key "foo" is $savedValue""", Toast.LENGTH_LONG).show()

        }

        registerButton.setOnClickListener { v: View? ->
//            cAccountTextField.text
            // 文字列を"Input"に書き込む
            val editor = dataStore.edit()
            editor.putString("Input", "文字列")

            //editor.commit();
            editor.apply()
        }

    }

}