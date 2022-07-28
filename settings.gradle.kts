pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven {
            name = "local-maven"
            url = uri("./plugins/local-maven")
        }
    }
}


includeBuild("./plugin")
include(":base")
include(":base-android")
include(":app_chat")
include(":common-data")
include(":qmui")

//插件
include(":plugins:bytecode-fog")
