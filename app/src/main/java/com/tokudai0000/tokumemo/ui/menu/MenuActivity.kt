package com.tokudai0000.tokumemo.ui.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.Menu
import com.tokudai0000.tokumemo.MenuLists
import com.tokudai0000.tokumemo.R
import com.tokudai0000.tokumemo.ui.main.MainModel
import java.util.*

class MenuActivity : AppCompatActivity() {

    private lateinit var viewModel: MenuModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initActivitySetup()
    }

    // MARK: - Private func
    /// MenuActivityの初期セットアップ
    private fun initActivitySetup() {
        // Outlet
        viewModel = ViewModelProviders.of(this).get(MenuModel::class.java)

        // findViewById
        var listView = findViewById<ListView>(R.id.listView)

        // Action
        // メニューのセルがタップされたら
        listView?.setOnItemClickListener { parent, view, position, id ->
            // タップされたセルの内容
            val menuID = viewModel.menuLists[position].id.toString()
            var menuUrl = viewModel.menuLists[position].url

            if (menuID == MenuLists.currentTermPerformance.toString()) {
                // 今期の成績をタップされた場合、URLを作成する
                menuUrl = viewModel.createCurrentTermPerformanceUrl()
            }
            // 親(MainActivity)にどのセルがタップされたのかを伝える
            val intent = Intent()
            intent.putExtra("MenuID_KEY", menuID)
            intent.putExtra("MenuUrl_KEY", menuUrl)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // ListViewに表示する内容をセット
        for (lists in Constant.menuLists) {
            if (lists.isDisplay) {
                viewModel.menuLists.add(lists)
            }
        }
        listView?.adapter = ListAdapter(this, viewModel.menuLists)
    }

    // メニュー外をタップされた時、MainActivityに戻る
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        finish()
        return false
    }

}