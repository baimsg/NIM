package com.baimsg.fog

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Create by Baimsg on 2022/7/25
 * 字符串加密注入类
 * @param fogPackages 需要字符串加密的包名集合
 * @param kg 密钥生成器
 * @param implementation 加密实现的类路径
 * @param fogClassName
 * @param mappingPrinter 映射文件输出对象
 **/
class StringFogClassInjector(
    private val fogPackages: Array<String>,
    private val kg: IKeyGenerator,
    implementation: String,
    private val fogClassName: String,
    private val mappingPrinter: StringFogMappingPrinter
) {
    private val mStringFogImpl: IStringFog

    init {
        mStringFogImpl = StringFogWrapper(implementation)
    }

    @Throws(IOException::class)
    fun doFog2Class(fileIn: File, fileOut: File) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = BufferedInputStream(FileInputStream(fileIn))
            outputStream = BufferedOutputStream(FileOutputStream(fileOut))
            processClass(inputStream, outputStream)
        } finally {
            closeQuietly(outputStream)
            closeQuietly(inputStream)
        }
    }

    @Throws(IOException::class)
    fun doFog2Jar(jarIn: File, jarOut: File) {
        val shouldExclude = shouldExcludeJar(jarIn)
        var zis: ZipInputStream? = null
        var zos: ZipOutputStream? = null
        try {
            zis = ZipInputStream(BufferedInputStream(FileInputStream(jarIn)))
            zos = ZipOutputStream(BufferedOutputStream(FileOutputStream(jarOut)))
            var entryIn: ZipEntry
            val processedEntryNamesMap: MutableMap<String, Int> = HashMap()
            while (zis.nextEntry.also { entryIn = it } != null) {
                val entryName = entryIn.name
                if (!processedEntryNamesMap.containsKey(entryName)) {
                    val entryOut = ZipEntry(entryIn)
                    // Set compress method to default, fixed #12
                    if (entryOut.method != ZipEntry.DEFLATED) {
                        entryOut.method = ZipEntry.DEFLATED
                    }
                    entryOut.compressedSize = -1
                    zos.putNextEntry(entryOut)
                    if (!entryIn.isDirectory) {
                        if (entryName.endsWith(".class") && !shouldExclude) {
                            processClass(zis, zos)
                        } else {
                            copy(zis, zos)
                        }
                    }
                    zos.closeEntry()
                    processedEntryNamesMap[entryName] = 1
                }
            }
        } finally {
            closeQuietly(zos)
            closeQuietly(zis)
        }
    }

    @Throws(IOException::class)
    private fun processClass(classIn: InputStream, classOut: OutputStream) {
        val cr = ClassReader(classIn)
        // skip module-info class, fixed #38
        if ("module-info" == cr.className) {
            val buffer = ByteArray(1024)
            var read: Int
            while (classIn.read(buffer).also { read = it } >= 0) {
                classOut.write(buffer, 0, read)
            }
        } else {
            val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
            val cv = ClassVisitorFactory.create(
                mStringFogImpl, mappingPrinter, fogPackages,
                kg, fogClassName, cr.className, cw
            )
            cr.accept(cv, 0)
            classOut.write(cw.toByteArray())
            classOut.flush()
        }
    }

    @Throws(IOException::class)
    private fun shouldExcludeJar(jarIn: File): Boolean {
        var zis: ZipInputStream? = null
        try {
            zis = ZipInputStream(BufferedInputStream(FileInputStream(jarIn)))
            var entryIn: ZipEntry
            while (zis.nextEntry.also { entryIn = it } != null) {
                val entryName = entryIn.name
                if (entryName.contains("StringFog")) {
                    return true
                }
            }
        } finally {
            closeQuietly(zis)
        }
        return false
    }

    private fun closeQuietly(target: Closeable?) {
        if (target != null) {
            try {
                target.close()
            } catch (e: Exception) {
                // Ignored.
            }
        }
    }

    @Throws(IOException::class)
    private fun copy(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(8192)
        var c: Int
        while (`in`.read(buffer).also { c = it } != -1) {
            out.write(buffer, 0, c)
        }
    }


}
