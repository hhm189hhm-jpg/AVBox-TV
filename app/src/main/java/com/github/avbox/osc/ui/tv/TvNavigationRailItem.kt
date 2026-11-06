package com.github.avbox.osc.ui.tv

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

/**
 * 侧栏导航项。非 TV 形态下等价于原生 NavigationRailItem；
 * TV 形态下补焦点描边，并在获得焦点时回调（用于「焦点即切页」）。
 *
 * 注意 onFocusChanged 必须排在 selectable 之前才能观测到焦点，
 * 这里靠 modifier 参数会被 NavigationRailItem 前置拼接来保证顺序。
 */
@Composable
fun TvNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFocused: (() -> Unit)? = null,
) {
    val isTv = LocalIsTv.current
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)

    // 切页必须放到焦点事件之外做。直接在 onFocusChanged 里改 pager 状态，
    // 会在焦点结算过程中触发重组和滚动，Compose 会把焦点再往下推一格
    // （实测按一次 DOWN 会从 Home 跳到 Live，页面却停在中途的 Records）。
    LaunchedEffect(focused) {
        if (focused) onFocused?.invoke()
    }

    val focusModifier = if (!isTv) {
        modifier
    } else {
        modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { focused = it.isFocused }
            .tvFocusRing(shape = shape, focused = focused)
    }

    NavigationRailItem(
        modifier = focusModifier,
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
    )
}
