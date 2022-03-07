package com.tokudai0000.tokumemo.ui.menu.Syllabus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.R

class SyllabusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_syllabus)

        // findViewById
        val backButton = findViewById<Button>(R.id.backButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        val subjectTextField = findViewById<EditText>(R.id.subjectTextField)
        val subjectTextSizeLabel = findViewById<TextView>(R.id.subjectTextSizeLabel)
        val subjectMessageLabel = findViewById<TextView>(R.id.subjectMessageLabel)

        val teacherTextField = findViewById<EditText>(R.id.teacherTextField)
        val teacherTextSizeLabel = findViewById<TextView>(R.id.teacherTextSizeLabel)
        val teacherMessageLabel = findViewById<TextView>(R.id.teacherMessageLabel)

        // Action
        backButton.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            val subjectText = subjectTextField.text.toString()
            val teacherText = teacherTextField.text.toString()

            subjectMessageLabel.text = "" // 初期値に戻す
            teacherMessageLabel.text = ""

            when {
                // 入力値が正常なデータか検証

                subjectText.length > 20 -> {
                    subjectMessageLabel.text = "エラー"
                }

                teacherText.length > 20 -> {
                    teacherMessageLabel.text = "エラー"
                }

                else -> {
                    val intent = Intent()
                    intent.putExtra("Subject_KEY", subjectText)
                    intent.putExtra("Teacher_KEY", teacherText)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }

        // 入力文字数のカウント、そして表示を行う
        subjectTextField.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            // テキストが変更された直後(入力が確定された後)に呼び出される
            override fun afterTextChanged(s: Editable?) {
                subjectTextSizeLabel.text = "${subjectTextField.text.toString().length}/20"
            }
        })
        teacherTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                teacherTextSizeLabel.text = "${teacherTextField.text.toString().length}/20"
            }
        })
    }

}