package com.cbi.app.trs.features.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.SystemData
import kotlinx.android.synthetic.main.item_home_bonus.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class HomeBonusContentAdapter @Inject constructor() : RecyclerView.Adapter<HomeBonusContentAdapter.HomeBonusContentViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    internal var collection: List<SystemData.Bonus>? by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class HomeBonusContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection?.get(position), position) }
            collection?.get(position)?.let {
                itemView.bonus_image.loadFromUrl(it.bonus_image, false, isPlaceHolder = true)
                itemView.bonus_title.text = it.bonus_title
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBonusContentViewHolder {
        return HomeBonusContentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_bonus, parent, false))
    }

    override fun getItemCount(): Int {
        collection?.let { return it.size }
        return 0
    }

    override fun onBindViewHolder(holder: HomeBonusContentViewHolder, position: Int) {
        holder.bind(position)
    }
}