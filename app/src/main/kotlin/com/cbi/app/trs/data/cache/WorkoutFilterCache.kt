package com.cbi.app.trs.data.cache

import android.content.Context
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.data.entities.WorkoutFilterData
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutFilterCache
@Inject constructor(val context: Context, private val serializer: Serializer, private val fileManager: FileManager) {
    private var filter: WorkoutFilterData? = null
    private val fileDir = "setting"
    private val filename: String = "$fileDir${java.io.File.separator}workoutFilter"

    fun get(): WorkoutFilterData {
        val file = File("${context.cacheDir}${File.separator}$filename")
        if (filter == null) filter = serializer.deserialize(fileManager.readFileContent(file), WorkoutFilterData::class.java)
        if (filter == null) filter = WorkoutFilterData()
        return filter!!
    }

    fun put(filterData: WorkoutFilterData) {
        this.filter = filterData
        val fileDir = File("${context.cacheDir}${File.separator}$fileDir")
        if (!fileDir.exists()) fileDir.mkdir()
        val file = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(file, serializer.serialize(filter, WorkoutFilterData::class.java))
    }

    fun clear() {
        this.filter = null
        val file = File("${context.cacheDir}${File.separator}$fileDir")
        fileManager.clearDirectory(file)
    }
}