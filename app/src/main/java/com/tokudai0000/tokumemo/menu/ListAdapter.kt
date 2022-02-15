package com.tokudai0000.tokumemo.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.tokudai0000.tokumemo.Menu
import com.tokudai0000.tokumemo.R
import java.util.*

class ListAdapter (val context: Context, val ArrayLists: ArrayList<Menu>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_menu, null)
        val menuTitle = view.findViewById<TextView>(R.id.menu_title)
        val user = ArrayLists[position]
        menuTitle.text = user.title
        return view
    }

    override fun getItem(position: Int): Any {
        return ArrayLists[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return ArrayLists.size
    }
}