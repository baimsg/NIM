import com.baimsg.build.Dep

plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = Dep.compileSdk

    defaultConfig {
        minSdk = Dep.minSdk

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Dep.javaVersion
        targetCompatibility = Dep.javaVersion
    }
    kotlinOptions {
        jvmTarget = Dep.kotlinJvmTarget
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    api(Dep.AndroidX.coreKtx)
    api(Dep.AndroidX.appcompat)
    api(Dep.AndroidX.annotation)
    api(Dep.MaterialDesign.material)

    //协程
    api(Dep.Kotlin.coroutinesAndroid)

    //ViewModel
    api(Dep.AndroidX.LifeCycle.vmKtx)
    api(Dep.AndroidX.LifeCycle.liveDate)
    api(Dep.AndroidX.LifeCycle.vmSavedState)
    api(Dep.AndroidX.LifeCycle.runtimeKtx)

    api(Dep.AndroidX.splashscreen)
    api(Dep.AndroidX.media)
    api(Dep.AndroidX.palette)

    implementation(Dep.Hilt.library)
    //hilt
    kapt(Dep.Hilt.compiler)

    api(Dep.AndroidX.multiDex)

}