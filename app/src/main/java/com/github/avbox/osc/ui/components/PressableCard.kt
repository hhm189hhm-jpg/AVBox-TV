package com.github.avbox.osc.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.github.avbox.osc.ui.tv.LocalIsTv
import com.github.avbox.osc.ui.tv.TvFocusScale
import com.github.avbox.osc.ui.tv.tvFocusRing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val isTv = LocalIsTv.current
    var focused by remember { mutableStateOf(false) }

    // 按压缩放与焦点缩放相乘：TV 上按 OK 时既有焦点放大，也有按压反馈
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale",
    )
    val focusScale by animateFloatAsState(
        targetValue = if (isTv && focused) TvFocusScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tvFocusScale",
    )
    val ringColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer {
                val s = pressScale * focusScale
                scaleX = s
                scaleY = s
            }
            .clip(shape)
            .tvFocusRing(shape = shape, focused = isTv && focused, color = ringColor)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                onLongClick = onLongClick,
                onClick = onClick,
            ),
        content = content,
    )
}
