package com.cbi.app.trs.data.cache

import android.content.Context
import com.cbi.app.trs.data.cache.serializer.Serializer
import com.cbi.app.trs.data.entities.PaymentCacheData
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentCache
@Inject constructor(val context: Context, private val serializer: Serializer, private val fileManager: FileManager) {
    private var data: PaymentCacheData? = null
    private val fileDir = "payment"
    private val filename: String = "$fileDir${File.separator}PaymentCacheData"

    fun get(): PaymentCacheData? {
        val file = File("${context.cacheDir}${File.separator}$filename")
        if (data == null) data = serializer.deserialize(fileManager.readFileContent(file), PaymentCacheData::class.java)
        if (data == null) data = PaymentCacheData()
        return data
    }

    fun put(data: PaymentCacheData) {
        this.data = data
        val fileDir = File("${context.cacheDir}${File.separator}$fileDir")
        if (!fileDir.exists()) fileDir.mkdir()
        val file = File("${context.cacheDir}${File.separator}$filename")
        fileManager.writeToFile(file, serializer.serialize(data, PaymentCacheData::class.java))
    }

    fun update(data: PaymentCacheData.PurchaseInfo?) {
        put(get()?.apply { purchase = data }!!)
    }

    fun clear() {
        this.data = null
        val file = File("${context.cacheDir}${File.separator}$fileDir")
        fileManager.clearDirectory(file)
    }
}