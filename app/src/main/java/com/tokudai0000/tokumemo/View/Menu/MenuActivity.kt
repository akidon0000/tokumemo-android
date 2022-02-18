package com.tokudai0000.tokumemo.View.Menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.R

class MenuActivity : AppCompatActivity() {

    private var listView: ListView? = null
//    var delegate: MainActivity? = null

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

        //アダプターにユーザーリストを導入
        val adapter = ListAdapter(this, Constant.menuLists)
        // ListViewにadapterをセット
        listView?.adapter = adapter
        // クイックリスナー
        listView?.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra("MenuID_KEY", Constant.menuLists[position].id.toString())
            intent.putExtra("MenuUrl_KEY", Constant.menuLists[position].url)
            setResult(Activity.RESULT_OK, intent)
//            delegate!!.test()

            finish()
        }
    }

}