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
        create("bytecodeFogPlugin") {
            id = "bytecode-fog"
            version = "1.0.0"
            implementationClass = "com.baimsg.fog.plugin.BytecodeFogPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = Dep.kotlinJvmTarget
}


publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri(rootProject.rootDir.path + "/local-plugin-repository")
        }
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle-api:7.2.1")
    implementation("org.ow2.asm:asm-all:6.0_BETA")
    implementation("com.squareup:javawriter:2.5.1")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.guava:guava:31.1-jre")
    implementation(project(":plugins:bytecode-fog-ext"))
    implementation(kotlin("stdlib"))
    gradleApi()
    localGroovy()
}