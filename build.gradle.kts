import com.baimsg.build.Dep

plugins {
    id("plugin-dep")
//    id("decompile-crasher") version "1.0.0"
    kotlin("plugin.serialization") version "1.6.21" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.21")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.41")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2")
    }
}

subprojects {
    group = Dep.group
}

allprojects {
    repositories {
        maven { setUrl("https://plugins.gradle.org/m2/") }
        maven { setUrl("https://jitpack.io") }
        mavenCentral()
        google()
        mavenLocal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
