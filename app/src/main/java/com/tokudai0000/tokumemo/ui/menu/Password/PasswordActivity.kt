package com.tokudai0000.tokumemo.ui.menu.Password

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tokudai0000.tokumemo.R

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // findViewById
        val backButton = findViewById<Button>(R.id.backButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        val cAccountTextField = findViewById<EditText>(R.id.cAccountTextField)
        val cAccountTextSizeLabel = findViewById<TextView>(R.id.cAccountTextSizeLabel)
        val cAccountMessageLabel = findViewById<TextView>(R.id.cAccountMessageLabel)

        val passwordTextField = findViewById<EditText>(R.id.passwordTextField)
        val passwordTextSizeLabel = findViewById<TextView>(R.id.passwordTextSizeLabel)
        val passwordMessageLabel = findViewById<TextView>(R.id.passwordMessageLabel)

        // Action
        backButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            val cAccountText = cAccountTextField.text.toString()
            val passwordText = passwordTextField.text.toString()

            cAccountMessageLabel.text = "" // 初期値に戻す
            passwordMessageLabel.text = ""

            when {
                // 入力値が正常なデータか検証

                cAccountText.isEmpty() -> {
                    cAccountMessageLabel.text = "空欄です"
                }

                // cアカウントの先頭はcから始まる(isEmptyで検証してる為、エラーが起きない)
                cAccountText.substring(0,1) != "c" -> {
                    cAccountMessageLabel.text = "cアカウント例(c100100100)"
                }

                // cアカウントは10桁(よく@tokushima-u.ac.jpとつけるユーザーがいる為の対策)
                cAccountText.length > 10 -> {
                    cAccountMessageLabel.text = "cアカウント例(c100100100)"
                }

                passwordText.isEmpty() -> {
                    passwordMessageLabel.text = "空欄です"
                }

                else -> {
                    // 登録
                    encryptedSave("KEY_cAccount", cAccountText)
                    encryptedSave("KEY_password", passwordText)
                    // MainActivityで再ログインの処理を行わせる
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }

        // 入力文字数のカウント、そして表示を行う
        cAccountTextField.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            // テキストが変更された直後(入力が確定された後)に呼び出される
            override fun afterTextChanged(s: Editable?) {
                cAccountTextSizeLabel.text = "${cAccountTextField.text.toString().length}/10"
            }
        })
        passwordTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                passwordTextSizeLabel.text = "${passwordTextField.text.toString().length}/20"
            }
        })

        // 保存しているデータを入力フィールドに表示
        cAccountTextField.setText(encryptedLoad("KEY_cAccount"))
        passwordTextField.setText(encryptedLoad("KEY_password"))
    }


    // 以下、暗号化してデバイスに保存する(MainActivityにも存在するので今後、統一)
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
                PasswordActivity.PREF_NAME,
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

}