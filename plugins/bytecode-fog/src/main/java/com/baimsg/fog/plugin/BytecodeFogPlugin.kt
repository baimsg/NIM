package com.baimsg.fog.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.baimsg.fog.kg.RandomKeyGenerator
import com.baimsg.fog.xor.StringFogImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

class BytecodeFogPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.apply {
            onVariants(selector().withName("debug")) { variant ->
                variant.instrumentation.transformClassesWith(
                    StringFogAsmClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) {
                    it.fogClassName.set(StringFogImpl::class.simpleName)
                    it.fogPackages.set(listOf("com.baimsg"))
                    it.kg.set(RandomKeyGenerator())
                    it.stringFogImpl.set(StringFogImpl())
                }
                variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }

    }


}
