package com.github.avbox.osc.ui.tv

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring

/** 焦点态放大倍数。TV 上 1.05 偏弱、1.12 会压到相邻卡片，1.08 是常用值。 */
const val TvFocusScale: Float = 1.08f

/** 焦点描边宽度。10-foot UI 下 2dp 在 1080p 上几乎看不见。 */
val TvFocusRingWidth: Dp = 3.dp

/**
 * 焦点描边。
 *
 * 不用 `Modifier.border` 直接画：描边以轮廓为中线，外侧一半会被父容器裁掉。
 * 这里把宽度翻倍，配合 clip 正好露出 [width]。
 * 位置必须在 clip 之后，border 会先画内容再压描边，不会被卡片内容盖住。
 */
@Composable
fun Modifier.tvFocusRing(
    shape: Shape,
    focused: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    width: Dp = TvFocusRingWidth,
): Modifier {
    if (!focused) return this
    return this.border(width = width * 2, color = color, shape = shape)
}

/**
 * 记录焦点状态，并把焦点态映射成缩放。
 *
 * 返回的 Modifier 顺序很关键：graphicsLayer 必须在 clip 之前，
 * 否则放大部分会被裁掉。
 */
@Composable
fun Modifier.tvFocusScale(
    scaleWhenFocused: Float = TvFocusScale,
    onFocusChanged: ((Boolean) -> Unit)? = null,
): Modifier {
    val isTv = LocalIsTv.current
    var focused by remember { mutableStateOf(false) }
    val animated by animateFloatAsState(
        targetValue = if (isTv && focused) scaleWhenFocused else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tvFocusScale",
    )
    return this
        .onFocusChanged {
            if (focused != it.isFocused) {
                focused = it.isFocused
                onFocusChanged?.invoke(it.isFocused)
            }
        }
        .graphicsLayer {
            scaleX = animated
            scaleY = animated
        }
}

/**
 * 页面级初始焦点。
 *
 * Compose 在窗口获得焦点时会自动把焦点给层级里的第一个可聚焦节点，
 * 但那个节点通常不可预测（往往是顶部栏的某个图标）。TV 上必须显式指定，
 * 否则用户按方向键的第一下会跳到奇怪的位置。
 */
@Composable
fun TvInitialFocus(
    requester: FocusRequester,
    enabled: Boolean = true,
    key: Any? = Unit,
) {
    val isTv = LocalIsTv.current
    LaunchedEffect(key, isTv, enabled) {
        if (!isTv || !enabled) return@LaunchedEffect
        // 等两帧，确保目标节点已经 attach 到焦点树
        withFrameNanos { }
        withFrameNanos { }
        runCatching { requester.requestFocus() }
    }
}

/**
 * 给可滚动容器留出焦点放大后的余量，避免边缘卡片被容器裁切。
 * 仅在 TV 形态下加内边距。
 */
@Composable
fun Modifier.tvFocusSafePadding(): Modifier =
    if (LocalIsTv.current) this.padding(TvFocusRingWidth) else this

/**
 * 给任意可点击组件补焦点描边。
 *
 * 必须排在 clickable / selectable **之前**，否则 onFocusChanged 观测不到焦点。
 * 非 TV 形态下原样返回，不影响手机端。
 */
@Composable
fun Modifier.tvFocusHighlight(
    shape: Shape = RoundedCornerShape(12.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    width: Dp = TvFocusRingWidth,
): Modifier {
    if (!LocalIsTv.current) return this
    var focused by remember { mutableStateOf(false) }
    return this
        .onFocusChanged { focused = it.isFocused }
        .tvFocusRing(shape = shape, focused = focused, color = color, width = width)
}
