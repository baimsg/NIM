package com.baimsg.buildsrc.fog

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

/**
 * Create by Baimsg on 2022/7/27
 *
 **/
class StringFogClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {
    private var ok = false
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("visit   => $name")
        if (name == "com/baimsg/chat/Constant") {
            ok = true
        }
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor? {
        return if (ok && name == "TEST" && descriptor == "Ljava/lang/String;") null else
            super.visitField(access, name, descriptor, signature, value)
    }
}