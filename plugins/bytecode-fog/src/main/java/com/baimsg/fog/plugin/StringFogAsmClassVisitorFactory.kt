package com.baimsg.fog.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.baimsg.fog.*
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
interface StringFogParams : InstrumentationParameters {
    @get:Input
    val fogPackages: ListProperty<String>

    @get:Input
    val fogClassName: Property<String>

    @get:Input
    val kg: Property<IKeyGenerator>

    @get:Input
    val stringFogImpl: Property<IStringFog>
}

abstract class StringFogAsmClassVisitorFactory : AsmClassVisitorFactory<StringFogParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val file =
            File("/Users/yueranzhishang/AndroidStudioProjects/nim/base/build/testMapping.txt")
        file.mkdirs()

        return StringFogClassVisitor(
            mStringFogImpl = parameters.get().stringFogImpl.get(),
            mMappingPrinter = StringFogMappingPrinter(file).apply { startMappingOutput(parameters.get().fogClassName.get()) },
            fogClassName = parameters.get().fogClassName.get(),
            cv = nextClassVisitor,
            mKeyGenerator = parameters.get().kg.get(),
        )
    }


    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        return if (WhiteLists.inWhiteList(className) || !parameters.get().fogPackages.get()
                .isInFogPackages(
                    className
                )
        ) {
            Log.v("StringFog ignore: $className")
            false
        } else {
            Log.v("StringFog execute: $className")
            true
        }
    }
}