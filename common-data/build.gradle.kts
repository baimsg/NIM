import com.baimsg.build.Dep

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = Dep.compileSdk

    defaultConfig {
        minSdk = Dep.minSdk

        multiDexEnabled = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Dep.javaVersion
        targetCompatibility = Dep.javaVersion
    }

    kotlinOptions {
        jvmTarget = Dep.kotlinJvmTarget

        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"

    }
    namespace = "com.baimsg.data"

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":base"))
    implementation(project(":base-android"))

    implementation(Dep.Hilt.library)
    //hilt
    kapt(Dep.Hilt.compiler)
    //Room
    kapt(Dep.AndroidX.Room.compiler)
    coreLibraryDesugaring(Dep.Libs.desugar)
}