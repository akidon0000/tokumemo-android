package com.tokudai0000.tokumemo.ui.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tokudai0000.tokumemo.Constant
import com.tokudai0000.tokumemo.Menu
import com.tokudai0000.tokumemo.MenuLists
import com.tokudai0000.tokumemo.R
import java.util.*

class MenuActivity : AppCompatActivity() {

    var menuLists = arrayListOf<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // findViewById
        var listView = findViewById<ListView>(R.id.listView)

        // Action
        listView?.setOnItemClickListener { parent, view, position, id ->
            val menuID = menuLists[position].id.toString()
            var menuUrl = menuLists[position].url
            if (menuID == MenuLists.currentTermPerformance.toString()) {
                // 2020年4月〜2021年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2020
                // 2021年4月〜2022年3月までの成績は https ... Results_Get_YearTerm.aspx?year=2021

                // 現在時刻の取得
                // Dateを作成すると現在日時が入るし、CalenderをgetInstanceでも現在日時が入る
                val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN);
                var year: Int = calendar.get(Calendar.YEAR)
                val month: Int = calendar.get(Calendar.MONTH) + 1 // 1月が0、12月が11であることに注意

                // 1月から3月までは前年の成績
                if (month <= 3) {
                    year -= 1
                }
                menuUrl = "https://eweb.stud.tokushima-u.ac.jp/Portal/StudentApp/Sp/ReferResults/SubDetail/Results_Get_YearTerm.aspx?year=" + "${year.toString()}"
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