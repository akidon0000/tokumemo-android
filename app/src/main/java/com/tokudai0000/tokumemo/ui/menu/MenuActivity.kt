package com.tokudai0000.tokumemo.ui.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.Menu
import com.tokudai0000.tokumemo.R

class MenuActivity : AppCompatActivity() {

    var menuLists = arrayListOf<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // findViewById
        var listView = findViewById<ListView>(R.id.listView)

        // Action
        listView?.setOnItemClickListener { parent, view, position, id ->
            // 親(MainActivity)にどのセルがタップされたのかを伝える
            val intent = Intent()
            intent.putExtra("MenuID_KEY", menuLists[position].id.toString())
            intent.putExtra("MenuUrl_KEY", menuLists[position].url)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // ListViewに表示する内容をセット
        for (lists in Constant.menuLists) {
            if (lists.isDisplay) {
                menuLists.add(lists)
            }
        }
        listView?.adapter = ListAdapter(this, menuLists)
    }

    // メニュー外をタップされた時、MainActivityに戻る
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        finish()
        return false
    }

}