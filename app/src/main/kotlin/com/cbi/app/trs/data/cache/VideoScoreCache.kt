package com.cbi.app.trs.data.cache

import android.content.Context
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.data.entities.VideoScore
import com.cbi.app.trs.data.entities.VideoScoreList
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoScoreCache
@Inject constructor(val context: Context, private val serializer: Serializer, private val fileManager: FileManager) {
    private var videoScoreList: VideoScoreList? = null
    private val fileDir = "video"
    private val filename: String = "$fileDir${java.io.File.separator}VideoScoreData"

    fun get(): VideoScoreList? {
        val file = File("${context.cacheDir}${File.separator}$filename")
        if (videoScoreList == null) videoScoreList = serializer.deserialize(fileManager.readFileContent(file), VideoScoreList::class.java)
        if (videoScoreList == null) videoScoreList = VideoScoreList(arrayListOf())
        return videoScoreList
    }

    fun put(videoScoreList: VideoScoreList) {
        this.videoScoreList = videoScoreList
        val fileDir = File("${context.cacheDir}${File.separator}$fileDir")
        if (!fileDir.exists()) fileDir.mkdir()
        val file = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(file, serializer.serialize(videoScoreList, VideoScoreList::class.java))
    }

    fun put(videoScore: VideoScore) {
        val list = get()?.apply {
            this.videos.add(videoScore)
        }
        if (list != null) {
            put(list)
        }
    }

    fun clear() {
        this.videoScoreList = null
        val file = File("${context.cacheDir}${File.separator}$fileDir")
        fileManager.clearDirectory(file)
    }
}