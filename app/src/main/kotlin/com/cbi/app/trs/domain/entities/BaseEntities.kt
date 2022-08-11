package com.cbi.app.trs.domain.entities

open class BaseEntities {
    val isSuccess = false
    val errorCode = 200
    val message = ""

    companion object {
        fun empty() = BaseEntities()
    }

}