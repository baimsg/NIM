pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}


includeBuild("./plugins/depend-manage")
include(":base")
include(":base-android")
include(":app_chat")
include(":common-data")
include(":qmui")
