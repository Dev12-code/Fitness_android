package com.cbi.app.trs.features.fragments.movies.movies_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.UpsellData
import kotlinx.android.synthetic.main.gear_on_rts_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class GearOnRTSAdapter @Inject constructor() : RecyclerView.Adapter<GearOnRTSAdapter.MayAsYouLikeViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    var onLoadmoreListener: OnLoadmoreListener? = null

    internal var collection: ArrayList<UpsellData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class MayAsYouLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }

            collection?.get(position)?.let {
                itemView.gear_on_rts_image.loadFromUrl(it.image_thumbnail)
                itemView.gear_on_rts_title.text = it.product_title
            }
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection?.get(position), position) }


            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayAsYouLikeViewHolder {
        return MayAsYouLikeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gear_on_rts_item, parent, false))
    }

    override fun getItemCount(): Int {
        collection?.let { return it.size }
        return 0
    }

    override fun onBindViewHolder(holder: MayAsYouLikeViewHolder, position: Int) {
        holder.bind(position)
    }
}