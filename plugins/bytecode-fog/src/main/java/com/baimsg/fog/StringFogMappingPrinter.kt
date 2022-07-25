package com.baimsg.fog

import com.github.javaparser.utils.Log
import org.apache.commons.io.IOUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Create by Baimsg on 2022/7/25
 * 输出 StringFog 映射到文件
 **/
 class StringFogMappingPrinter(private val mMappingFile: File) {
    private var mWriter: BufferedWriter? = null
    private var mCurrentClassName: String? = null

    fun startMappingOutput(implementation: String) {
        try {
            if (mMappingFile.exists() && !mMappingFile.delete()) {
                throw IOException("删除 stringFog 射文件失败")
            }
            val dir = mMappingFile.parentFile
            mWriter = if (dir.exists() || dir.mkdirs()) {
                BufferedWriter(FileWriter(mMappingFile))
            } else {
                throw IOException("Failed to create dir: " + dir.path)
            }
            mWriter?.write("stringFog impl: $implementation")
            mWriter?.newLine()
        } catch (e: IOException) {
            Log.error("创建 stringFog 映射文件失败")
        }
    }

    fun output(className: String, originValue: String, encryptValue: String) {
        if (className.isBlank()) return
        try {
            if (className != mCurrentClassName) {
                mWriter?.newLine()
                mWriter?.write("[$className]")
                mWriter?.newLine()
                mCurrentClassName = className
            }
            mWriter?.write("$originValue -> $encryptValue")
            mWriter?.newLine()
        } catch (e: IOException) {
            // Ignore
        }
    }

    fun endMappingOutput() {
        if (mWriter != null) {
            IOUtils.closeQuietly(mWriter)
        }
    }

}