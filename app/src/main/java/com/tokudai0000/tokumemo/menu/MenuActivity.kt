package com.tokudai0000.tokumemo.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import com.tokudai0000.tokumemo.Constant
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

        val menuListsTitle = Constant.menuLists

        //アダプターにユーザーリストを導入
        val adapter = ListAdapter(this, Constant.menuLists)
        
        // ListViewにadapterをセット
        listView?.adapter = adapter
        // クイックリスナー
        listView?.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, Constant.menuLists[position].title, Toast.LENGTH_SHORT).show()
        }
    }

}