package com.cbi.app.trs.data.entities

data class WorkoutFilterData(var equipment_ids: ArrayList<Int> = ArrayList(), var focus_areas: ArrayList<Int> = ArrayList(),
                             var duration: Int = -1, var pre_post_filter: Int = 0) {
}