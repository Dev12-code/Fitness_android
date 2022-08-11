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
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.item_workout_related_video.view.*
import kotlinx.android.synthetic.main.relate_video_item.view.pre
import kotlinx.android.synthetic.main.relate_video_item.view.relate_video_image
import kotlinx.android.synthetic.main.relate_video_item.view.video_duration
import kotlinx.android.synthetic.main.relate_video_item.view.video_title
import javax.inject.Inject
import kotlin.properties.Delegates

class RelatedVideoAdapter @Inject constructor() : RecyclerView.Adapter<RelatedVideoAdapter.MayAsYouLikeViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    var onLoadmoreListener: OnLoadmoreListener? = null

    var isShowPrePost = false

    internal var collection: ArrayList<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var isShowCoverView: Boolean = false

    fun setShowCoverView(value: Boolean) {
        this.isShowCoverView = value
        notifyDataSetChanged()
    }


    inner class MayAsYouLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }
            itemView.relate_video_image.loadFromUrl(collection[position].image_thumbnail, isPlaceHolder = true)
            itemView.video_title.text = collection[position].video_title
//            itemView.video_views.text = "${collection[position].view_count}"
            itemView.video_duration.text = "${collection[position].video_duration / 60}"
            if (isShowPrePost) {
                when (collection[position].pre_post_type) {
                    null -> itemView.pre.visibility = View.GONE
                    0 -> itemView.pre.visibility = View.GONE
                    1 -> {
                        itemView.pre.visibility = View.VISIBLE
                        itemView.pre.text = "Pre"
                    }
                    2 -> {
                        itemView.pre.visibility = View.VISIBLE
                        itemView.pre.text = "Post"
                    }
                    3 -> {
                        itemView.pre.visibility = View.VISIBLE
                        itemView.pre.text = "Pre/Post"
                    }
                    else -> itemView.pre.visibility = View.GONE
                }
            }

            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }

            if (isShowCoverView) {
                itemView.bg_premium.visibility = View.VISIBLE
            } else {
                itemView.bg_premium.visibility = View.GONE
            }

            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayAsYouLikeViewHolder {
        return MayAsYouLikeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.relate_video_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: MayAsYouLikeViewHolder, position: Int) {
        holder.bind(position)
    }
}