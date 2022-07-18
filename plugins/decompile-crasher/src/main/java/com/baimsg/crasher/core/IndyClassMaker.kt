package com.baimsg.crasher.core

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.commons.InstructionAdapter
import java.io.PrintStream
import java.lang.invoke.*

/**
 * Created by anatawa12 on 2018/09/09.
 */
object IndyClassMaker {
    private val Object = getType(Object::class.java)
    private val Lookup = getType(MethodHandles.Lookup::class.java)
    private val MethodHandles = getType(MethodHandles::class.java)
    private val String = getType(String::class.java)
    private val MethodType = getType(MethodType::class.java)
    private val Class = getType(Class::class.java)
    private val CallSite = getType(CallSite::class.java)
    private val MethodHandle = getType(MethodHandle::class.java)
    private val ConstantCallSite = getType(ConstantCallSite::class.java)
    private val StringBuilder = getType(StringBuilder::class.java)
    private val System = getType(System::class.java)
    private val PrintStream = getType(PrintStream::class.java)

    private val NoSuchMethodException = getType(NoSuchMethodException::class.java)
    private val NoSuchFieldException = getType(NoSuchFieldException::class.java)
    private val IllegalAccessException = getType(IllegalAccessException::class.java)
    private val IllegalArgumentException = getType(IllegalArgumentException::class.java)
    private val RuntimeException = getType(RuntimeException::class.java)
    private val Throwable = getType(Throwable::class.java)

