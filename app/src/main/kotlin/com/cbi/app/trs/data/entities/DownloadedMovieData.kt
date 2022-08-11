package com.cbi.app.trs.data.entities

import android.net.Uri

data class DownloadedMovieData(var list: ArrayList<MovieData> = ArrayList()) {
    fun removeMovie(movieData: MovieData) {
        for (i in list) {
            if (i.video_id == movieData.video_id) {
                list.remove(i)
                break
            }
        }
    }

    fun removeMovie(uri: Uri) {
        for (i in list) {
            if (i.video_play_url == uri.toString()) {
                list.remove(i)
                break
            }
        }
    }

    fun addOrUpdateMovie(movieData: MovieData) {
        removeMovie(movieData)
        list.add(movieData)
    }

    fun updateDownloadDateMovie(uri: Uri, downloadedDate: Long) {
        for (i in list) {
            if (i.video_play_url == uri.toString()) {
                i.downloadedDate = downloadedDate
            }
        }
    }

    fun updateDownloadStatusMovie(uri: Uri) {
        for (i in list) {
            if (i.video_play_url == uri.toString()) {
                i.isDownloaded = true
            }
        }
    }

    fun getDownloadedList(): ArrayList<MovieData> {
        val result = ArrayList<MovieData>()
        for (i in list) {
            if (i.isDownloaded) result.add(i)
        }
        return result
    }


    fun isMovieDownloaded(movieData: MovieData): Boolean {
        for (i in list) {
            if (i.video_id == movieData.video_id) return i.isDownloaded
        }
        return false
    }
}