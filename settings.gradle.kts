pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "local-maven"
            url = uri("./plugins/local-maven")
        }
        mavenLocal()
    }
}


includeBuild("./plugin")
include(":base")
include(":base-android")
include(":app_chat")
include(":common-data")
include(":qmui")

