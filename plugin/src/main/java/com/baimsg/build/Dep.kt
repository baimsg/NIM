package com.baimsg.build

import org.gradle.api.JavaVersion

object Dep {
    val javaVersion = JavaVersion.VERSION_11
    const val kotlinJvmTarget = "11"
    const val kotlinVer = "1.6.21"

    const val compileSdk = 31
    const val minSdk = 21
    const val targetSdk = 30
    const val group = "com.baimsg.chat"
    const val packageName = "com.baimsg.chat"
    const val version = "1.0.0"

    object MaterialDesign {
        const val material = "com.google.android.material:material:1.4.0"
    }

    object Retrofit {
        private const val retrofit = "2.9.0"
        const val library = "com.squareup.retrofit2:retrofit:$retrofit"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$retrofit"
        const val kotlinSerializerConverter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    }

    object OkHttp {
        private const val okhttp = "4.9.3"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$okhttp"
        const val library = "com.squareup.okhttp3:okhttp:$okhttp"
    }

    object AndroidX {
        const val core = "androidx.core:core:1.7.0"
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
        const val appcompat = "androidx.appcompat:appcompat:1.4.1"
        const val annotation = "androidx.annotation:annotation:1.3.0"
        const val splashscreen = "androidx.core:core-splashscreen:1.0.0-beta02"
        const val media = "androidx.media:media:1.6.0"
        const val emoji = "androidx.emoji:emoji:1.1.0"
        const val collection = "androidx.collection:collection-ktx:1.2.0"
        const val browser = "androidx.browser:browser:1.4.0"
        const val palette = "androidx.palette:palette-ktx:1.0.0"
        const val multiDex = "androidx.multidex:multidex:2.0.1"
        const val documentFile = "androidx.documentfile:documentfile:1.1.0-alpha01"
        const val dataStore = "androidx.datastore:datastore-preferences:1.0.0"


        const val exoPlayer = "com.google.android.exoplayer:exoplayer-core:2.15.1"
        const val exoPlayerOkhttp = "com.google.android.exoplayer:extension-okhttp:2.15.0"

        const val exoPlayerFlac = "com.github.alashow.ExoPlayer-Extensions:extension-flac:v2.15.1"

        object ConstraintLayout {
            private const val version = "2.1.3"
            const val library = "androidx.constraintlayout:constraintlayout:$version"
        }

        object Activity {
            private const val version = "1.4.0"
            const val library = "androidx.activity:activity-ktx:$version"
        }

        object Fragment {
            private const val version = "1.4.1"
            const val library = "androidx.fragment:fragment-ktx:$version"
        }

        object Hilt {
            private const val androidxHilt = "1.0.0"
            const val work = "androidx.hilt:hilt-work:$androidxHilt"
            const val navigation = "androidx.hilt:hilt-navigation-compose:$androidxHilt"
        }

        object Navigation {
            private const val version = "2.4.2"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
        }

        object LifeCycle {
            private const val ver = "2.5.0-rc01"
            const val vmKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$ver"
            const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$ver"
            const val liveDate = "androidx.lifecycle:lifecycle-livedata-ktx:$ver"
            const val vmSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$ver"
            const val compiler = "androidx.lifecycle:lifecycle-compiler:$ver"
        }

        object Paging {
            private const val ver = "3.1.1"
            const val common = "androidx.paging:paging-common-ktx:$ver"
            const val runtime = "androidx.paging:paging-runtime-ktx:$ver"
            const val compose = "androidx.paging:paging-compose:1.0.0-alpha14"
        }

        //数据库
        object Room {
            private const val ver = "2.4.2"
            const val common = "androidx.room:room-common:$ver"
            const val ktx = "androidx.room:room-ktx:$ver"
            const val runtime = "androidx.room:room-runtime:$ver"
            const val compiler = "androidx.room:room-compiler:$ver"
            const val paging = "androidx.room:room-paging:$ver"
        }

        object Work {
            const val runtime = "androidx.work:work-runtime-ktx:2.7.1"
        }
    }

    object Kotlin {
        private const val coroutines = "1.6.1"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVer"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVer"

        object Serialization {
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3"
            const val protobuf = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.3"
        }
    }

    object Umeng {
        //友盟基础组件库（所有友盟业务SDK都依赖基础组件库）
        const val common = "com.umeng.umsdk:common:9.4.7"
        const val asms = "com.umeng.umsdk:asms:1.5.0"
        const val abtest = "com.umeng.umsdk:abtest:1.0.0"
        const val apm = "com.umeng.umsdk:apm:1.5.2"
    }

    object Logger {
        const val library = "com.orhanobut:logger:2.2.0"
    }

    object Hilt {
        private const val hilt = "2.41"
        const val library = "com.google.dagger:hilt-android:$hilt"
        const val compiler = "com.google.dagger:hilt-compiler:$hilt"
    }

    //第三方库
    object Libs {
        const val mmkv = "com.tencent:mmkv-static:1.2.10"
        const val desugar = "com.android.tools:desugar_jdk_libs:1.1.5"
    }

}