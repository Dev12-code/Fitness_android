package com.cbi.app.trs.core.platform

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager


class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    var enabledSwipe = true
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (enabledSwipe) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (enabledSwipe) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setSwipeEnabled(enabled: Boolean) {
        this.enabledSwipe = enabled
    }

}