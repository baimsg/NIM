pluginManagement {
    repositories {
        maven {
            name = "local-maven"
            url = uri("./plugins/local-maven")
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
include(":plugins:bytecode-fog")
//include(":plugins:decompile-crasher")
