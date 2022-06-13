package com.baimsg.chat.util.extensions

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.baimsg.chat.util.DeviceHelper
import java.lang.reflect.Field

/**
 *
 * create by baimsg 2021/11/24
 * Email 1469010683@qq.com
 *
 **/

private const val STATUS_BAR_TYPE_DEFAULT = 0
private const val STATUS_BAR_TYPE_MIUI = 1
private const val STATUS_BAR_TYPE_FLYME = 2
private const val STATUS_BAR_TYPE_ANDROID6 = 3 // Android 6.0
private const val STATUS_BAR_DEFAULT_HEIGHT_DP = 25f // 大部分状态栏都是25dp

// 在某些机子上存在不同的density值，所以增加两个虚拟值
var sVirtualDensity = -1f
private var sStatusBarHeight = -1


@StatusBarType
private var mStatusBarType: Int = STATUS_BAR_TYPE_DEFAULT


/**
 * 沉浸状态栏
 */
fun Activity.translucent() {
    window.translucent(0x40000000)
}

/**
 * 沉浸状态栏
 */
fun Activity.translucent(@ColorInt colorOn5x: Int) {
    window.translucent(colorOn5x)
}

/**
 * 沉浸状态栏
 */
fun Window.translucent() {
    translucent(0x40000000)
}

/**
 * 沉浸状态栏
 */
fun Window.translucent(@ColorInt colorOn5x: Int) {
    // 版本小于4.4，绝对不考虑沉浸式
    if (!supportTranslucent()) {
        return
    }

    if (DeviceHelper.isNotchOfficialSupport()) {
        if (ViewCompat.isAttachedToWindow(decorView)) {
            realHandleDisplayCutoutMode(
                this,
                decorView
            )
        } else {
            decorView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    realHandleDisplayCutoutMode(
                        this@translucent,
                        v
                    )
                }

                override fun onViewDetachedFromWindow(v: View) {}
            })
        }
    }

    // 小米和魅族4.4 以上版本支持沉浸式
    // 小米 Android 6.0 ，开发版 7.7.13 及以后版本设置黑色字体又需要 clear FLAG_TRANSLUCENT_STATUS, 因此还原为官方模式
    if (DeviceHelper.isFlymeLowerThan(8) || DeviceHelper.isMIUI() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
        )
        return
    }

    var systemUiVisibility: Int = decorView.systemUiVisibility
    systemUiVisibility =
        systemUiVisibility or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    decorView.systemUiVisibility = systemUiVisibility
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && supportTransplantStatusBar6()) {
        // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
        // ZUK Z1是个另类，自家应用可以实现字体颜色变色，但没开放接口
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        //延伸到状态栏
//        addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        statusBarColor = Color.TRANSPARENT
    } else {
        // android 5不能修改状态栏字体颜色，因此直接用FLAG_TRANSLUCENT_STATUS，nexus表现为半透明
        // 魅族和小米的表现如何？
        // update: 部分手机运用FLAG_TRANSLUCENT_STATUS时背景不是半透明而是没有背景了。。。。。
//                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 采取setStatusBarColor的方式，部分机型不支持，那就纯黑了，保证状态栏图标可见
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        //延伸到状态栏
//        addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        statusBarColor = colorOn5x
    }

}

/**
 * Essential Phone 在 Android 8 之前沉浸式做得不全，系统不从状态栏顶部开始布局却会下发 WindowInsets
 */
private fun supportTranslucent(): Boolean {
    return !(DeviceHelper.isEssentialPhone() && Build.VERSION.SDK_INT < 26)
}

/**
 * 真实手柄显示抠图模式
 * @param window
 * @param decorView
 */
@TargetApi(28)
private fun realHandleDisplayCutoutMode(window: Window, decorView: View) {
    if (decorView.rootWindowInsets != null &&
        decorView.rootWindowInsets.displayCutout != null
    ) {
        val params = window.attributes
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = params
    }
}

/**
 * 检测 Android 6.0 是否可以启用 window.setStatusBarColor(Color.TRANSPARENT)。
 */
private fun supportTransplantStatusBar6(): Boolean {
    return !(DeviceHelper.isZUKZ1() || DeviceHelper.isZTKC2016())
}


/**
 * 设置状态栏黑色字体图标，
 * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
 *
 */
fun Activity.setStatusBarLightMode(): Boolean {
    // 无语系列：ZTK C2016只能时间和电池图标变色。。。。
    if (DeviceHelper.isZTKC2016()) {
        return false
    }
    if (mStatusBarType != STATUS_BAR_TYPE_DEFAULT) {
        return setStatusBarLightMode(
            mStatusBarType
        )
    }
    if (isMIUICustomStatusBarLightModeImpl() && window.miuiSetStatusBarLightMode(
            true
        )
    ) {
        mStatusBarType =
            STATUS_BAR_TYPE_MIUI
        return true
    } else if (window.flymeSetStatusBarLightMode(
            true
        )
    ) {
        mStatusBarType =
            STATUS_BAR_TYPE_FLYME
        return true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.android6SetStatusBarLightMode(
            true
        )
        mStatusBarType =
            STATUS_BAR_TYPE_ANDROID6
        return true
    }
    return false
}

/**
 * 设置状态栏黑色字体图标
 * @param type 状态栏类型
 */
fun Activity.setStatusBarLightMode(@StatusBarType type: Int): Boolean {
    when (type) {
        STATUS_BAR_TYPE_MIUI -> {
            return window.miuiSetStatusBarLightMode(
                true
            )
        }
        STATUS_BAR_TYPE_FLYME -> {
            return window.flymeSetStatusBarLightMode(
                true
            )
        }
        STATUS_BAR_TYPE_ANDROID6 -> {
            return window.android6SetStatusBarLightMode(
                true
            )
        }
        else -> return false
    }
}

