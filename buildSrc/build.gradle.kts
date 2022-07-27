plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    idea
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "11"
}

dependencies {

}
