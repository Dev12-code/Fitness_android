package com.cbi.app.trs.core.platform

import com.cbi.app.trs.data.entities.MovieData

interface OnDownloadClickListener {
    fun onDownloadClick(item: MovieData, position: Int)
}