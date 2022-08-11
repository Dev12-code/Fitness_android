package com.cbi.app.trs.data.cache

import android.content.Context
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.data.entities.UserData
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataCache
@Inject constructor(
    val context: Context,
    private val serializer: Serializer,
    private val fileManager: FileManager
) {
    private var userData: UserData? = null
    private val fileDir = "user"
    private val filename: String = "$fileDir${java.io.File.separator}UserData"

    fun get(): UserData? {
        val file = File("${context.cacheDir}${File.separator}$filename")
        if (userData == null) userData =
            serializer.deserialize(fileManager.readFileContent(file), UserData::class.java)
        if (userData == null) userData = UserData()
        return userData
    }

    fun put(userData: UserData) {
        this.userData = userData
        val fileDir = File("${context.cacheDir}${File.separator}$fileDir")
        if (!fileDir.exists()) fileDir.mkdir()
        val file = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(file, serializer.serialize(userData, UserData::class.java))
    }

    fun clear() {
        this.userData = null
        val file = File("${context.cacheDir}${File.separator}$fileDir")
        fileManager.clearDirectory(file)
    }
}