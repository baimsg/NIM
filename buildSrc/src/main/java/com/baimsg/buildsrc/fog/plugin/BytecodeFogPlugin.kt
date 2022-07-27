package com.baimsg.buildsrc.fog.plugin

import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter

class BytecodeFogPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.apply {
            onVariants(selector().withName("debug")) { variant ->
                variant.instrumentation.transformClassesWith(
                    ExampleClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) {
                    it.writeToStdout.set(true)
                }
                variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }

    }


    interface ExampleParams : InstrumentationParameters {
        @get:Input
        val writeToStdout: Property<Boolean>
    }

    abstract class ExampleClassVisitorFactory : AsmClassVisitorFactory<ExampleParams> {

        override fun createClassVisitor(
            classContext: ClassContext,
            nextClassVisitor: ClassVisitor
        ): ClassVisitor {
            println("classContext.currentClassData.className  =>  " + classContext.currentClassData.className)
            return if (parameters.get().writeToStdout.get()) {
                com.baimsg.buildsrc.fog.StringFogClassVisitor(nextClassVisitor)
            } else {
                TraceClassVisitor(nextClassVisitor, PrintWriter(File("trace_out")))
            }
        }

        override fun isInstrumentable(classData: ClassData): Boolean {
            return classData.className.startsWith("com.baimsg")
        }
    }


}
