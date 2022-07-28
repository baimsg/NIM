package com.baimsg.fog

import org.objectweb.asm.*


/**
 * Create by Baimsg on 2022/7/25
 * 访问类执行字符串雾
 **/
class StringFogClassVisitor(
    private val mStringFogImpl: IStringFog,
    private val mMappingPrinter: StringFogMappingPrinter,
    fogClassName: String,
    cv: ClassVisitor,
    private val mKeyGenerator: IKeyGenerator
) : ClassVisitor(Opcodes.ASM9, cv) {

    companion object {
        private const val IGNORE_ANNOTATION =
            "Lcom/github/megatronking/stringfog/annotation/StringFogIgnore;"
    }

    private var mFogClassName: String

    private var isClInitExists = false

    private val mStaticFinalFields: MutableList<ClassStringField> = mutableListOf()
    private val mStaticFields: MutableList<ClassStringField> = mutableListOf()
    private val mFinalFields: MutableList<ClassStringField> = mutableListOf()
    private val mFields: MutableList<ClassStringField> = mutableListOf()

    private var mClassName: String? = null

    private var mIgnoreClass = false


    init {
        mFogClassName = fogClassName.replace(".", "/")
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor {
        mIgnoreClass = IGNORE_ANNOTATION == desc
        return super.visitAnnotation(desc, visible)
    }

    override fun visitField(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        var mValue = value
        if (ClassStringField.STRING_DESC == desc && name != null && !mIgnoreClass) {
            // static final, in this condition, the value is null or not null.
            if (access and Opcodes.ACC_STATIC != 0 && access and Opcodes.ACC_FINAL != 0) {
                mStaticFinalFields.add(ClassStringField(name, value as String?))
                mValue = null
            }
            // static, in this condition, the value is null.
            if (access and Opcodes.ACC_STATIC != 0 && access and Opcodes.ACC_FINAL == 0) {
                mStaticFields.add(ClassStringField(name, value as String?))
                mValue = null
            }

            // final, in this condition, the value is null or not null.
            if (access and Opcodes.ACC_STATIC == 0 && access and Opcodes.ACC_FINAL != 0) {
                mFinalFields.add(ClassStringField(name, value as String?))
                mValue = null
            }

            // normal, in this condition, the value is null.
            if (access and Opcodes.ACC_STATIC == 0 && access and Opcodes.ACC_FINAL != 0) {
                mFields.add(ClassStringField(name, value as String?))
                mValue = null
            }
        }
        return super.visitField(access, name, desc, signature, mValue)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, desc, signature, exceptions)
        if (mv == null || mIgnoreClass) {
            return mv
        }

        if ("<clinit>" == name) {
            isClInitExists = true
            // 如果 clinit 存在意味着静态字段（非最终字段）将在此处初始化。
            mv = object : StubMethodVisitor(Opcodes.ASM6, mv) {

                private var lastStashCst: String? = null
                override fun visitCode() {
                    super.visitCode()
                    // 这里初始化静态最终字段.
                    for (field in mStaticFinalFields) {
                        if (!canEncrypted(field.value)) {
                            continue
                        }
                        insertDecryptInstructions(field.value)
                        super.visitFieldInsn(
                            Opcodes.PUTSTATIC,
                            mClassName,
                            field.name,
                            ClassStringField.STRING_DESC
                        )
                    }
                }

                override fun visitLdcInsn(cst: Any?) {
                    // 这里初始化静态或静态最终字段，但我们必须检查字段名称 int 'visitFieldInsn'
                    if (cst is String && canEncrypted(cst)) {
                        lastStashCst = cst
                        insertDecryptInstructions(lastStashCst)
                    } else {
                        lastStashCst = null
                        super.visitLdcInsn(cst)
                    }
                }

                override fun visitFieldInsn(
                    opcode: Int,
                    owner: String,
                    name: String?,
                    desc: String?
                ) {
                    if (mClassName == owner && lastStashCst != null) {
                        var isContain = false
                        for (field in mStaticFields) {
                            if (field.name == name) {
                                isContain = true
                                break
                            }
                        }
                        if (!isContain) {
                            for (field in mStaticFinalFields) {
                                if (field.name == name && field.value == null) {
                                    field.value = lastStashCst
                                    break
                                }
                            }
                        }
                    }
                    lastStashCst = null
                    super.visitFieldInsn(opcode, owner, name, desc)
                }
            }
        } else if ("<init>" == name) {
            // Here init final(not static) and normal fields
            mv = object : StubMethodVisitor(Opcodes.ASM6, mv) {
                override fun visitLdcInsn(cst: Any) {
                    // We don't care about whether the field is final or normal
                    if (cst is String && canEncrypted(cst)) {
                        insertDecryptInstructions(cst)
                    } else {
                        super.visitLdcInsn(cst)
                    }
                }
            }
        } else {
            mv = object : StubMethodVisitor(Opcodes.ASM6, mv) {
                override fun visitLdcInsn(cst: Any) {
                    if (cst is String && canEncrypted(cst)) {
                        // If the value is a static final field
                        for (field in mStaticFinalFields) {
                            if (cst == field.value) {
                                super.visitFieldInsn(
                                    Opcodes.GETSTATIC,
                                    mClassName,
                                    field.name,
                                    ClassStringField.STRING_DESC
                                )
                                return
                            }
                        }
                        // If the value is a final field (not static)
                        for (field in mFinalFields) {
                            // if the value of a final field is null, we ignore it
                            if (cst == field.value) {
                                super.visitVarInsn(Opcodes.ALOAD, 0)
                                super.visitFieldInsn(
                                    Opcodes.GETFIELD,
                                    mClassName,
                                    field.name,
                                    "Ljava/lang/String;"
                                )
                                return
                            }
                        }
                        // local variables
                        insertDecryptInstructions(cst)
                        return
                    }
                    super.visitLdcInsn(cst)
                }
            }
        }
        return mv
    }

    override fun visitEnd() {
        if (!mIgnoreClass && !isClInitExists && mStaticFinalFields.isNotEmpty()) {
            val mv = StubMethodVisitor(
                Opcodes.ASM6,
                super.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
            )
            mv.visitCode()
            // Here init static final fields.
            for (field in mStaticFinalFields) {
                if (!canEncrypted(field.value)) {
                    continue
                }
                mv.insertDecryptInstructions(field.value)
                mv.visitFieldInsn(
                    Opcodes.PUTSTATIC,
                    mClassName,
                    field.name,
                    ClassStringField.STRING_DESC
                )
            }
            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(1, 0)
            mv.visitEnd()
        }
        super.visitEnd()
    }


    private fun canEncrypted(value: String?): Boolean {
        return !value.isNullOrBlank() && value.length < 65536 shr 2 &&
                mStringFogImpl.shouldFog(value)
    }

    private fun getJavaClassName(): String {
        return mClassName?.replace('/', '.') ?: "null"
    }

    /**
     * 插入解密说明
     * 构造一个新的[MethodVisitor]
     *
     * @param api 此访问者实现的 ASM API 版本。 必须是一个[Opcodes.ASM4] 或 [Opcodes.ASM5]
     * @param mv  此访问者必须将方法委托给的方法访问者
     */
    open inner class StubMethodVisitor(api: Int, mv: MethodVisitor) : MethodVisitor(api, mv) {

        fun insertDecryptInstructions(originalValue: String?) {
            val key = mKeyGenerator.generate(originalValue!!)
            val encryptValue = mStringFogImpl.encrypt(originalValue, key)
            pushArray(encryptValue)
            pushArray(key)
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                mFogClassName,
                "decrypt",
                "([B[B)Ljava/lang/String;",
                false
            )
            mMappingPrinter.output(
                getJavaClassName(), originalValue,
                encryptValue.contentToString()
            )
        }

        private fun pushArray(buffer: ByteArray) {
            pushNumber(buffer.size)
            mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BYTE)
            mv.visitInsn(Opcodes.DUP)
            for (i in buffer.indices) {
                pushNumber(i)
                pushNumber(buffer[i].toInt())
                mv.visitInsn(Type.BYTE_TYPE.getOpcode(Opcodes.IASTORE))
                if (i < buffer.size - 1) mv.visitInsn(Opcodes.DUP)
            }
        }

        private fun pushNumber(value: Int) {
            if (value >= -1 && value <= 5) {
                mv.visitInsn(Opcodes.ICONST_0 + value)
            } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.BIPUSH, value)
            } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.SIPUSH, value)
            } else {
                mv.visitLdcInsn(value)
            }
        }
    }
}