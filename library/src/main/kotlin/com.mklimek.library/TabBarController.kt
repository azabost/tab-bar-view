package com.mklimek.library

import android.content.Context
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TabBarController(context: Context, val tabs: List<Tab>, val tabBarView: TabBarView, val separators: Boolean) {

    var tintSelectedColor = 0
    var tintSelectedEnabled = false

    var currentItem = 0
        set(value) {
            field = value
            tabs.filter { tabs.indexOf(it) != value }.forEach {
                var childIndex = tabs.indexOf(it)
                if (separators)
                    childIndex *= 2

                val child = tabBarView.getChildAt(childIndex)

                if (child is TextView) {
                    child.background = it.background
                    if(tintSelectedEnabled){
                        val tv = child as TextView
                        tv.setTextColor(Color.BLACK)
                        it.iconId?.colorFilter = null
                    }
                }
            }
            val selectedTab = tabs.first { tabs.indexOf(it) == value }

            var selectedTabIndex = tabs.indexOf(selectedTab)
            if (separators)
                selectedTabIndex *= 2

            val selectedChild = tabBarView.getChildAt(selectedTabIndex)
            selectedChild.background = selectedTab.backgroundSelected
            if(tintSelectedEnabled){
                val tv = selectedChild as TextView
                tv.setTextColor(tintSelectedColor)
                selectedTab.iconId?.colorFilter = LightingColorFilter(Color.BLACK, tintSelectedColor)
            }
            listener?.pageHasBeenChanged(value)
        }

    private var listener: TabBarListener? = null

    init{
        val inflater = LayoutInflater.from(context)
        for(tab in tabs){
            val textView = inflater.inflate(R.layout.bottom_tab_bar_item, tabBarView, false) as TextView
            textView.text = tab.title
            textView.setTextColor(Color.WHITE)
            textView.background = tab.background
            textView.setCompoundDrawablesWithIntrinsicBounds(null, tab.iconId, null, null)
            textView.setOnClickListener { currentItem = tabs.indexOf(tab) }
            tabBarView.addView(textView)
            if (separators) {
                val separator = View(context)
                separator.layoutParams = ViewGroup.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
                separator.background = ColorDrawable(Color.BLACK)
                tabBarView.addView(separator)
            }
        }
    }

    fun getTabsAsTextViews(): List<TextView>{
        var children = mutableListOf<TextView>()
        tabs.forEach {
            children.add(tabBarView.getChildAt(tabs.indexOf(it)) as TextView)
        }
        return children
    }

    fun setListener(listener: TabBarListener){
        this.listener = listener
    }

    object TabBuilder{

        private var tabs = mutableListOf<Tab>()

        fun addTab(title: String, iconId: Drawable?, backgroundId: Drawable?, backgroundSelectedId: Drawable?): TabBuilder{
            tabs.add(Tab(title, iconId, backgroundId, backgroundSelectedId))
            return this
        }

        fun build(): List<Tab>{
            val result = tabs.toList()
            tabs.clear()
            return result
        }
    }

    class Tab(val title: String, val iconId: Drawable?, val background: Drawable?, val backgroundSelected: Drawable?){
    }

}

