package com.cbi.app.trs.features.fragments.workout_sport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.features.utils.AppConstants
import kotlinx.android.synthetic.main.workout_sport_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class WorkoutSportAdapter @Inject constructor() : RecyclerView.Adapter<WorkoutSportAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    internal var collection: List<CategoryData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var isShowCoverView: Boolean = false

    private var type: String? = null

    fun setShowCoverView(value: Boolean) {
        this.isShowCoverView = value
        notifyDataSetChanged()
    }

    fun setType(value: String) {
        this.type = value
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 0.dp2px
            if (adapterPosition == 0) {
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 20.dp2px
            }
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }
            itemView.category_title.text = collection[position].category_title
            itemView.category_image.loadFromUrl(collection[position].category_thumbnail, isPlaceHolder = true)
            itemView.category_count.text = "${collection[position].video_count}"

            if (isShowCoverView) {
                itemView.premium_view.visibility = View.VISIBLE
                //apply for case Air Squat and Back Squat
                if (type == AppConstants.WORKOUTS && collection[position].category_title.contains("Squat")) {
                    itemView.premium_view.visibility = View.GONE
                }
            } else {
                itemView.premium_view.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.workout_sport_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}