package com.cbi.app.trs.features.fragments.daily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.new_video_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class NewVideoAdapter @Inject constructor() : RecyclerView.Adapter<NewVideoAdapter.MayAsYouLikeViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    internal var collection: List<MovieData>? by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class MayAsYouLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection?.get(position), position) }
            itemView.movie_title.text = collection?.get(position)?.video_title ?: ""
            collection?.get(position)?.let {
                itemView.new_image.loadFromUrl(it.image_thumbnail, isPlaceHolder = true)
                itemView.video_views.text = "${it.view_count}"
                itemView.video_duration.text = "${it.video_duration/60}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayAsYouLikeViewHolder {
        return MayAsYouLikeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.new_video_item, parent, false))
    }

    override fun getItemCount(): Int {
        collection?.let { return it.size }
        return 0
    }

    override fun onBindViewHolder(holder: MayAsYouLikeViewHolder, position: Int) {
        holder.bind(position)
    }
}