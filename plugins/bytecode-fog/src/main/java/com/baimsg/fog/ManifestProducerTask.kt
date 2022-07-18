package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/18
 *
 **/
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ManifestProducerTask: DefaultTask() {
    @get:InputFile
    abstract val gitInfoFile: RegularFileProperty

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val gitVersion = gitInfoFile.get().asFile.readText()
        var manifest = mergedManifest.asFile.get().readText()
        manifest = manifest.replace("android:versionCode=\"1\"", "android:versionCode=\"${gitVersion}\"")
        println("Writes to " + updatedManifest.get().asFile.absolutePath)
        updatedManifest.get().asFile.writeText(manifest)
    }
}