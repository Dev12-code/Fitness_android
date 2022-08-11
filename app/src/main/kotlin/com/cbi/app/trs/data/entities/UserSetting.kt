package com.cbi.app.trs.data.entities

data class UserSetting(var my_equipments: List<SystemData.Equipment>? = null, val my_focus_areas: List<SystemData.Area>? = null)