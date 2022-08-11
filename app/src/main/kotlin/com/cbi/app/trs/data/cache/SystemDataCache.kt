package com.cbi.app.trs.data.cache

import android.content.Context
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.data.entities.SystemData
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemDataCache
@Inject constructor(val context: Context, private val serializer: Serializer, private val fileManager: FileManager) {
    private var systemData: SystemData? = null
    private val filename: String = "SystemData"

    fun get(): SystemData {
        val systemDataFile = File("${context.cacheDir}${File.separator}$filename")
        if (systemData == null) systemData = serializer.deserialize(fileManager.readFileContent(systemDataFile), SystemData::class.java)
        if (systemData == null) systemData = SystemData(null, null, null, null, null, null, null, null, null, null)
        return systemData as SystemData
    }

    fun put(systemData: SystemData) {
        this.systemData = systemData
        val systemDataFile = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(systemDataFile, serializer.serialize(systemData, SystemData::class.java))
    }
}