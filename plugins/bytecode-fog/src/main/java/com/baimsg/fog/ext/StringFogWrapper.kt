package com.baimsg.fog.ext

/**
 * Create by Baimsg on 2022/7/25
 * 包装加密 解密函数实现类
 **/
class StringFogWrapper(impl: String) : IStringFog {
    private var mStringFogImpl: IStringFog

    /**
     * 通过反射加载实例
     */
    init {
        mStringFogImpl = try {
            Class.forName(impl).getDeclaredConstructor().newInstance() as IStringFog
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("StringFog implementation class not found: $impl")
        } catch (e: InstantiationException) {
            throw IllegalArgumentException("StringFog implementation class new instance failed: ${e.message}")
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("StringFog implementation class access failed: ${e.message}")
        }
    }

    override fun encrypt(data: String, key: ByteArray): ByteArray {
        return mStringFogImpl.encrypt(data, key)
    }

    override fun decrypt(data: ByteArray, key: ByteArray): String {
        return mStringFogImpl.decrypt(data, key)
    }

    override fun shouldFog(data: String): Boolean {
        return mStringFogImpl.shouldFog(data)
    }
}