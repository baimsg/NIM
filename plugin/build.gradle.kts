import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    idea
    kotlin("jvm") version "1.7.0"
    `kotlin-dsl`
}

group = "com.baimsg.decide.plugin"
version = "0.0.1"

buildscript {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    }
}

gradlePlugin {
    plugins {
        create("plugin-dep") {
            id = "plugin-dep"
            implementationClass = "com.baimsg.build.DepPlugin"
        }
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    api(gradleApi())
    api(gradleKotlinDsl())
    api(kotlin("gradle-plugin", version = "1.7.0"))
    api(kotlin("gradle-plugin-api", version = "1.7.0"))
    api("com.android.tools.build:gradle-api:7.2.2")
    api("com.android.tools.build:gradle:7.2.2")
    implementation(kotlin("stdlib-jdk8"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "11"
}