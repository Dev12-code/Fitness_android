package com.cbi.app.trs.features.fragments.pain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.cbi.app.trs.R
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.SystemData
import kotlinx.android.synthetic.main.pain_body_layout.view.*
import javax.inject.Inject

class PainBodyAdapter @Inject constructor(var userDataCache: UserDataCache) : PagerAdapter() {

    var onPainItemClickListener: OnItemClickListener? = null
    var backList: ArrayList<SystemData.PainArea> = ArrayList()
    var frontList: ArrayList<SystemData.PainArea> = ArrayList()

    override fun isViewFromObject(view: View, item: Any): Boolean {
        return view == item
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val layout = LayoutInflater.from(container.context).inflate(R.layout.pain_body_layout, container, false)
        container.addView(layout)
        if (position == 0) {
            layout.body_pain.addBaseImage(R.drawable.full_body_front)
            layout.body_pain.addBodyParts(frontList)
            if (!isAllowForPremium()) {
                layout.body_pain.addHighlightImage(R.drawable.quads)
                layout.body_pain.addHighlightImage(R.drawable.head)
            }
        } else {
            layout.body_pain.addBaseImage(R.drawable.full_body_back)
            layout.body_pain.addBodyParts(backList)
            if (!isAllowForPremium()) {
                layout.body_pain.addHighlightImage(R.drawable.elbow)
                layout.body_pain.addHighlightImage(R.drawable.back_head)
            }
        }
        layout.body_pain.onItemClickListener = this.onPainItemClickListener

        return layout
    }

    private fun isAllowForPremium(): Boolean {
        if (userDataCache.get()?.user_profile == null) {
            return false
        }
        if (userDataCache.get()!!.user_profile!!.isFreeUser()) {
            return false
        }
        return true
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        (item as View).body_pain.onDestroy()
        container.removeView(item as View)
    }
}