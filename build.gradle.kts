import com.baimsg.build.Dep

plugins {
    id("plugin-dep")
    kotlin("plugin.serialization") version "1.7.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.android.application") version "7.3.0" apply false
    id("com.android.library") version "7.3.0" apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://plugins.gradle.org/m2/") }
        maven { setUrl("https://jitpack.io") }
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.7.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.43.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.2")
    }
}

subprojects {
    group = Dep.group
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://plugins.gradle.org/m2/") }
        maven { setUrl("https://jitpack.io") }
        mavenLocal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
