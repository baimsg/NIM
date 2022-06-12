package com.baimsg.decide.util.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import com.baimsg.decide.util.DeviceHelper
import java.util.*

/**
 *
 * create by baimsg 2021/11/24
 * Email 1469010683@qq.com
 *
 **/


/**
 * 单位转换: dp -> px
 *
 * @param dp
 * @return
 */
fun Context.dp2px(dp: Float): Float {
    return (getDensity() * dp + 0.5f)
}


/**
 * 单位转换: sp -> px
 *
 * @param sp
 * @return
 */
fun Context.sp2px(sp: Float): Float {
    return (getFontDensity() * sp + 0.5f)
}

/**
 * 单位转换:px -> dp
 *
 * @param px
 * @return
 */
fun Context.px2dp(px: Float): Float {
    return (px / getDensity() + 0.5f)
}

/**
 * 单位转换:px -> sp
 *
 * @param px
 * @return
 */
fun Context.px2sp(px: Float): Float {
    return (px / getFontDensity() + 0.5f)
}

/**
 * 判断是否有状态栏
 *
 * @return
 */
fun Context.hasStatusBar(): Boolean {
    if (this is Activity) {
        val attrs = window.attributes
        return attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != WindowManager.LayoutParams.FLAG_FULLSCREEN
    }
    return true
}

/**
 * 获取ActionBar高度
 *
 * @return
 */
fun Context.getActionBarHeight(): Int {
    var actionBarHeight = 0
    val tv = TypedValue()
    if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        actionBarHeight = TypedValue.complexToDimensionPixelSize(
            tv.data,
            resources.displayMetrics
        )
    }
    return actionBarHeight
}

/**
 * 获取虚拟菜单的高度
 *
 * @return 若无则返回0
 */
fun Context.getNavMenuHeight(): Int {
    if (!isNavMenuExist()) {
        return 0
    }
    val resourceNavHeight = getResourceNavHeight()
    return if (resourceNavHeight >= 0) {
        resourceNavHeight
    } else getRealScreenSize()[1] - getScreenHeight()
    // 小米 MIX 有nav bar, 而 getRealScreenSize(context)[1] - getScreenHeight(context) = 0
}

/**
 * 通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
 */
fun Context.isNavMenuExist(): Boolean {
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    return !hasMenuKey && !hasBackKey
}

/**
 * navigation_bar_height 的值
 * 小米4没有nav bar, 而 navigation_bar_height 有值
 */
private fun Context.getResourceNavHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else -1
}


/**
 * 是否有摄像头
 */
private var sHasCamera: Boolean? = null

/**
 * 是否有相机
 */
fun Context.hasCamera(): Boolean {
    if (sHasCamera == null) {
        val pckMgr = packageManager
        val flag = pckMgr
            .hasSystemFeature("android.hardware.camera.front")
        val flag1 = pckMgr.hasSystemFeature("android.hardware.camera")
        val flag2: Boolean = flag || flag1
        sHasCamera = flag2
    }
    return sHasCamera == true
}

/**
 * 是否有硬件menu
 *
 */
fun Context.hasHardwareMenuKey(): Boolean {
    val flag: Boolean = when {
        Build.VERSION.SDK_INT < 11 -> true
        Build.VERSION.SDK_INT >= 14 -> {
            ViewConfiguration.get(this).hasPermanentMenuKey()
        }
        else -> false
    }
    return flag
}

/**
 * 是否有网络功能
 *
 * @return
 */
@SuppressLint("MissingPermission")
fun Context.hasInternet(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null
}

/**
 * 判断是否存在pckName包
 *
 * @param pckName 包名
 * @return
 */
fun Context.isPackageExist(pckName: String): Boolean {
    try {
        val pckInfo = packageManager
            .getPackageInfo(pckName, 0)
        if (pckInfo != null) return true
    } catch (ignored: PackageManager.NameNotFoundException) {
    }
    return false
}

/**
 * 判断 SDCard 是否加载结束
 *
 */
fun isSdcardReady(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment
        .getExternalStorageState()
}

/**
 * 获取当前国家的语言
 *
 * @return
 */
fun Context.getCurCountryLan(): String {
    val config = resources.configuration
    val sysLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.locales[0]
    } else {
        config.locale
    }
    return (sysLocale.language
            + "-"
            + sysLocale.country)
}

/**
 * 判断是否为中文环境
 *
 * @return
 */
fun Context.isZhCN(): Boolean {
    val config = resources.configuration
    val sysLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.locales[0]
    } else {
        config.locale
    }
    val lang = sysLocale.country
    return lang.equals("CN", ignoreCase = true)
}

