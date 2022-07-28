plugins {
    id("java-library")
    kotlin("jvm")
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = com.baimsg.build.Dep.javaVersion
    targetCompatibility = com.baimsg.build.Dep.javaVersion
}

publishing {
    repositories {
        maven {
            name = "local-maven"
            url = uri(rootProject.rootDir.path + "/plugins/local-maven")
        }
    }
}