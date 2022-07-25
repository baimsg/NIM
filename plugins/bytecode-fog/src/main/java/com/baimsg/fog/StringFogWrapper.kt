package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/25
 *
 **/
class StringFogWrapper(impl: String) : IStringFog {
    private var mStringFogImpl: IStringFog? = null
    override fun encrypt(data: String, key: ByteArray): ByteArray {
        return if (mStringFogImpl == null) data.toByteArray() else mStringFogImpl?.encrypt(
            data,
            key
        ) ?: ByteArray(0)
    }

    override fun decrypt(data: ByteArray, key: ByteArray): String {
        return if (mStringFogImpl == null) String(data) else mStringFogImpl?.decrypt(data, key)
            ?: ""
    }

    override fun shouldFog(data: String): Boolean {
        return mStringFogImpl != null && mStringFogImpl?.shouldFog(data) == true
    }

    init {
        mStringFogImpl = try {
            Class.forName(impl).newInstance() as IStringFog
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Stringfog implementation class not found: $impl")
        } catch (e: InstantiationException) {
            throw IllegalArgumentException(
                "Stringfog implementation class new instance failed: "
                        + e.message
            )
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException(
                "Stringfog implementation class access failed: "
                        + e.message
            )
        }
    }
}