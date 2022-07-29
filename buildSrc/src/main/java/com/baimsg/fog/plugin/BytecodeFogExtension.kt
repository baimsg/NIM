package com.baimsg.fog.plugin

import com.baimsg.fog.*
import com.baimsg.fog.kg.RandomKeyGenerator

/**
 * Create by Baimsg on 2022/7/29
 * 字节码加密配置
 **/
abstract class BytecodeFogExtension {
    /**
     * 忽略加密类注解的className
     */
    abstract var ignoreFogClassName: String

    /**
     * 解密函数的 className
     */
    abstract var implementation: String

    /**
     * 需要执行加密操作的包名
     * 默认全部执行
     */
    var fogPackages: List<String> = emptyList()

    /**
     * 密钥
     */
    var kg: IKeyGenerator = RandomKeyGenerator()

    /**
     * 启用或禁用 bytecodeFog 插件。 默认启用。
     */
    var enable: Boolean = true

    /**
     * 启用或禁用 bytecodeFog 调试消息打印。 默认启用.
     */
    var debug: Boolean = true

}