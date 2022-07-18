pluginManagement {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("./local-plugin-repository")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}


includeBuild("./plugins/depend-manage")
include(":base")
include(":base-android")
include(":app_chat")
include(":common-data")
include(":qmui")

//插件
include(":plugins:decompile-crasher")
