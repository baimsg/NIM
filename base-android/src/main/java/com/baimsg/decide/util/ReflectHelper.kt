package com.baimsg.decide.util

import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

object ReflectHelper {
    private const val TAG = "ReflectHelper"

    fun <T> getStaticFieldValue(cls: Class<*>?, name: String?): T? {
        if (null != cls && null != name) {
            try {
                val field = getField(cls, name)
                if (null != field) {
                    field.isAccessible = true
                    return field[cls] as T
                }
            } catch (t: Throwable) {
                Log.w(TAG, "get static field $name of $cls error", t)
            }
        }
        return null
    }

    fun setStaticFieldValue(cls: Class<*>?, name: String?, value: Any?): Boolean {
        if (null != cls && null != name) {
            try {
                val field = getField(cls, name)
                if (null != field) {
                    field.isAccessible = true
                    field[cls] = value
                    return true
                }
            } catch (t: Throwable) {
                Log.w(TAG, "set static field $name of $cls error", t)
            }
        }
        return false
    }

    fun <T> getFieldValue(obj: Any?, name: String?): T? {
        if (null != obj && null != name) {
            try {
                val field = getField(obj.javaClass, name)
                if (null != field) {
                    field.isAccessible = true
                    return field[obj] as T
                }
            } catch (t: Throwable) {
                Log.w(TAG, "get field $name of $obj error", t)
            }
        }
        return null
    }

    fun <T> getFieldValue(obj: Any?, type: Class<*>?): T? {
        if (null != obj && null != type) {
            try {
                val field = getField(obj.javaClass, type)
                if (null != field) {
                    field.isAccessible = true
                    return field[obj] as T
                }
            } catch (t: Throwable) {
                Log.w(TAG, "get field with type $type of $obj error", t)
            }
        }
        return null
    }

    fun setFieldValue(obj: Any?, name: String?, value: Any?): Boolean {
        if (null != obj && null != name) {
            try {
                val field = getField(obj.javaClass, name)
                if (null != field) {
                    field.isAccessible = true
                    field[obj] = value
                    return true
                }
            } catch (t: Throwable) {
                Log.w(TAG, "set field $name of $obj error", t)
            }
        }
        return false
    }

    fun <T> newInstance(className: String, vararg args: Any?): T? {
        return try {
            newInstance(Class.forName(className), *args)
        } catch (e: ClassNotFoundException) {
            Log.w(TAG, "new instance of $className error", e)
            null
        }
    }

    fun <T> newInstance(clazz: Class<*>, vararg args: Any?): T? {
        val arrayOfConstructors = clazz.declaredConstructors
        loop@ for (constructor in arrayOfConstructors) {
            val types = constructor.parameterTypes
            if (types.size == args.size) {
                for (i in types.indices) {
                    if (null != args[i] && !types[i].isAssignableFrom(args[i]!!.javaClass)) {
                        continue@loop
                    }
                }
                return try {
                    constructor.isAccessible = true
                    constructor.newInstance(*args) as T
                } catch (t: Throwable) {
                    Log.w(TAG, "Invoke constructor $constructor error", t)
                    null
                }
            }
        }
        return null
    }

    fun <T> invokeStaticMethod(klass: Class<*>?, name: String?): T? {
        return invokeStaticMethod(klass, name, arrayOfNulls<Class<*>?>(0), arrayOfNulls(0))
    }

    fun <T> invokeStaticMethod(
        klass: Class<*>?,
        name: String?,
        types: Array<Class<*>?>?,
        args: Array<Any?>?
    ): T? {
        if (null != klass && null != name && null != types && null != args && types.size == args.size) {
            try {
                val method = getMethod(klass, name, types)
                if (null != method) {
                    method.isAccessible = true
                    return method.invoke(klass, *args) as T
                }
            } catch (e: Throwable) {
                Log.w(
                    TAG,
                    "Invoke " + name + "(" + Arrays.toString(types) + ") of " + klass + " error",
                    e
                )
            }
        }
        return null
    }


    fun <T> invokeMethod(obj: Any?, name: String?): T? {
        return invokeMethod(obj, name, arrayOfNulls<Class<*>?>(0), arrayOfNulls(0))
    }

    fun <T> invokeMethod(
        obj: Any?,
        name: String?,
        types: Array<Class<*>?>?,
        args: Array<Any?>?
    ): T? {
        if (null != obj && null != name && null != types && null != args && types.size == args.size) {
            try {
                val method = getMethod(obj.javaClass, name, types)
                if (null != method) {
                    method.isAccessible = true
                    return method.invoke(obj, *args) as T
                }
            } catch (e: Throwable) {
                Log.w(
                    TAG,
                    "Invoke " + name + "(" + Arrays.toString(types) + ") of " + obj + " error",
                    e
                )
            }
        }
        return null
    }

    fun getField(cls: Class<*>, name: String?): Field? {
        return try {
            cls.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            val parent = cls.superclass ?: return null
            getField(parent, name)
        }
    }

    fun getField(cls: Class<*>, type: Class<*>): Field? {
        val fields = cls.declaredFields
        if (fields.isEmpty()) {
            val parent = cls.superclass ?: return null
            return getField(parent, type)
        }
        for (field in fields) {
            if (field.type == type) {
                return field
            }
        }
        return null
    }

    private fun getMethod(cls: Class<*>, name: String, types: Array<Class<*>?>): Method? {
        return try {
            cls.getDeclaredMethod(name, *types)
        } catch (e: NoSuchMethodException) {
            val parent = cls.superclass ?: return null
            getMethod(parent, name, types)
        }
    }
}