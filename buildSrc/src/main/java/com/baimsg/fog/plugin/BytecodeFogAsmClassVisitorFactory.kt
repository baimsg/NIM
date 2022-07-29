package com.baimsg.fog.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.baimsg.fog.BytecodeFogWrapper
import com.baimsg.fog.IKeyGenerator
import com.baimsg.fog.WhiteLists
import com.baimsg.fog.util.Log
import com.baimsg.fog.util.extension.isInFogPackages
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import java.io.File

/**
 * Create by Baimsg on 2022/7/27
 *
 **/

interface BytecodeFogParams : InstrumentationParameters {
    @get:Input
    val fogPackages: ListProperty<String>

    @get:Input
    val implementation: Property<String>

    @get:Input
    val ignoreFogClassName: Property<String>

    @get:Input
    val kg: Property<IKeyGenerator>

    @get:Input
    val enable: Property<Boolean>

    @get:Input
    val mappingFile: Property<File>
}

abstract class BytecodeFogAsmClassVisitorFactory : AsmClassVisitorFactory<BytecodeFogParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        parameters.get().apply {
            return BytecodeFogClassVisitor(
                implementation = implementation.get(),
                ignoreFogClassName=ignoreFogClassName.get(),
                mBytecodeFogImpl = BytecodeFogWrapper(implementation.get()),
                mappingFile = mappingFile.get(),
                cv = nextClassVisitor,
                mKeyGenerator = kg.get(),
            )
        }

    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        return when {
            !parameters.get().enable.get() -> {
                false
            }
            WhiteLists.inWhiteList(className) || !parameters.get().fogPackages.get()
                .isInFogPackages(className) -> {
                Log.v("bytecodeFog ignore => $className")
                false
            }
            else -> {
                Log.v("bytecodeFog execute => $className")
                true
            }
        }
    }
}