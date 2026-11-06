package com.github.avbox.osc.util

import com.github.avbox.osc.BuildConfig
import com.github.avbox.osc.api.TmdbPoster

/**
 * 内置 TMDB Key 的一次性种子。
 *
 * 背景：源码里不带明文 key（避免入库泄露），但官方包体验上「装完就能看到
 * 演员阵容 / 分集剧名 / 分集截图」。做法是构建时用 -PTMDB_API_KEY 注入，
 * 首次启动再落到设置里，后续用户可以在 TMDB 页面自行修改。
 *
 * 只在用户完全没有配置过时写入，绝不覆盖已有设置。
 */
object TmdbDefaults {

    fun seedIfNeeded() {
        try {
            val builtIn = BuildConfig.TMDB_API_KEY_DEFAULT
            if (builtIn.isBlank()) return
            // 用户已经填过 key 或主动开过开关，就不动
            if (KV.get(KVKeys.TMDB_API_KEY, "").isNotBlank()) return
            if (KV.get(KVKeys.TMDB_ENABLE, false)) return

            KV.put(KVKeys.TMDB_API_KEY, builtIn)
            KV.put(KVKeys.TMDB_ENABLE, true)
            TmdbImages.notifyConfigChanged()
            TmdbPoster.notifyConfigChanged()
            AppLog.i("TmdbDefaults: seeded built-in TMDB key")
        } catch (th: Throwable) {
            // 种子失败不能影响启动
            AppLog.e("TmdbDefaults", th)
        }
    }

    /** 设置页用来提示「已内置」 */
    fun hasBuiltInKey(): Boolean = BuildConfig.TMDB_API_KEY_DEFAULT.isNotBlank()
}
