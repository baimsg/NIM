import com.baimsg.build.Dep

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm")
}

java {
    sourceCompatibility = Dep.javaVersion
    targetCompatibility = Dep.javaVersion
}

gradlePlugin {
    plugins {
        create("decompileCrasher") {
            id = "decompile-crasher"
            version = "1.0.0"
            implementationClass = "com.baimsg.crasher.DecompileCrasherPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = Dep.kotlinJvmTarget
}


publishing {
    repositories {
        maven {
            name = "local-maven"
            url = uri(rootProject.rootDir.path + "/plugins/local-maven")
        }
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.ow2.asm:asm-all:6.0_BETA")
    implementation("com.android.tools.build:gradle-api:7.2.1")
    implementation(kotlin("stdlib"))
    gradleApi()
    localGroovy()
}