    @Suppress("LocalVariableName")
    fun make(indyClass: IndyClass, isRuntimeDebug: Boolean): ByteArray? {
        val writer = ClassWriter(COMPUTE_MAXS or COMPUTE_FRAMES)
        val thisClass = getObjectType(indyClass.classPath)

        writer.apply {
            visit(V1_8, ACC_PUBLIC, indyClass.classPath, null, Object.internalName, null)
            visitSource("<generated by tool>", "")
            //*
            InstructionAdapter(visitMethod(ACC_PRIVATE, "<init>", "()$VOID_TYPE", null, null)).apply {
                visitCode()
                load(0, thisClass)
                invokespecial(Object.internalName, "<init>", "()$VOID_TYPE", false)
                areturn(VOID_TYPE)
                visitMaxs(0, 0)
                visitEnd()
            }
            // */

            //region field
            //*
            //region fieldCallSite
            InstructionAdapter(visitMethod(ACC_PUBLIC or ACC_STATIC, indyClass.field,
                    "($Lookup$String$MethodType$INT_TYPE$Class$Class$String)$CallSite", null,
                    arrayOf(
                            IllegalAccessException.internalName,
                            NoSuchFieldException.internalName
                    ))).apply {
                val signetureType = Class
                val indyClassMethodName = indyClass.field

                getCallSite(thisClass, signetureType, indyClassMethodName, isRuntimeDebug)
            }
            //endregion

            //region fieldMethodHandle
            InstructionAdapter(visitMethod(ACC_PUBLIC or ACC_STATIC, indyClass.field + "$$1",
                    "($Lookup$INT_TYPE$Class$Class$String)$MethodHandle", null,
                    arrayOf(
                            IllegalAccessException.internalName,
                            NoSuchFieldException.internalName
                    ))).apply {
                val caller = 0
                val callType = 1
                val owner = 2
                val findType = 3
                val name = 4

                visitCode()

                load(callType, INT_TYPE)
                val default = Label()
                val l0 = Label()
                val l1 = Label()
                val l2 = Label()
                val l3 = Label()
                tableswitch(0, 3, default, l0, l1, l2, l3)
                val list = listOf(
                        l0 to "findStaticGetter",
                        l1 to "findStaticSetter",
                        l2 to "findGetter",
                        l3 to "findSetter"
                )
                for ((label, mName) in list) {
                    visitLabel(label)

                    load(caller, Lookup)
                    load(owner, Class)
                    load(name, String)
                    load(findType, Class)
                    invokevirtual(Lookup.internalName, mName, "($Class$String$Class)$MethodHandle", false)
                    areturn(MethodHandle)
                }
                visitLabel(default)
                anew(IllegalArgumentException)
                dup()
                aconst("arg$callType")
                invokespecial(IllegalArgumentException.internalName, "<init>", "($String)$VOID_TYPE", false)
                athrow()

                visitMaxs(0, 0)
                visitEnd()
            }
            //endregion
            // */
            //endregion

            //region method
            InstructionAdapter(visitMethod(ACC_PUBLIC or ACC_STATIC, indyClass.method,
                    "($Lookup$String$MethodType$INT_TYPE$Class$MethodType$String)$CallSite", null,
                    arrayOf(
                            IllegalAccessException.internalName,
                            NoSuchMethodException.internalName
                    ))).apply {
                val signetureType = MethodType
                val indyClassMethodName = indyClass.method

                getCallSite(thisClass, signetureType, indyClassMethodName, isRuntimeDebug)
            }
            //region methodMethodHandle
            InstructionAdapter(visitMethod(ACC_PUBLIC or ACC_STATIC, indyClass.method + "$$1",
                    "($Lookup$INT_TYPE$Class$MethodType$String)$MethodHandle", null,
                    arrayOf(
                            IllegalAccessException.internalName,
                            NoSuchMethodException.internalName
                    ))).apply {
                val caller = 0
                val callType = 1
                val owner = 2
                val findType = 3
                val name = 4

                visitCode()

                load(callType, INT_TYPE)
                val default = Label()
                val l0 = Label()
                val l1 = Label()
                val l2 = Label()
                val l3 = Label()
                tableswitch(0, 3, default, l0, l1, l2, l3)
                val list = listOf(
                        l0 to "findVirtual",
                        l1 to "findStatic"
                )
                for ((label, mName) in list) {
                    visitLabel(label)
                    load(caller, Lookup)
                    load(owner, Class)
                    load(name, String)
                    load(findType, Class)
                    invokevirtual(Lookup.internalName, mName, "($Class$String$MethodType)$MethodHandle", false)
                    areturn(MethodHandle)
                }

                visitLabel(l2)
                load(caller, Lookup)
                load(owner, Class)
                load(name, String)
                load(findType, Class)
                load(caller, Lookup)
                invokevirtual(Lookup.internalName, "lookupClass", "()$Class", false)
                invokevirtual(Lookup.internalName, "findSpecial", "($Class$String$MethodType$Class)$MethodHandle", false)
                areturn(MethodHandle)

                visitLabel(l3)
                load(caller, Lookup)
                load(owner, Class)
                load(findType, Class)
                invokevirtual(Lookup.internalName, "findConstructor", "($Class$MethodType)$MethodHandle", false)
                areturn(MethodHandle)

                visitLabel(default)
                anew(IllegalArgumentException)
                dup()
                aconst("arg$callType")
                invokespecial(IllegalArgumentException.internalName, "<init>", "($String)$VOID_TYPE", false)
                athrow()

                visitMaxs(0, 0)
                visitEnd()
            }
            //endregion
            //endregion
            visitEnd()
        }

        return writer.toByteArray()
    }

