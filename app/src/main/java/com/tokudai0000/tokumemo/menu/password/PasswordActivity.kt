package com.tokudai0000.tokumemo.menu.password

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.R

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // "DataStore"という名前でインスタンスを生成
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)


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
            val str = dataStore.getString("Input", "NoData")

            passwordMessageLabel.text = str
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