package com.cbi.app.trs.data.cache

import android.content.Context
import android.net.Uri
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.data.entities.DownloadedMovieData
import com.cbi.app.trs.features.fragments.movies.DownloadTracker
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadedMovieCache
@Inject constructor(val context: Context, private val serializer: Serializer, private val fileManager: FileManager, private val downloadTracker: DownloadTracker) {
    private var downloadedMovieData: DownloadedMovieData? = null
    private val filename: String = "downloaded_movie"

    fun get(): DownloadedMovieData {
        val file = File("${context.cacheDir}${File.separator}$filename")
        if (downloadedMovieData == null) downloadedMovieData = serializer.deserialize(fileManager.readFileContent(file), DownloadedMovieData::class.java)
        if (downloadedMovieData == null) downloadedMovieData = DownloadedMovieData()
        return downloadedMovieData!!
    }

    fun put(downloadedMovieData: DownloadedMovieData) {
        this.downloadedMovieData = downloadedMovieData
        val file = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(file, serializer.serialize(downloadedMovieData, DownloadedMovieData::class.java))
    }

    fun clear() {
        for (item in get().list) {
            try {
                downloadTracker.removeDownload(Uri.parse(item.video_play_url))
            } catch (e: Exception) {
            }
        }
        put(DownloadedMovieData())
    }
}