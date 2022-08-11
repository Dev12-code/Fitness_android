package com.cbi.app.trs.features.fragments.progress.leaderboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.loadFromUrl
import com.cbi.app.trs.data.entities.UserRank
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import kotlinx.android.synthetic.main.leaderboard_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class LeaderBoardAdapter @Inject constructor() : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    internal var collection: List<UserRank> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var yourUserName: String = ""

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.leaderboard_index.text = "${collection[position].user_rank_index}"
            itemView.leaderboard_image.loadFromUrl(collection[position].user_avatar, false, isPlaceHolder = true)
            itemView.leadboard_name.text = collection[position].username
            itemView.leaderboard_badge.text = collection[position].user_badge
            itemView.leaderboard_score.text = collection[position].user_point

            if (yourUserName.isNotBlank() && yourUserName == collection[position].username) {
                itemView.setBackgroundColor(Color.parseColor("#ebe8ff"))
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}