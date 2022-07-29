import com.baimsg.build.Dep

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = Dep.compileSdk

    defaultConfig {
        minSdk = Dep.minSdk

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

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    //Kotlin
    api(Dep.Kotlin.stdlib)
    api(Dep.Kotlin.Serialization.json)
    api(Dep.Kotlin.coroutinesCore)
    //hilt
    api(Dep.Hilt.library)
    kapt(Dep.Hilt.compiler)
    //retrofit
    api(Dep.Retrofit.library)
    api(Dep.Retrofit.kotlinSerializerConverter)
    //okHttp
    api(Dep.OkHttp.library)
    api(Dep.OkHttp.loggingInterceptor)
    //Rom
    api(Dep.AndroidX.Room.common)
    api(Dep.AndroidX.Room.runtime)
    api(Dep.AndroidX.Room.ktx)
    api(Dep.AndroidX.Room.paging)
    kapt(Dep.AndroidX.Room.compiler)

    api(Dep.AndroidX.documentFile)
    api(Dep.AndroidX.dataStore)

    api(Dep.Libs.mmkv)
    api(Dep.Libs.PinYin.library)
    api(Dep.Libs.PinYin.android)
    coreLibraryDesugaring(Dep.Libs.desugar)
    api(project(":bytecode-fog-ext"))
}