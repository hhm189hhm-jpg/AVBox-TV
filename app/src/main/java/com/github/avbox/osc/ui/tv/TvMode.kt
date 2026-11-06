package com.github.avbox.osc.ui.tv

import android.content.pm.PackageManager
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.avbox.osc.BuildConfig
import com.github.avbox.osc.util.AppContextHolder

/**
 * TV 形态判定。
 *
 * 有两层：
 * 1. [isTvBuild] —— 编译期 flavor。tv 包恒为 true，phone 包恒为 false。
 * 2. [isLeanbackDevice] —— 运行期探测。用来兜住「手机包被装到电视盒子上」这种情况，
 *    让同一份 UI 在电视上也能走对交互分支。
 *
 * UI 里统一用 [LocalIsTv] 取值，不要在 composable 里直接读常量，
 * 否则后续要做「强制 TV 模式」开关时无法动态切换。
 */
object TvMode {

    val isTvBuild: Boolean = BuildConfig.IS_TV

    val isLeanbackDevice: Boolean by lazy {
        runCatching {
            AppContextHolder.context()
                ?.packageManager
                ?.hasSystemFeature(PackageManager.FEATURE_LEANBACK) == true
        }.getOrDefault(false)
    }

    val enabled: Boolean
        get() = isTvBuild || isLeanbackDevice
}

val LocalIsTv = staticCompositionLocalOf { TvMode.enabled }
