package com.cbi.app.trs.features.fragments.mobility.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.OnItemClickListener
import kotlinx.android.synthetic.main.mobility_test_item.view.*

class MobilityTestListAdapter : PagerAdapter() {

    var onItemClickListener: OnItemClickListener? = null

    override fun isViewFromObject(view: View, item: Any): Boolean {
        return view == item
    }

    override fun getCount(): Int {
        return 16
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val layout = LayoutInflater.from(container.context).inflate(R.layout.mobility_test_item, container, false)
        container.addView(layout)
        layout.mobility_test_1.setOnClickListener {
            onItemClickListener?.onItemClick(position, 1)
        }
        layout.mobility_test_2.setOnClickListener {
            onItemClickListener?.onItemClick(position, 2)
        }
        layout.mobility_test_3.setOnClickListener {
            onItemClickListener?.onItemClick(position, 3)
        }

        layout.mobility_test_1_text.text = "Question ${position + 1} as point 1"
        layout.mobility_test_2_text.text = "Question ${position + 1} as point 2"
        layout.mobility_test_3_text.text = "Question ${position + 1} as point 3"
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        container.removeView(item as View)
    }
}