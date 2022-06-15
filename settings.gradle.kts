pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}


includeBuild("./plugin")
include(":base")
include(":base-android")
include(":app_chat")
include(":jadx")
