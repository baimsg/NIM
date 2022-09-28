import com.baimsg.build.Dep

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = Dep.compileSdk

    defaultConfig {
        minSdk = Dep.minSdk
        targetSdk = Dep.targetSdk
    }


    compileOptions {
        sourceCompatibility = Dep.javaVersion
        targetCompatibility = Dep.javaVersion
    }
    kotlinOptions {
        jvmTarget = Dep.kotlinJvmTarget
    }
    namespace = "com.qmuiteam.qmui"
}


dependencies {
    api(Dep.AndroidX.appcompat)
    api(Dep.AndroidX.annotation)
    api(Dep.AndroidX.ConstraintLayout.library)
    api(Dep.AndroidX.swipeRefreshLayout)
    api(Dep.MaterialDesign.material)
}