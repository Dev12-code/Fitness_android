package com.cbi.app.trs.features.fragments.intro

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.data.entities.MovieData
import kotlin.properties.Delegates

class IntroListAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    internal var collection: List<MovieData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getCount(): Int = collection.size

    override fun getItem(position: Int): BaseFragment = IntroDetailFragment().apply {
        movieData = collection[position]
        this.position = position
        this.sizeList = collection.size
    }
}