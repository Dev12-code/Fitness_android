package com.cbi.app.trs.data.cache

import java.io.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Helper class to do operations on regular files/directories.
 */
@Singleton
class FileManager @Inject constructor() {
    /**
     * Writes a file to Disk.
     * This is an I/O operation and this method executes in the main thread, so it is recommended to
     * perform this operation using another thread.
     *
     * @param file The file to write to Disk.
     */
    fun writeToFile(file: File, fileContent: String?) {
        try {
            val writer = FileWriter(file)
            writer.write(fileContent)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Reads a content from a file.
     * This is an I/O operation and this method executes in the main thread, so it is recommended to
     * perform the operation using another thread.
     *
     * @param file The file to read from.
     * @return A string with the content of the file.
     */
    fun readFileContent(file: File): String {
        val fileContentBuilder = StringBuilder()
        if (file.exists()) {
            var stringLine: String?
            try {
                val fileReader = FileReader(file)
                val bufferedReader = BufferedReader(fileReader)
                while (bufferedReader.readLine().also { stringLine = it } != null) {
                    fileContentBuilder.append(stringLine).append("\n")
                }
                bufferedReader.close()
                fileReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return fileContentBuilder.toString()
    }

    /**
     * Returns a boolean indicating whether this file can be found on the underlying file system.
     *
     * @param file The file to check existence.
     * @return true if this file exists, false otherwise.
     */
    fun exists(file: File): Boolean {
        return file.exists()
    }

    /**
     * Warning: Deletes the content of a directory.
     * This is an I/O operation and this method executes in the main thread, so it is recommended to
     * perform the operation using another thread.
     *
     * @param directory The directory which its content will be deleted.
     */
    fun clearDirectory(directory: File): Boolean {
        var result = false
        if (directory.exists()) {
            for (file in directory.listFiles()) {
                result = file.delete()
            }
        }
        return result
    }
}