    private fun InstructionAdapter.getCallSite(
            thisClass: Type,
            signetureType: Type,
            indyClassMethodName: String,
            isRuntimeDebug: Boolean) {
        val caller = 0
        val methodType = 2
        val callType = 3
        val owner = 4
        val findType = 5
        val name = 6
        val handle = 7
        val suppressedThrowable = 7
        visitCode()
        line(101)
        val tryStart = labelHere()

        aconst(null)
        store(suppressedThrowable, IllegalAccessException)


        if (isRuntimeDebug) {
            getstatic(System.internalName, "out", "$PrintStream")

            anew(StringBuilder)
            dup()
            aconst("has lookupModes: ")
            invokespecial(StringBuilder.internalName, "<init>", "($String)$VOID_TYPE", false)

            load(caller, Lookup)
            invokevirtual(Lookup.internalName, "lookupModes", "()$INT_TYPE", false)
            /*
        getstatic(Lookup.internalName, "PRIVATE", "$INT_TYPE")
        and(INT_TYPE)
        // */
            invokevirtual(StringBuilder.internalName, "append", "($INT_TYPE)$StringBuilder", false)
            aconst(" for ")
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            aconst("callType: ")
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            load(callType, INT_TYPE)
            invokevirtual(StringBuilder.internalName, "append", "($INT_TYPE)$StringBuilder", false)
            aconst(", owner: ")
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            load(owner, Class)
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            aconst(", name: ")
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            load(name, String)
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            aconst(", findType: ")
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
            load(findType, String)
            invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)

            invokevirtual(PrintStream.internalName, "println", "($Object)$VOID_TYPE", false)
        }

        line(102)

        load(caller, Lookup)
        load(callType, INT_TYPE)
        load(owner, Class)
        load(findType, signetureType)
        load(name, String)
        invokestatic(thisClass.internalName, "$indyClassMethodName$$1", "($Lookup$INT_TYPE$Class$signetureType$String)$MethodHandle", false)
        store(handle, MethodHandle)

        //*

        line(103)

        load(handle, MethodHandle)
        invokevirtual(MethodHandle.internalName, "type", "()$MethodType", false)
        load(methodType, MethodType)
        invokevirtual(MethodType.internalName, "equals", "($Object)$BOOLEAN_TYPE", false)
        val endIf1 = Label()
        ifne(endIf1)

        line(104)

        invokestatic(MethodHandles.internalName, "publicLookup", "()$Lookup", false)
        load(callType, INT_TYPE)
        load(owner, Class)
        load(findType, signetureType)
        load(name, String)
        invokestatic(thisClass.internalName, "$indyClassMethodName$$1", "($Lookup$INT_TYPE$Class$signetureType$String)$MethodHandle", false)
        store(handle, MethodHandle)

        visitLabel(endIf1)

        line(105)
        // */

        anew(ConstantCallSite)
        dup()
        load(handle, MethodHandle)
        invokespecial(ConstantCallSite.internalName, "<init>", "($MethodHandle)$VOID_TYPE", false)
        areturn(CallSite)

        val tryEnd = labelHere()

        line(106)
        val handler = labelHere()
        visitTryCatchBlock(tryStart, tryEnd, handler, Throwable.internalName)

        wrapLog(callType, owner, name, findType)

        visitMaxs(0, 0)
        visitEnd()
    }

    fun InstructionAdapter.wrapLog(callType: Int, owner: Int, name: Int, findType: Int) {
        anew(RuntimeException)
        dup()
        dup2X1()
        pop2()
        anew(StringBuilder)
        dup()
        invokespecial(StringBuilder.internalName, "<init>", "()V", false)
        aconst("callType: ")
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        load(callType, INT_TYPE)
        invokevirtual(StringBuilder.internalName, "append", "($INT_TYPE)$StringBuilder", false)
        aconst(", owner: ")
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        load(owner, Class)
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        aconst(", name: ")
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        load(name, String)
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        aconst(", findType: ")
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        load(findType, String)
        invokevirtual(StringBuilder.internalName, "append", "($Object)$StringBuilder", false)
        invokevirtual(StringBuilder.internalName, "toString", "()$String", false)
        swap()
        invokespecial(RuntimeException.internalName, "<init>", "($String$Throwable)V", false)
        athrow()
    }

    fun MethodVisitor.line(line: Int) {
        visitLineNumber(line, Label().apply { visitLabel(this) })
    }

    fun MethodVisitor.labelHere() = Label().apply { visitLabel(this) }
}
