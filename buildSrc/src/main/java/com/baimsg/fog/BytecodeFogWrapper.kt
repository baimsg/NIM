package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/25
 * 通过反射包装加解密实现对象
 **/
class BytecodeFogWrapper(impl: String) : IBytecodeFog {
    private var mBytecodeFogImpl: IBytecodeFog

    /**
     * 通过反射加载实例
     */
    init {
        mBytecodeFogImpl = try {
            Class.forName(impl).getDeclaredConstructor().newInstance() as IBytecodeFog
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("BytecodeFog implementation class not found: $impl")
        } catch (e: InstantiationException) {
            throw IllegalArgumentException("BytecodeFog implementation class new instance failed: ${e.message}")
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("BytecodeFog implementation class access failed: ${e.message}")
        }
    }

    override fun encrypt(data: String, key: ByteArray): ByteArray {
        return mBytecodeFogImpl.encrypt(data, key)
    }

    override fun decrypt(data: ByteArray, key: ByteArray): String {
        return mBytecodeFogImpl.decrypt(data, key)
    }

    override fun shouldFog(data: String): Boolean {
        return mBytecodeFogImpl.shouldFog(data)
    }
}