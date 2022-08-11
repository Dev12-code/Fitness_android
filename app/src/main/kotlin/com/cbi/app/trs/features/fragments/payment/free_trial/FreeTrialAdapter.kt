package com.cbi.app.trs.features.fragments.payment.free_trial

import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.data.entities.ReviewData
import kotlinx.android.synthetic.main.free_trial_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class FreeTrialAdapter @Inject constructor() : PagerAdapter() {
    internal var collection: List<ReviewData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, item: Any): Boolean {
        return view == item
    }

    override fun getCount(): Int {
        return collection.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val layout = LayoutInflater.from(container.context).inflate(R.layout.free_trial_item, container, false)
        layout.reviewer_avatar.loadFromUrl(collection[position].reviewer_avatar, false, isPlaceHolder = true)
        layout.reviewer_name.text = collection[position].reviewer_name
        layout.reviewer_content.text = "\"${Html.fromHtml(collection[position].review_content)}\""
        layout.reviewer_member_since.text = "Member since ${collection[position].reviewer_member_since}"
        layout.reviewer_content.movementMethod = ScrollingMovementMethod()
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        container.removeView(item as View)
    }
}