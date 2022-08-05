package com.baimsg.fog.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.baimsg.fog.util.Log
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class BytecodeFogPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val fogExtension = project.extensions.create("bytecodeFog", BytecodeFogExtension::class.java)

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.apply {
            onVariants(selector().all()) { variant ->
                variant.instrumentation.transformClassesWith(
                    BytecodeFogAsmClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) {
                    Log.isDebug = fogExtension.debug
                    val mappingFile =
                        File(project.buildDir, "outputs/logs/bytecode_fog.txt")
                    if (!mappingFile.exists()) {
                        mappingFile.parentFile.mkdirs()
                    }
                    mappingFile.writeText("")
                    it.mappingFile.set(mappingFile)
                    it.enable.set(fogExtension.enable)
                    it.implementation.set(fogExtension.implementation)
                    it.ignoreFogClassName.set(fogExtension.ignoreFogClassName)
                    it.fogPackages.set(fogExtension.fogPackages)
                    it.kg.set(fogExtension.kg)
                }
                variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }
    }


}
