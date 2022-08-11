package com.cbi.app.trs.features.fragments.movies.pain_detail

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnDownloadClickListener
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.cache.DownloadedMovieCache
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.pain_detail_movie_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class PainDetailAdapter @Inject constructor() : RecyclerView.Adapter<PainDetailAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
//
//    var onLoadmoreListener: OnLoadmoreListener? = null

    var onDownloadClickListener: OnDownloadClickListener? = null

    @Inject
    lateinit var downloadedMovieCache: DownloadedMovieCache

    internal var collection: List<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            itemView.pain_detail_item_playing.visibility = View.GONE
            itemView.download_btn.visibility = View.VISIBLE
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }
//
//            if (position == itemCount - 1) {
//                onLoadmoreListener?.onLoadMore()
//                onLoadmoreListener = null
//            }
            collection[position].let { it1 ->
                itemView.video_title.text = it1.video_title
                itemView.pain_detail_item_image.loadFromUrl(it1.image_thumbnail, isPlaceHolder = true)
                if (it1.is_playing) itemView.pain_detail_item_playing.visibility = View.VISIBLE
                itemView.video_duration.text = DateUtils.formatElapsedTime(it1.video_duration * 1L)
                itemView.download_btn.setOnClickListener { onDownloadClickListener?.onDownloadClick(it1, position) }
                if (downloadedMovieCache.get().isMovieDownloaded(it1)) itemView.download_btn.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pain_detail_movie_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}