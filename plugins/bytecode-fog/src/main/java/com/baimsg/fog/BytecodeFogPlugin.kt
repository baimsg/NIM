package com.baimsg.fog

import com.android.build.api.artifact.SingleArtifact
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

        androidComponents.onVariants { variant ->

            val copyApksProvider =
                project.tasks.register("copy${variant.name}Apks", CopyApksTask::class.java)

            val transformationRequest = variant.artifacts.use(copyApksProvider)
                .wiredWithDirectories(
                    CopyApksTask::apkFolder,
                    CopyApksTask::outFolder
                )
                .toTransformMany(SingleArtifact.APK)

            copyApksProvider.configure {
                this.transformationRequest.set(transformationRequest)
            }
        }

//        val gitVersionProvider =
//            project.tasks.register("gitVersionProvider", GitVersionTask::class.java) {
//                gitVersionOutputFile.set(
//                    File(project.buildDir, "intermediates/gitVersionProvider/output")
//                )
//                outputs.upToDateWhen { false }
//            }
//
//        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
//
//        androidComponents.onVariants { variant ->
//            val manifestProducer =
//                project.tasks.register(
//                    variant.name + "ManifestProducer",
//                    ManifestProducerTask::class.java
//                ) {
//                    gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
//                }
//            variant.artifacts.use(manifestProducer)
//                .wiredWithFiles(
//                    ManifestProducerTask::mergedManifest,
//                    ManifestProducerTask::updatedManifest)
//                .toTransform(SingleArtifact.MERGED_MANIFEST)
//
//            project.tasks.register(variant.name + "Verifier", VerifyManifestTask::class.java) {
//                apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
//                builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
//            }
//        }

//        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
//        androidComponents.onVariants { variant ->
//            variant.instrumentation.transformClassesWith(
//                ExampleClassVisitorFactory::class.java,
//                InstrumentationScope.ALL
//            ) {
//                it.writeToStdout.set(true)
//            }
//            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
//        }
    }

    interface ExampleParams : InstrumentationParameters {
        @get:Input
        val writeToStdout: Property<Boolean>
    }

    abstract class ExampleClassVisitorFactory :
        AsmClassVisitorFactory<ExampleParams> {

        override fun createClassVisitor(
            classContext: ClassContext,
            nextClassVisitor: ClassVisitor
        ): ClassVisitor {
            println("classContext.currentClassData.className  =>  " + classContext.currentClassData.className)
            return if (parameters.get().writeToStdout.get()) {
                TraceClassVisitor(nextClassVisitor, null)
            } else {
                TraceClassVisitor(nextClassVisitor, PrintWriter(File("trace_out")))
            }
        }

        override fun isInstrumentable(classData: ClassData): Boolean {
            println("classData.className   =>  " + classData.className)
            return classData.className.startsWith("com.baimsg")
        }
    }

}
