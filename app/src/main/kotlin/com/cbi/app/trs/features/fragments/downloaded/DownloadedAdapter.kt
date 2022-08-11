package com.cbi.app.trs.features.fragments.downloaded

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.getMonthYearDate
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.features.fragments.search.SearchResultAdapter
import kotlinx.android.synthetic.main.downloaded_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class DownloadedAdapter @Inject constructor() : RecyclerView.Adapter<DownloadedAdapter.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    internal var collection: ArrayList<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onDeleteClickListener: SearchResultAdapter.OnDeleteClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }
            itemView.video_title.text = collection[position].video_title
            itemView.video_image.loadFromUrl(collection[position].image_thumbnail, isPlaceHolder = true)
            itemView.video_at.text = collection[position].downloadedDate.getMonthYearDate()
            itemView.remove_btn.setOnClickListener { onDeleteClickListener?.onDeleteItem(collection[position]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.downloaded_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}