/**
 * 设置状态栏白色字体图标
 * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
 */
fun Activity.setStatusBarDarkMode(): Boolean {
    if (mStatusBarType == STATUS_BAR_TYPE_DEFAULT) {
        // 默认状态，不需要处理
        return true
    }
    when (mStatusBarType) {
        STATUS_BAR_TYPE_MIUI -> {
            return window.miuiSetStatusBarLightMode(
                false
            )
        }
        STATUS_BAR_TYPE_FLYME -> {
            return window.flymeSetStatusBarLightMode(
                false
            )
        }
        STATUS_BAR_TYPE_ANDROID6 -> {
            return window.android6SetStatusBarLightMode(
                false
            )
        }
        else -> return true
    }
}

/**
 * 设置状态栏字体图标为深色，需要 MIUIV6 以上
 *
 * @param light  是否把状态栏字体及图标颜色设置为深色
 * @return boolean 成功执行返回 true
 */
fun Window.miuiSetStatusBarLightMode(light: Boolean): Boolean {
    var result = false
    val clazz: Class<*> = this.javaClass
    try {
        val darkModeFlag: Int
        val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
        val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
        darkModeFlag = field.getInt(layoutParams)
        val extraFlagField = clazz.getMethod(
            "setExtraFlags",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )
        if (light) {
            extraFlagField.invoke(this, darkModeFlag, darkModeFlag) //状态栏透明且黑色字体
        } else {
            extraFlagField.invoke(this, 0, darkModeFlag) //清除黑色字体
        }
        result = true
    } catch (ignored: Exception) {
    }
    return result
}

/**
 * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回Android原生实现
 * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
 */
private fun isMIUICustomStatusBarLightModeImpl(): Boolean {
    return if (DeviceHelper.isMIUIV9() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        true
    } else DeviceHelper.isMIUIV5() || DeviceHelper.isMIUIV6() ||
            DeviceHelper.isMIUIV7() || DeviceHelper.isMIUIV8()
}

/**
 * 设置状态栏图标为深色和魅族特定的文字风格
 * 可以用来判断是否为 Flyme 用户
 *
 * @param light  是否把状态栏字体及图标颜色设置为深色
 * @return boolean 成功执行返回true
 */
fun Window.flymeSetStatusBarLightMode(light: Boolean): Boolean {
    var result = false
    android6SetStatusBarLightMode(light)
    // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
    // 高版本调用这个出现不可预期的 Bug,官方文档也没有给出完整的高低版本兼容方案
    if (DeviceHelper.isFlymeLowerThan(7)) {
        try {
            val lp = this.attributes
            val darkFlag = WindowManager.LayoutParams::class.java
                .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java
                .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (light) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            this.attributes = lp
            result = true
        } catch (ignored: Exception) {
        }
    } else if (DeviceHelper.isFlyme()) {
        result = true
    }
    return result
}

/**
 * 设置状态栏字体图标为深色，Android 6
 *
 * @param window 需要设置的窗口
 * @param light  是否把状态栏字体及图标颜色设置为深色
 * @return boolean 成功执行返回true
 */
@TargetApi(23)
private fun Window.android6SetStatusBarLightMode(light: Boolean): Boolean {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        val insetsController = WindowCompat.getInsetsController(this, decorView)
        if (insetsController != null) {
            insetsController.isAppearanceLightStatusBars = light
        }
    } else {
        // 经过测试，小米 Android 11 用  WindowInsetsControllerCompat 不起作用， 我还能说什么呢。。。
        val decorView = decorView
        var systemUi = decorView.systemUiVisibility
        systemUi = if (light) {
            systemUi or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            systemUi and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        decorView.systemUiVisibility = systemUi
    }
    if (DeviceHelper.isMIUIV9()) {
        // MIUI 9 低于 6.0 版本依旧只能回退到以前的方案
        // https://github.com/Tencent/QMUI_Android/issues/160
        this.miuiSetStatusBarLightMode(light)
    }
    return true
}


/**
 * 获取是否全屏
 *
 * @return 是否全屏
 */
fun Activity.isFullScreen(): Boolean {
    var ret = false
    try {
        val attrs = window.attributes
        ret = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ret
}

/**
 * 获取状态栏的高度。
 */
fun Context.getStatusBarHeight(): Int {
    if (sStatusBarHeight == -1) {
        initStatusBarHeight()
    }
    return sStatusBarHeight
}

/**
 * 加载状态栏高度
 */
private fun Context.initStatusBarHeight() {
    val clazz: Class<*>
    var obj: Any? = null
    var field: Field? = null
    try {
        clazz = Class.forName("com.android.internal.R\$dimen")
        obj = clazz.newInstance()
        if (DeviceHelper.isMeizu()) {
            try {
                field = clazz.getField("status_bar_height_large")
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        if (field == null) {
            field = clazz.getField("status_bar_height")
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    if (field != null && obj != null) {
        try {
            val id = field[obj].toString().toInt()
            sStatusBarHeight =
                resources.getDimensionPixelSize(id)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
    if (sStatusBarHeight <= 0) {
        sStatusBarHeight = if (sVirtualDensity == -1f) {
            dp2px(STATUS_BAR_DEFAULT_HEIGHT_DP).toInt()
        } else {
            ((STATUS_BAR_DEFAULT_HEIGHT_DP * sVirtualDensity + 0.5f).toInt())
        }
    }
}

@IntDef(
    STATUS_BAR_TYPE_DEFAULT,
    STATUS_BAR_TYPE_MIUI,
    STATUS_BAR_TYPE_FLYME,
    STATUS_BAR_TYPE_ANDROID6
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
private annotation class StatusBarType