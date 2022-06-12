package com.baimsg.decide.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.res.Configuration
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.ChecksSdkIntAtLeast
import java.io.*
import java.lang.reflect.Method
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern

/**
 *
 * create by baimsg 2021/11/24
 * Email 1469010683@qq.com
 *
 **/
@SuppressLint("PrivateApi")
object DeviceHelper {
    private val MEIZU_BOARD = arrayOf("m9", "M9", "mx", "MX")

    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_FLYME_VERSION_NAME = "ro.build.display.id"
    private const val CPU_FILE_PATH_0 = "/sys/devices/system/cpu/"
    private const val CPU_FILE_PATH_1 = "/sys/devices/system/cpu/possible"
    private const val CPU_FILE_PATH_2 = "/sys/devices/system/cpu/present"
    private val CPU_FILTER =
        FileFilter { pathname -> Pattern.matches("cpu[0-9]", pathname.name) }
    private const val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"

    private val BRAND = Build.BRAND.lowercase()

    private const val FLYME = "flyme"
    private const val ZTE_C2016 = "zte c2016"
    private const val ZUK_Z1 = "zuk z1"
    private var sMiuiVersionName: String? = null
    private var sFlymeVersionName: String? = null
    private var sIsTabletChecked = false
    private var sIsTabletValue = false

    //总内存
    private var sTotalMemory: Long = -1

    //内部储存
    var sInnerStorageSize: Long = -1

    //外部内存
    private var sExtraStorageSize: Long = -1

    //
    private var sBatteryCapacity = -1.0

    //cpu内核数
    private var sCpuCoreCount = -1

    init {
        val properties = Properties()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/build.prop 会报 permission denied
            var stream: FileInputStream? = null
            try {
                stream =
                    FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
                properties.load(stream)
            } catch (e: Exception) {
            } finally {
                stream?.close()
            }
        }

