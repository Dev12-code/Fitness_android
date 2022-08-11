package com.cbi.app.trs.data.cache

import android.content.Context
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.domain.entities.activity.AchievementEntity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementBadgeCache
@Inject constructor(val context: Context, private val serializer: Serializer, private val fileManager: FileManager) {
    private var data: AchievementEntity.Data? = null
    private val fileDir = "achievement"
    private val filename: String = "$fileDir${java.io.File.separator}AchievementBadgeCache"

    fun get(): AchievementEntity.Data? {
        val file = File("${context.cacheDir}${File.separator}$filename")
        if (data == null) data = serializer.deserialize(fileManager.readFileContent(file), AchievementEntity.Data::class.java)
        if (data == null) data = AchievementEntity.Data(0, ArrayList())
        return data
    }

    fun put(data: AchievementEntity.Data) {
        this.data = data
        val fileDir = File("${context.cacheDir}${File.separator}$fileDir")
        if (!fileDir.exists()) fileDir.mkdir()
        val file = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(file, serializer.serialize(data, AchievementEntity.Data::class.java))
    }

    fun clear() {
        this.data = null
        val file = File("${context.cacheDir}${File.separator}$fileDir")
        fileManager.clearDirectory(file)
    }
}