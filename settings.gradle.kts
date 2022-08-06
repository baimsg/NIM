pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}


includeBuild("./plugin")
include(":base")
include(":base-android")
include(":app_chat")
include(":common-data")
include(":qmui")

include(":bytecode-fog-ext")