        val clzSystemProperties: Class<*>?
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties")
            val getMethod = clzSystemProperties.getDeclaredMethod("get", String::class.java)
            // miui
            sMiuiVersionName =
                getLowerCaseName(
                    properties,
                    getMethod,
                    KEY_MIUI_VERSION_NAME
                )
            //flyme
            sFlymeVersionName =
                getLowerCaseName(
                    properties,
                    getMethod,
                    KEY_FLYME_VERSION_NAME
                )
        } catch (e: Exception) {
        }
    }

    /**
     * 判断是否是平板（私有方法仅加载一次）
     * @param context 上下文
     */
    private fun initTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >=
                Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 判断是否为平板设备
     * @param context 上下文
     */
    fun isTablet(context: Context): Boolean {
        if (sIsTabletChecked) {
            return sIsTabletValue
        }
        sIsTabletValue = initTablet(context)
        sIsTabletChecked = true
        return sIsTabletValue
    }

    /**
     * 判断是否是flyme系统
     */
    fun isFlyme(): Boolean {
        return !sFlymeVersionName.isNullOrBlank() && sFlymeVersionName?.contains(FLYME) == true
    }

    /**
     * 低于flyme版本
     * @param majorVersion 主要版本
     */
    fun isFlymeLowerThan(majorVersion: Int): Boolean {
        return isFlymeLowerThan(majorVersion, 0, 0)
    }

    /**
     * 是否低于flyme版本
     * @param majorVersion 主要版本
     * @param minorVersion 最低版本
     * @param patchVersion 匹配版本
     */
    fun isFlymeLowerThan(majorVersion: Int, minorVersion: Int, patchVersion: Int): Boolean {
        var isLower = false
        if (!sFlymeVersionName.isNullOrBlank()) {
            try {
                val pattern = Pattern.compile("(\\d+\\.){2}\\d")
                val matcher =
                    pattern.matcher(sFlymeVersionName)
                if (matcher.find()) {
                    val versionString = matcher.group()
                    if (versionString.isNotEmpty()) {
                        val version = versionString.split(".")
                        if (version.isNotEmpty()) {
                            if (version[0].toInt() < majorVersion) {
                                isLower = true
                            }
                        }
                        if (version.size >= 2 && minorVersion > 0) {
                            if (version[1].toInt() < majorVersion) {
                                isLower = true
                            }
                        }
                        if (version.size >= 3 && patchVersion > 0) {
                            if (version[2].toInt() < majorVersion) {
                                isLower = true
                            }
                        }
                    }
                }
            } catch (ignore: Throwable) {
            }
        }
        return isMeizu() && isLower
    }

    /**
     * 获取小写的名称
     * @param p
     * @param get
     * @param key
     */
    private fun getLowerCaseName(p: Properties, get: Method, key: String): String? {
        var name = p.getProperty(key)
        if (name == null) {
            try {
                name = get.invoke(null, key) as String
            } catch (ignored: Exception) {
            }
        }
        if (name != null) name = name.lowercase()
        return name
    }

    /**
     * 是否是官方认证的刘海
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    fun isNotchOfficialSupport(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * 判断是否是MIUI系统
     */
    fun isMIUI(): Boolean {
        return !sMiuiVersionName.isNullOrBlank()
    }

    fun isMIUIV5(): Boolean {
        return "v5" == sMiuiVersionName
    }

    fun isMIUIV6(): Boolean {
        return "v6" == sMiuiVersionName
    }

    fun isMIUIV7(): Boolean {
        return "v7" == sMiuiVersionName
    }

    fun isMIUIV8(): Boolean {
        return "v8" == sMiuiVersionName
    }

    fun isMIUIV9(): Boolean {
        return "v9" == sMiuiVersionName
    }

    /**
     * 判断是否为 ZUK Z1
     * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
     */
    fun isZUKZ1(): Boolean {
        val board = Build.MODEL
        return !board.isNullOrBlank() && board.lowercase().contains(ZUK_Z1)
    }

    /**
     * 判断是否为 ZTK C2016。
     */
    fun isZTKC2016(): Boolean {
        val board = Build.MODEL
        return !board.isNullOrBlank() && board.lowercase().contains(ZTE_C2016)
    }

    /**
     * 判断是否是魅族
     */
    fun isMeizu(): Boolean {
        return isPhone(MEIZU_BOARD) || isFlyme()
    }

    fun isXiaomi(): Boolean {
        return Build.MANUFACTURER.lowercase() == "xiaomi"
    }

    fun isVivo(): Boolean {
        return BRAND.contains("vivo") || BRAND.contains("bbk")
    }

    fun isOppo(): Boolean {
        return BRAND.contains("oppo")
    }

    fun isHuawei(): Boolean {
        return BRAND.contains("huawei") || BRAND.contains("honor")
    }

    /**
     * 是 Essential 的手机
     */
    fun isEssentialPhone(): Boolean {
        return BRAND.contains("essential")
    }

    /**
     * 是否是小米全面屏
     */
    fun isMiuiFullDisplay(context: Context): Boolean {
        return isMIUI() && Settings.Global.getInt(
            context.contentResolver,
            "force_fsg_nav_bar",
            0
        ) != 0
    }

    /**
     * 获取运行内存
     */
    fun getTotalMemory(context: Context): Long {
        if (sTotalMemory != -1L) {
            return sTotalMemory
        }
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        sTotalMemory = memoryInfo.totalMem
        return sTotalMemory
    }

    /**
     * 获取内部存储大小
     */
    fun getInnerStorageSize(): Long {
        if (sInnerStorageSize != -1L) {
            return sInnerStorageSize
        }
        val dataDir = Environment.getDataDirectory() ?: return 0
        sInnerStorageSize = dataDir.totalSpace
        return sInnerStorageSize
    }

    /**
     * 有额外存储
     */
    fun hasExtraStorage(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    /**
     * 获取额外存储大小
     */
    fun getExtraStorageSize(): Long {
        if (sExtraStorageSize != -1L) {
            return sExtraStorageSize
        }
        if (!hasExtraStorage()) {
            return 0
        }
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.blockCountLong
        sExtraStorageSize = blockSize * availableBlocks
        return sExtraStorageSize
    }

    /**
     * 获取总存储大小
     */
    fun getTotalStorageSize(): Long {
        return getInnerStorageSize() + getExtraStorageSize()
    }

    /**
     * 获取CPU核心计数
     */
    fun getCpuCoreCount(): Int {
        if (sCpuCoreCount != -1) {
            return sCpuCoreCount
        }
        var cores: Int
        try {
            cores = getCoresFromFile(CPU_FILE_PATH_1)
            if (cores == 0) {
                cores = getCoresFromFile(CPU_FILE_PATH_2)
            }
            if (cores == 0) {
                cores =
                    getCoresFromCPUFiles(CPU_FILE_PATH_0)
            }
        } catch (e: java.lang.Exception) {
            cores = 0
        }
        if (cores == 0) {
            cores = 1
        }
        sCpuCoreCount = cores
        return cores
    }

    /**
     * 从 CPU 文件中获取核心数
     */
    private fun getCoresFromCPUFiles(path: String): Int {
        val list =
            File(path).listFiles(CPU_FILTER)
        return list?.size ?: 0
    }

    /**
     * 从文件中获取核心数
     */
    private fun getCoresFromFile(file: String): Int {
        var inputStream: InputStream? = null
        return try {
            inputStream = FileInputStream(file)
            val buf = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            val fileContents = buf.readLine()
            buf.close()
            if (fileContents == null || !fileContents.matches("0-[\\d]+$".toRegex())) {
                return 0
            }
            val num = fileContents.substring(2)
            num.toInt() + 1
        } catch (e: IOException) {
            0
        } finally {
            inputStream?.close()
        }
    }

    /**
     * 判断悬浮窗权限（目前主要用户魅族与小米的检测）。
     */
    fun isFloatWindowOpAllowed(context: Context): Boolean {
        val version = Build.VERSION.SDK_INT
        // 24 是AppOpsManager.OP_SYSTEM_ALERT_WINDOW 的值，该值无法直接访问
        return checkOp(context, 24)
    }

    /**
     * 获取电池容量
     */
    fun getBatteryCapacity(context: Context?): Double {
        if (sBatteryCapacity != -1.0) {
            return sBatteryCapacity
        }
        val ret: Double = try {
            val cls = Class.forName(POWER_PROFILE_CLASS)
            val instance = cls.getConstructor(Context::class.java).newInstance(context)
            val method = cls.getMethod("getBatteryCapacity")
            method.invoke(instance) as Double
        } catch (ignore: java.lang.Exception) {
            -1.0
        }
        sBatteryCapacity = ret
        return sBatteryCapacity
    }


    @SuppressLint("DiscouragedPrivateApi")
    private fun checkOp(context: Context, op: Int): Boolean {
        val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val method = manager.javaClass.getDeclaredMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val property =
                method.invoke(manager, op, Binder.getCallingUid(), context.packageName) as Int
            return AppOpsManager.MODE_ALLOWED == property
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }


    private fun isPhone(boards: Array<String>): Boolean {
        val board = Build.BOARD ?: return false
        for (value in boards) {
            if (board == value) {
                return true
            }
        }
        return false
    }

}