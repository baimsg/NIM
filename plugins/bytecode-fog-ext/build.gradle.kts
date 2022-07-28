import  com.baimsg.build.Dep
plugins {
    id("java-library")
    kotlin("jvm")
}

java {
    sourceCompatibility = Dep.javaVersion
    targetCompatibility = Dep.javaVersion
}
