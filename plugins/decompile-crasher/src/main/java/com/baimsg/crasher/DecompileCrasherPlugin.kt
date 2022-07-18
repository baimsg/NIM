package com.baimsg.crasher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import java.io.File

class DecompileCrasherPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.apply()
    }
}

fun Project.apply() {
    apply(mapOf("plugin" to "android"))

    val jar = tasks.getByName("jar") as Jar
    val build = tasks.getByName("build")

    val obfuscationJar = tasks.create("obfuscationJar", ObfuscationTask::class.java).apply {
        jarTask = jar
        destinationDir = File(buildDir, "libs")
        dependsOn(jar)
    }

    build.dependsOn(obfuscationJar)
}