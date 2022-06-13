import com.baimsg.build.Dep
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {

    compileSdk = Dep.compileSdk

    signingConfigs {
        val properties = Properties()
        val propFile = project.file("../keystore.properties")
        if (propFile.exists()) {
            properties.load(propFile.inputStream())
        }
        create("release") {
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
            storeFile = file(properties.getProperty("storeFile"))
            storePassword = properties.getProperty("storePassword")
            enableV2Signing = true
            enableV1Signing = true
        }
    }

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = Dep.packageName
        minSdk = Dep.minSdk
        targetSdk = Dep.targetSdk
        versionCode = 1
        versionName = Dep.version

        ndk {
            abiFilters.apply {
                add("armeabi-v7a")
                add("arm64-v8a")
            }
        }
    }

    buildTypes {
        getByName("release") {
            // 启用代码压缩、优化和混淆（由R8或者ProGuard执行）
            isMinifyEnabled = true
            // 启用资源压缩（由Android Gradle plugin执行）
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = Dep.javaVersion
        targetCompatibility = Dep.javaVersion
    }

    kotlinOptions {
        jvmTarget = Dep.kotlinJvmTarget
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":base"))
    implementation(project(":base-android"))

}