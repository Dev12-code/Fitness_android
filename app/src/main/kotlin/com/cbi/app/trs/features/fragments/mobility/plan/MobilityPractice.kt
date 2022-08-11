package com.cbi.app.trs.features.fragments.mobility.plan

import com.cbi.app.trs.data.entities.MovieData
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class MobilityPractice(title: String, val imageRes: Int, items: List<MovieData>) : ExpandableGroup<MovieData>(title, items) {
}