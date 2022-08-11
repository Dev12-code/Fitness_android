package com.cbi.app.trs.features.fragments.search

import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.MovieData
import kotlinx.android.synthetic.main.search_result_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class SearchResultAdapter @Inject constructor() :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    var itemSize = (Resources.getSystem().displayMetrics.widthPixels - 50.dp2px) / 2

    var onItemClickListener: OnItemClickListener? = null

    var onLoadmoreListener: OnLoadmoreListener? = null

    var onDeleteClickListener: OnDeleteClickListener? = null

    var isRemovable = false

    internal var collection: ArrayList<MovieData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: SearchResultAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView) {
            (itemView.search_result_image_wrap.layoutParams).height = itemSize
            (itemView.search_result_image_wrap.layoutParams).width = itemSize
        }

        fun bind(position: Int) {
            if (isRemovable) {
                itemView.remove_btn.visibility = View.VISIBLE
                itemView.remove_btn.setOnClickListener {
                    onDeleteClickListener?.onDeleteItem(
                        collection[position]
                    )
                }
            }
            itemView.search_result_item.setOnClickListener {
                onItemClickListener?.onItemClick(
                    collection?.get(position),
                    position
                )
            }

            when (position % 2) {
                0 -> {
                    (itemView.search_result_item.layoutParams as FrameLayout.LayoutParams).gravity =
                        Gravity.START
                }
                else -> {
                    (itemView.search_result_item.layoutParams as FrameLayout.LayoutParams).gravity =
                        Gravity.END
                }
            }

            collection.get(position).let {
                itemView.search_result_image.loadFromUrl(it.image_thumbnail, isPlaceHolder = true)
                itemView.video_title.text = it.video_title
                //                itemView.video_views.text = "${it.view_count}"
                itemView.video_duration.text = "${it.video_duration / 60}"
            }

            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }
        }
    }


    interface OnDeleteClickListener {
        fun onDeleteItem(item: MovieData)
    }
}
