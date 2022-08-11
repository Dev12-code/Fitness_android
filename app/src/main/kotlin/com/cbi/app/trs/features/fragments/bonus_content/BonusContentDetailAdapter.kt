package com.cbi.app.trs.features.fragments.bonus_content

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.mini_qa_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class BonusContentDetailAdapter @Inject constructor() : RecyclerView.Adapter<BonusContentDetailAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }

            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }

            itemView.video_image.loadFromUrl(collection[position].image_thumbnail, isPlaceHolder = true)
            itemView.video_title.text = collection[position].video_title
            itemView.video_duration.text = DateUtils.formatElapsedTime(collection[position].video_duration * 1L)
            if (collection[position].is_playing) itemView.item_playing.visibility = View.VISIBLE else itemView.item_playing.visibility = View.GONE
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    var onLoadmoreListener: OnLoadmoreListener? = null

    internal var collection: ArrayList<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mini_qa_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}