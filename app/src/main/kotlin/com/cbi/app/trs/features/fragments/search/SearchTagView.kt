package com.cbi.app.trs.features.fragments.search

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.OnItemClickListener
import kotlinx.android.synthetic.main.search_tag_item.view.*

class SearchTagView : FrameLayout {

    var onItemClickListener: OnItemClickListener? = null
    var isChecked = false

    fun setText(text: String) {
        search_tag_txv.text = text
        search_tag.setOnClickListener {
            onItemClickListener?.onItemClick(text, 0)
            setCheck(!isChecked)
        }
    }

    fun getEnable(): Boolean {
        return search_tag.isEnabled
    }

    fun setCheck(isChecked: Boolean, isEnabled: Boolean = true) {
        search_tag.isSelected = isChecked
        search_tag.isEnabled = isEnabled
        this.isChecked = isChecked
    }

    init {
        inflate(context, R.layout.search_tag_item, this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}