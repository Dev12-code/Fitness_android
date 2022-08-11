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
import com.cbi.app.trs.features.utils.AppConstants
import kotlinx.android.synthetic.main.feature_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class FeatureAdapter @Inject constructor() : RecyclerView.Adapter<FeatureAdapter.MayAsYouLikeViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    private var isShowCoverView: Boolean = false

    internal var collection: List<MovieData>? by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

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
            val item = collection?.get(position)
            item?.let {
                if (it.video_id == AppConstants.VIDEO_SEE_MORE_ID) {
                    itemView.see_more.visibility = View.VISIBLE
                    itemView.setOnClickListener { onItemClickListener?.onItemClick(null, AppConstants.VIDEO_SEE_MORE_ID) }
                } else {
                    itemView.see_more.visibility = View.GONE
                    itemView.movie_title.text = it.video_title ?: ""
                    itemView.feature_image.loadFromUrl(it.image_thumbnail
                            ?: "", isPlaceHolder = true)
                    itemView.video_duration.text = "${it.video_duration / 60}"
                    itemView.setOnClickListener { onItemClickListener?.onItemClick(item, position) }
                }

                if (isShowCoverView) {
                    itemView.pain_detail_premium.visibility = View.VISIBLE
                } else {
                    itemView.pain_detail_premium.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayAsYouLikeViewHolder {
        return MayAsYouLikeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.feature_item, parent, false))
    }

    override fun getItemCount(): Int {
        collection?.let {
            return it.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: MayAsYouLikeViewHolder, position: Int) {
        holder.bind(position)
    }
}