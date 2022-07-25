package com.baimsg.fog

import com.google.common.collect.ImmutableSet
import com.squareup.javawriter.JavaWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.lang.model.element.Modifier


/**
 * Create by Baimsg on 2022/7/25
 *
 **/
object StringFogClassGenerator {
    @Throws(IOException::class)
    fun generate(
        outputFile: File,
        packageName: String?,
        className: String?,
        kg: IKeyGenerator?,
        implementation: String
    ) {
        val outputDir = outputFile.parentFile
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw IOException("Can not mkdirs the dir: $outputDir")
        }
        val lastIndexOfDot = implementation.lastIndexOf(".")
        val implementationSimpleClassName =
            if (lastIndexOfDot == -1) implementation else implementation.substring(
                implementation.lastIndexOf(".") + 1
            )
        val javaWriter = JavaWriter(
            FileWriter(outputFile)
        )
        javaWriter.emitPackage(packageName)
        javaWriter.emitEmptyLine()
        javaWriter.emitImports(implementation)
        javaWriter.emitEmptyLine()
        javaWriter.emitJavadoc("Generated code from StringFog gradle plugin. Do not modify!")
        javaWriter.beginType(
            className, "class", ImmutableSet.of<Modifier>(
                Modifier.PUBLIC,
                Modifier.FINAL
            )
        )
        javaWriter.emitField(
            implementationSimpleClassName, "IMPL",
            ImmutableSet.of<Modifier>(
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.FINAL
            ),
            "new $implementationSimpleClassName()"
        )
        javaWriter.emitEmptyLine()
        javaWriter.beginMethod(
            String::class.java.simpleName, "decrypt",
            ImmutableSet.of<Modifier>(Modifier.PUBLIC, Modifier.STATIC),
            ByteArray::class.java.simpleName, "value",
            ByteArray::class.java.simpleName, "key"
        )
        javaWriter.emitStatement("return " + "IMPL.decrypt(value, key)")
        javaWriter.endMethod()
        javaWriter.emitEmptyLine()
        javaWriter.endType()
        javaWriter.close()
    }
}