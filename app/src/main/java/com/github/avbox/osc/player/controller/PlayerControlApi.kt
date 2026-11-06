package com.github.avbox.osc.player.controller

import android.webkit.WebView
import androidx.media3.ui.SubtitleView

import com.github.avbox.osc.bean.SourceBean
import com.github.avbox.osc.player.PlayerKernelView
import com.github.avbox.osc.player.state.PlayerUiState
import com.github.avbox.osc.subtitle.widget.SimpleSubtitleView
import org.json.JSONObject
import java.util.HashMap

interface PlayerControlApi {

    fun setKernelProvider(view: PlayerKernelView?) {
    }

    fun getSubtitleView(): SimpleSubtitleView

    fun getLyricView(): SimpleSubtitleView

    fun getExoSubtitleView(): SubtitleView

    fun getUiState(): PlayerUiState

    fun setListener(listener: VodControlListener?) {
    }

    fun setPlayerConfig(playerCfg: JSONObject)

    fun showParse(userJxList: Boolean)

    fun setPreviewMode(previewMode: Boolean)

    fun setTitle(playTitleInfo: String) {
    }

    fun setUrlTitle(playTitleInfo: String) {
    }

    fun setHasDanmu(hasDanmu: Boolean)

    fun setCanChangePosition(canChangePosition: Boolean)

    fun setEnableInNormal(enableInNormal: Boolean)

    fun setGestureEnabled(gestureEnabled: Boolean)

    fun toggleControlBar()

    fun hidePauseRoot()

    fun onNewPlayStarted(sameContent: Boolean)

    fun adoptCurrentContent()

    fun setLifecyclePaused(paused: Boolean)

    fun setExitPaused(paused: Boolean)

    fun resetSpeed()

    fun onBackPressed(): Boolean

    fun switchPlayer(): Boolean

    fun stopOther()

    fun playM3u8(url: String?, headers: HashMap<String, String>?)

    fun encodeUrl(url: String?): String

    fun firstUrlByArray(url: String?): String

    fun evaluateScript(sourceBean: SourceBean?, url: String?, view: WebView?)

    fun getWebPlayUrlIfNeeded(webPlayUrl: String?): String

    // ---- TV 遥控器控制 ----
    /** 播放/暂停。与底部栏的 OK 行为一致。 */
    fun togglePlay() {
    }

    /** 相对当前进度跳转，[deltaMs] 为负表示后退。 */
    fun seekBy(deltaMs: Long) {
    }

    /** 呼出控制栏。 */
    fun showControls() {
    }
}
