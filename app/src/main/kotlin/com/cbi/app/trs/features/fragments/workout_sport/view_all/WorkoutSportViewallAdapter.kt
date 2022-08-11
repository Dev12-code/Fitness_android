package com.cbi.app.trs.features.fragments.workout_sport.view_all

import android.content.res.Resources
import android.os.Parcel
import android.os.Parcelable
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
import com.cbi.app.trs.data.entities.CategoryData
import kotlinx.android.synthetic.main.workout_sport_viewall_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class WorkoutSportViewallAdapter @Inject constructor() : RecyclerView.Adapter<WorkoutSportViewallAdapter.ViewHolder>(), Parcelable {
    var onItemClickListener: OnItemClickListener? = null

    var itemSize = (Resources.getSystem().displayMetrics.widthPixels - 50.dp2px) / 2

    internal var collection: List<CategoryData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    constructor(parcel: Parcel) : this() {
        itemSize = parcel.readInt()
    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView) {
            (itemView.workout_viewall_item.layoutParams as FrameLayout.LayoutParams).height = itemSize
            (itemView.workout_viewall_item.layoutParams as FrameLayout.LayoutParams).width = itemSize
        }

        fun bind(position: Int) {
            when (position % 2) {
                0 -> {
                    (itemView.workout_viewall_item.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.START
                }
                else -> {
                    (itemView.workout_viewall_item.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.END
                }
            }
            itemView.workout_viewall_item.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }
            itemView.category_image.loadFromUrl(collection[position].category_thumbnail, isPlaceHolder = false)
            itemView.category_title.text = collection[position].category_title
            itemView.video_count.text = "${collection[position].video_count}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.workout_sport_viewall_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(itemSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkoutSportViewallAdapter> {
        override fun createFromParcel(parcel: Parcel): WorkoutSportViewallAdapter {
            return WorkoutSportViewallAdapter(parcel)
        }

        override fun newArray(size: Int): Array<WorkoutSportViewallAdapter?> {
            return arrayOfNulls(size)
        }
    }
}