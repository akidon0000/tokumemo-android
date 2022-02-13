package com.tokudai0000.tokumemo.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val stringExtra = intent.getStringExtra("PARENT_KEY")
//        val stringExtra = intent.getStringExtra("PARENT_KEY")
//
//        val intent = Intent().run {
//            putExtra("CHILD_KEY", "子からのデータです！")
//        }

//        childOKButton = findViewById(R.id.childOKButton)
//        childOKButton.setOnClickListener {
//            setResult(Activity.RESULT_OK, intent)
//        }
//
//        childCanceledButton = findViewById(R.id.childCanceledButton)
//        childCanceledButton.setOnClickListener {
//            setResult(Activity.RESULT_CANCELED, intent)
//        }
    }
}