/**
 * 设置全屏
 *
 */
fun Activity.setFullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * 取消全屏
 *
 */
fun Activity.cancelFullScreen() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    //这个不清除目前没影响
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

/**
 * 是否有导航栏
 */
fun Context.hasNavigationBar(): Boolean {
    val hasNav = deviceHasNavigationBar()
    if (!hasNav) {
        return false
    }
    return if (DeviceHelper.isVivo()) {
        vivoNavigationGestureEnabled()
    } else true
}

/**
 * 判断设备是否存在NavigationBar
 *
 * @return true 存在, false 不存在
 */
@SuppressLint("PrivateApi")
private fun deviceHasNavigationBar(): Boolean {
    var haveNav = false
    try {
        //1.通过WindowManagerGlobal获取windowManagerService
        // 反射方法：IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        val windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal")
        val getWmServiceMethod =
            windowManagerGlobalClass.getDeclaredMethod("getWindowManagerService")
        getWmServiceMethod.isAccessible = true
        //getWindowManagerService是静态方法，所以invoke null
        val iWindowManager = getWmServiceMethod.invoke(null)

        //2.获取windowMangerService的hasNavigationBar方法返回值
        // 反射方法：haveNav = windowManagerService.hasNavigationBar();
        val iWindowManagerClass: Class<*> = iWindowManager.javaClass
        val hasNavBarMethod = iWindowManagerClass.getDeclaredMethod("hasNavigationBar")
        hasNavBarMethod.isAccessible = true
        haveNav = hasNavBarMethod.invoke(iWindowManager) as Boolean
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return haveNav
}


/**
 * 获取 DisplayMetrics
 *
 * @return
 */
fun Context.getDisplayMetrics(): DisplayMetrics {
    return resources.displayMetrics
}


/**
 * 获取密度
 */
fun Context.getDensity(): Float {
    return resources.displayMetrics.density
}

/**
 * 获取字体密度
 */
fun Context.getFontDensity(): Float {
    return resources.displayMetrics.scaledDensity
}

/**
 * 获取屏幕宽度
 *
 * @return
 */
fun Context.getScreenWidth(): Int {
    return getDisplayMetrics().widthPixels
}

/**
 * 获取屏幕高度
 *
 * @return
 */
fun Context.getScreenHeight(): Int {
    var screenHeight = getDisplayMetrics().heightPixels
    if (DeviceHelper.isXiaomi() && xiaomiNavigationGestureEnabled()) {
        screenHeight += getResourceNavHeight()
    }
    return screenHeight
}

/**
 * 获取屏幕的真实宽高
 *
 * @return
 */
fun Context.getRealScreenSize(): IntArray {
    return doGetRealScreenSize()
}

/**
 * 获取真实屏幕尺寸
 */
private fun Context.doGetRealScreenSize(): IntArray {
    val size = IntArray(2)
    var widthPixels: Int
    var heightPixels: Int
    val w = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val d = w.defaultDisplay
    val metrics = DisplayMetrics()
    d.getMetrics(metrics)
    // since SDK_INT = 1;
    widthPixels = metrics.widthPixels
    heightPixels = metrics.heightPixels
    try {
        // used when 17 > SDK_INT >= 14; includes window decorations (statusbar bar/menu bar)
        widthPixels = Display::class.java.getMethod("getRawWidth").invoke(d) as Int
        heightPixels = Display::class.java.getMethod("getRawHeight").invoke(d) as Int
    } catch (ignored: Exception) {
    }
    if (Build.VERSION.SDK_INT >= 17) {
        try {
            // used when SDK_INT >= 17; includes window decorations (statusbar bar/menu bar)
            val realSize = Point()
            d.getRealSize(realSize)
            Display::class.java.getMethod("getRealSize", Point::class.java).invoke(d, realSize)
            widthPixels = realSize.x
            heightPixels = realSize.y
        } catch (ignored: Exception) {
        }
    }
    size[0] = widthPixels
    size[1] = heightPixels
    return size
}


/**
 * 获取vivo手机设置中的"navigation_gesture_on"值，判断当前系统是使用导航键还是手势导航操作
 *
 * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
 */
fun Context.vivoNavigationGestureEnabled(): Boolean {
    return Settings.Secure.getInt(contentResolver, "navigation_gesture_on", 0) != 0
}


fun Context.xiaomiNavigationGestureEnabled(): Boolean {
    return Settings.Global.getInt(contentResolver, "force_fsg_nav_bar", 0) != 0
}