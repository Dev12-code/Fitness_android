package com.cbi.app.trs.features.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromLocal
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.HomeWorkoutSpotsData
import kotlinx.android.synthetic.main.item_home_workouts_spots.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class HomeWorkoutSpotsAdapter @Inject constructor() : RecyclerView.Adapter<HomeWorkoutSpotsAdapter.HomeWorkoutSpotsViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    internal var collection: List<HomeWorkoutSpotsData>? by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class HomeWorkoutSpotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection?.get(position), position) }
            collection?.get(position)?.let {
                itemView.new_image.loadFromLocal(it.imageId, false, isPlaceHolder = true)
                itemView.title.text = it.title
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeWorkoutSpotsViewHolder {
        return HomeWorkoutSpotsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_workouts_spots, parent, false))
    }

    override fun getItemCount(): Int {
        collection?.let { return it.size }
        return 0
    }

    override fun onBindViewHolder(holder: HomeWorkoutSpotsViewHolder, position: Int) {
        holder.bind(position)
    }
}