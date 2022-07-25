package com.baimsg.fog

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * Create by Baimsg on 2022/7/25
 *
 **/
object ClassVisitorFactory {
    fun create(
        stringFogImpl: IStringFog?, mappingPrinter: StringFogMappingPrinter?,
        fogPackages: Array<String>?, kg: IKeyGenerator?, fogClassName: String?,
        className: String, cw: ClassWriter
    ): ClassVisitor {
        if (WhiteLists.inWhiteList(className) || !isInFogPackages(fogPackages, className)) {
//            Log.v("StringFog ignore: $className")
            return createEmpty(cw)
        }
//        Log.v("StringFog execute: $className")
        return StringFogClassVisitor(
            stringFogImpl!!, mappingPrinter!!, fogClassName!!, cw,
            kg!!
        )
    }

    private fun createEmpty(cw: ClassWriter): ClassVisitor {
        return object : ClassVisitor(Opcodes.ASM5, cw) {}
    }

    private fun isInFogPackages(fogPackages: Array<String>?, className: String): Boolean {
        if (className.isBlank()) {
            return false
        }
        if (fogPackages == null || fogPackages.isEmpty()) {
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