plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.chaquopy) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

/**
 * 目标 ABI 列表，由 `-PabiFilter=...` 指定，形如：
 *   ./gradlew :app:assembleRelease -PabiFilter=armeabi-v7a
 *   ./gradlew :app:assembleRelease -PabiFilter=arm64-v8a,armeabi-v7a
 *
 * 不传时保持 arm64-v8a 单架构，与既有 CI release 产物一致。
 * 注意：迅雷 SDK（libxl_stat / libxl_thunder_sdk）只随 arm64-v8a 分发，
 * 打 armeabi-v7a 时 app 侧 Thunder 会走 ABI 门禁降级，详见 Thunder.isNativeAvailable。
 */
val targetAbis: List<String> = (providers.gradleProperty("abiFilter").orNull)
    ?.split(',')
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?.takeIf { it.isNotEmpty() }
    ?: listOf("arm64-v8a")

extra["targetAbis"] = targetAbis
extra["abiFilterDeclared"] = providers.gradleProperty("abiFilter").isPresent
