package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/25
 *
 **/
internal object WhiteLists {

    private val CLASS_WHITE_LIST: MutableList<String> = mutableListOf()

    init {
        // default classes short name in white list.
        addWhiteList("BuildConfig")
        addWhiteList("R")
        addWhiteList("R2")
        addWhiteList("StringFog")
    }

    fun inWhiteList(name: String): Boolean {
        return name.isNotBlank() && checkClass(shortClassName(name))
    }

    private fun addWhiteList(name: String) {
        CLASS_WHITE_LIST.add(name)
    }

    private fun checkClass(name: String): Boolean {
        for (className in CLASS_WHITE_LIST) {
            if (name == className) {
                return true
            }
        }
        return false
    }

    private fun trueClassName(className: String): String {
        return className.replace('/', '.')
    }

    private fun shortClassName(className: String): String {
        val spiltArrays = trueClassName(className).split(".").toTypedArray()
        return spiltArrays[spiltArrays.size - 1]
    }


}
