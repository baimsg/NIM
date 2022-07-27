package com.baimsg.fog

import com.baimsg.fog.ext.IKeyGenerator
import com.baimsg.fog.ext.IStringFog
import com.baimsg.fog.util.Log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * Create by Baimsg on 2022/7/25
 * 产生字符串加密的工厂
 **/
object ClassVisitorFactory {

    /**
     * 创建 [ClassVisitor] 对象
     * @param stringFogImpl 加解密实现对象
     * @param mappingPrinter 映射文件输出对象
     * @param fogPackages 需要加密的包名
     * @param kg 密钥提供对象
     * @param fogClassName 加密类
     * @param className 当前 className
     * @param cw [ClassWriter] 对象
     */
    fun create(
        stringFogImpl: IStringFog,
        mappingPrinter: StringFogMappingPrinter,
        fogPackages: Array<String>,
        kg: IKeyGenerator,
        fogClassName: String,
        className: String,
        cw: ClassWriter
    ): ClassVisitor {
        if (WhiteLists.inWhiteList(className) || !isInFogPackages(fogPackages, className)) {
            Log.v("StringFog ignore: $className")
            return createEmpty(cw)
        }
        Log.v("StringFog execute: $className")
        return StringFogClassVisitor(stringFogImpl, mappingPrinter, fogClassName, cw, kg)
    }

    /**
     * 创建不进行任何处理的 [ClassVisitor] 对象
     * @param cw [ClassWriter] 对象
     */
    private fun createEmpty(cw: ClassWriter): ClassVisitor {
        return object : ClassVisitor(Opcodes.ASM6, cw) {}
    }

    /**
     * 判断当前类是否是需要进行字符串加密的
     * @param fogPackages 加密包名集合
     * @param className 当前 className
     */
    private fun isInFogPackages(fogPackages: Array<String>, className: String): Boolean {
        if (className.isBlank()) {
            return false
        }
        if (fogPackages.isEmpty()) {
            // default we fog all packages.
            return true
        }
        for (fogPackage in fogPackages) {
            if (className.replace('/', '.').startsWith("$fogPackage.")) {
                return true
            }
        }
        return false
    }
}