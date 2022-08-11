package com.cbi.app.trs.features.fragments.progress.achievement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.data.entities.AchievementData
import kotlinx.android.synthetic.main.achievement_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class AchievementAdapter @Inject constructor() : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    internal var collection: List<AchievementData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = collection[position]
            if (item.achievement_is_active) {
                itemView.achievement_image.loadFromUrl(item.achievement_active_image, false, isPlaceHolder = true)
                itemView.achievement_status.text = "Unlocked"
            } else {
                itemView.achievement_image.loadFromUrl(item.achievement_inactive_image, false, isPlaceHolder = true)
                itemView.achievement_status.text = String.format("%d-days streak", item.achievement_milestone)
            }

            itemView.achievement_txv.text = collection[position].achievement_title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.achievement_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}