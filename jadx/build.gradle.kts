import com.baimsg.build.Dep

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Dep.compileSdk

    defaultConfig {
        minSdk = Dep.minSdk
    }


    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Dep.javaVersion
        targetCompatibility = Dep.javaVersion
    }
    kotlinOptions {
        jvmTarget = Dep.kotlinJvmTarget
    }
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
    coreLibraryDesugaring(Dep.Libs.desugar)
}