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
        viewBinding = true
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
    api(Dep.AndroidX.swipeRefreshLayout)
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

    //navigation
    api(Dep.AndroidX.Navigation.fragmentKtx)
    api(Dep.AndroidX.Navigation.uiKtx)

    implementation(Dep.Hilt.library)
    //hilt
    kapt(Dep.Hilt.compiler)

    api(Dep.AndroidX.multiDex)
    api(Dep.Libs.glide)
    api(Dep.Libs.baseAdapter)
    api(Dep.Libs.toasty)
    api(Dep.Libs.Dialog.core)
    api(Dep.Libs.Dialog.input)
    api(Dep.Libs.Dialog.datetime)
    api(Dep.Libs.Dialog.color)
    api(Dep.Libs.Dialog.files)
    api(Dep.Libs.Dialog.bottomSheets)
    api(Dep.Libs.Dialog.lifecycle)
}