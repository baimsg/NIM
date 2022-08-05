import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.21"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    google()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "11"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.squareup:javawriter:2.5.1")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.android.tools.build:gradle-api:7.2.2")
}
