package com.baimsg.buildsrc.fog

import com.android.build.api.instrumentation.*
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter

/**
 * Create by Baimsg on 2022/7/27
 *
 **/

interface StringFogParams : InstrumentationParameters {
    @get:Input
    val writeToStdout: Property<Boolean>
}

abstract class StringFogAsmClassVisitorFactory : AsmClassVisitorFactory<StringFogParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        println("classContext.currentClassData.className  =>  " + classContext.currentClassData.className)
        return if (parameters.get().writeToStdout.get()) {
            StringFogClassVisitor(nextClassVisitor)
        } else {
            TraceClassVisitor(nextClassVisitor, PrintWriter(File("trace_out")))
        }
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className.startsWith("com.baimsg")
    }
}