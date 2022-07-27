package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/25
 * 类中的字符串 field
 **/
internal data class ClassStringField(
    val name: String?,
    var value: String?
) {
    companion object {
        const val STRING_DESC = "Ljava/lang/String;"
    }
}
