package com.cbi.app.trs.features.fragments.progress.histories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.histories_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class HistoriesAdapter @Inject constructor() : RecyclerView.Adapter<HistoriesAdapter.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    var onLoadmoreListener: OnLoadmoreListener? = null

    internal var collection: ArrayList<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }
            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }
            itemView.video_image.loadFromUrl(collection[position].image_thumbnail, isPlaceHolder = true)
            itemView.video_title.text = collection[position].video_title
            itemView.video_count.text = "${collection[position].view_count}"
            itemView.video_duration.text = "${collection[position].video_duration / 60}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.histories_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}