package com.albertkingdom.loginsignuptest.util

import android.view.View

abstract class DoubleClickListener: View.OnClickListener {
    var lastClickTime: Long = 0
    companion object {
        const val DOUBLE_CLICK_TIME_DELTA = 300
    }
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }
    abstract fun onDoubleClick(v: View?)
}