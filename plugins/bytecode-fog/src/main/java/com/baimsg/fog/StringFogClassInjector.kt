package com.baimsg.fog

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Create by Baimsg on 2022/7/25
 *
 **/
class StringFogClassInjector(
    fogPackages: Array<String>, kg: IKeyGenerator, implementation: String,
    fogClassName: String, mappingPrinter: StringFogMappingPrinter
) {
    private val mFogPackages: Array<String>
    private val mFogClassName: String
    private val mKeyGenerator: IKeyGenerator
    private val mStringFogImpl: IStringFog
    private val mMappingPrinter: StringFogMappingPrinter

    @Throws(IOException::class)
    fun doFog2Class(fileIn: File, fileOut: File) {
        var `is`: InputStream? = null
        var os: OutputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(fileIn))
            os = BufferedOutputStream(FileOutputStream(fileOut))
            processClass(`is`, os)
        } finally {
            closeQuietly(os)
            closeQuietly(`is`)
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
                mStringFogImpl, mMappingPrinter, mFogPackages,
                mKeyGenerator, mFogClassName, cr.className, cw
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

    init {
        mFogPackages = fogPackages
        mKeyGenerator = kg
        mStringFogImpl = StringFogWrapper(implementation)
        mFogClassName = fogClassName
        mMappingPrinter = mappingPrinter
    }
}
