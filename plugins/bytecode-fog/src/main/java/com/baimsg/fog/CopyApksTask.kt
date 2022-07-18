package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/18
 *
 **/
import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.variant.BuiltArtifact
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.io.Serializable
import javax.inject.Inject


interface WorkItemParameters: WorkParameters, Serializable {
    val inputApkFile: RegularFileProperty
    val outputApkFile: RegularFileProperty
}

abstract class WorkItem @Inject constructor(private val workItemParameters: WorkItemParameters)
    : WorkAction<WorkItemParameters> {
    override fun execute() {
        workItemParameters.outputApkFile.get().asFile.delete()
        workItemParameters.inputApkFile.asFile.get().copyTo(
            workItemParameters.outputApkFile.get().asFile)
    }
}
abstract class CopyApksTask @Inject constructor(private val workers: WorkerExecutor): DefaultTask() {

    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val outFolder: DirectoryProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<CopyApksTask>>

    @TaskAction
    fun taskAction() {

        transformationRequest.get().submit(
            this,
            workers.noIsolation(),
            WorkItem::class.java) {
                builtArtifact: BuiltArtifact,
                outputLocation: Directory,
                param: WorkItemParameters ->
            val inputFile = File(builtArtifact.outputFile)
            param.inputApkFile.set(inputFile)
            param.outputApkFile.set(File(outputLocation.asFile, inputFile.name))
            param.outputApkFile.get().asFile
        }
    }
}