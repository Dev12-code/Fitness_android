package com.cbi.app.trs.features.fragments.workout_sport.detail

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemPremiumClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.item_workout_related_video.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class WorkoutsRelatedVideo @Inject constructor(var userDataCache: UserDataCache) : RecyclerView.Adapter<WorkoutsRelatedVideo.MayAsYouLikeViewHolder>() {

    var onItemClickListener: OnItemPremiumClickListener? = null

    var onLoadmoreListener: OnLoadmoreListener? = null

    var isShowPrePost = false

    var itemSize = (Resources.getSystem().displayMetrics.widthPixels - 50.dp2px) / 2

    var itemImageSizeWidth = (Resources.getSystem().displayMetrics.widthPixels * 35) / 100

    internal var collection: ArrayList<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }


    private var isShowCoverView: Boolean = false

    fun setShowCoverView(value: Boolean) {
        this.isShowCoverView = value
        notifyDataSetChanged()
    }

    inner class MayAsYouLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            (itemView.relate_video_image.layoutParams as ConstraintLayout.LayoutParams).width = itemImageSizeWidth
            //follow ratio in zeplin
            (itemView.relate_video_image.layoutParams as ConstraintLayout.LayoutParams).height = (itemImageSizeWidth * 75) / 134
            (itemView.bg_premium.layoutParams as ConstraintLayout.LayoutParams).height = (itemImageSizeWidth * 75) / 134
        }

        fun bind(position: Int) {
//            when (position % 2) {
//                0 -> {
//                    (itemView.workout_item.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.START
//                }
//                else -> {
//                    (itemView.workout_item.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.END
//                }
//            }

            itemView.relate_video_image.loadFromUrl(collection[position].image_thumbnail, isAnimation = false, isPlaceHolder = true)
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

            if (isShowCoverView) {
                itemView.bg_premium.visibility = View.VISIBLE
            } else {
                itemView.bg_premium.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClickListener?.onItemPremiumClick(collection[position], position)
            }


            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MayAsYouLikeViewHolder {
        return MayAsYouLikeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_workout_related_video, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: MayAsYouLikeViewHolder, position: Int) {
        holder.bind(position)
    }
}