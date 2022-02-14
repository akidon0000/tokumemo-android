package com.tokudai0000.tokumemo.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.R

class MenuActivity : AppCompatActivity() {

    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setupListView()
    }

    // メニュー外をタップされた時、MainActivityに戻る
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        finish()
        return false
    }

    private fun setupListView() {
        listView = findViewById<ListView>(R.id.listView)

        val menuListsTitle = arrayOf(
                "教務事務システム",
                "マナバ",
                "図書貸し出し延長",
                "パスワード",
                "このアプリについて",
                "図書Web[常三島]"
        )

        // simple_list_item_1 は、 もともと用意されている定義済みのレイアウトファイルのID
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, menuListsTitle)
        // ListViewにadapterをセット
        listView?.adapter = arrayAdapter
        // クイックリスナー
        listView?.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, menuListsTitle[position], Toast.LENGTH_SHORT).show()
        }
    }

}