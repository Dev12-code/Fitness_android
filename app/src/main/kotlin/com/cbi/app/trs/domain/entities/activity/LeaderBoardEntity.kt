package com.cbi.app.trs.domain.entities.activity

import com.cbi.app.trs.data.entities.LeaderBoardData
import com.cbi.app.trs.data.entities.UserRank
import com.cbi.app.trs.domain.entities.BaseEntities

data class LeaderBoardEntity(val data: LeaderBoardData = LeaderBoardData(UserRank(), ArrayList())) : BaseEntities()