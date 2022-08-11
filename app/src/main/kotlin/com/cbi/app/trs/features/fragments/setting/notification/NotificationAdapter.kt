package com.cbi.app.trs.features.fragments.setting.notification

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.getMonthDate
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.NotificationData
import kotlinx.android.synthetic.main.notification_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class NotificationAdapter @Inject constructor() : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var onLoadmoreListener: OnLoadmoreListener? = null
    internal var collection: ArrayList<NotificationData> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.setOnClickListener { onItemClickListener?.onItemClick(collection[position], position) }
            collection[position].let {
                if (it.notification_is_readed) itemView.notification_item_selected.visibility = VISIBLE else itemView.notification_item_selected.visibility = GONE
                if (it.notification_type == "new_video") {
                    itemView.notification_avatar.setImageResource(R.drawable.notification_new_video)
                } else {
                    itemView.notification_avatar.setImageResource(R.drawable.notification_achievement)
                }
                itemView.notification_name.text = it.notification_name
                itemView.notification_content.text = it.notification_description
                itemView.notification_date.text = it.notification_date.getMonthDate()
            }
            if (position == itemCount - 1) {
                onLoadmoreListener?.onLoadMore()
                onLoadmoreListener = null
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}