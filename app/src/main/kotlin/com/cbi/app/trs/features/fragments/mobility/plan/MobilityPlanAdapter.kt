package com.cbi.app.trs.features.fragments.mobility.plan

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.MovieData
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.mobility_practice_item.view.*
import kotlinx.android.synthetic.main.mobility_video_item.view.*


class MobilityPlanAdapter : ExpandableRecyclerViewAdapter<MobilityPlanAdapter.PracticeHolder, MobilityPlanAdapter.VideoHolder>, GroupExpandCollapseListener {
    var onItemClickListener: OnItemClickListener? = null

    constructor(groups: List<MobilityPractice>?) : super(groups) {
        setOnGroupExpandCollapseListener(this)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): PracticeHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.mobility_practice_item, parent, false)
        return PracticeHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.mobility_video_item, parent, false)
        return VideoHolder(view)
    }

    override fun onBindChildViewHolder(holder: VideoHolder, flatPosition: Int, group: ExpandableGroup<*>,
                                       childIndex: Int) {
        holder.bind(group.items[childIndex] as MovieData)
    }

    override fun onBindGroupViewHolder(holder: PracticeHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>?) {
        holder.bind(group as MobilityPractice)
    }

    inner class PracticeHolder(itemView: View) : GroupViewHolder(itemView) {
        fun bind(practice: MobilityPractice) {
            itemView.practice_title.text = practice.title
            itemView.practice_image.setImageResource(practice.imageRes)
//            itemView.practice_count.text = "${practice.items.size}"
            if (isGroupExpanded(practice))
                itemView.expand_btn.setImageResource(R.drawable.ic_arrow_drop_up_blue)
            else
                itemView.expand_btn.setImageResource(R.drawable.ic_arrow_drop_down_blue)
        }
    }

    inner class VideoHolder(itemView: View) : ChildViewHolder(itemView) {
        fun bind(movieDetails: MovieData) {
            itemView.video_title.text = movieDetails.video_title
            itemView.video_image.loadFromUrl(movieDetails.image_thumbnail, isPlaceHolder = true)
            itemView.video_duration.text = DateUtils.formatElapsedTime(movieDetails.video_duration * 1L)
            itemView.setOnClickListener { onItemClickListener?.onItemClick(movieDetails, 0) }
        }
    }

    override fun onGroupCollapsed(group: ExpandableGroup<*>?) {
        notifyDataSetChanged()
    }

    override fun onGroupExpanded(group: ExpandableGroup<*>?) {
        notifyDataSetChanged()
    }
}