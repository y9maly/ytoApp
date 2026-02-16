package me.maly.y9to.compose.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.window.singleWindowApplication

actual fun preview(content: @Composable (() -> Unit)) {
    singleWindowApplication { content() }
}

@OptIn(ExperimentalFoundationApi::class)
actual suspend fun PointerInputScope.detectMouseSecondaryGestures(
    onDoubleTap: ((Offset) -> Unit)?,
    onLongPress: ((Offset) -> Unit)?,
    onPress: suspend PressGestureScope.(Offset) -> Unit,
    onTap: ((Offset) -> Unit)?
) = detectTapGestures(
    matcher = PointerMatcher.mouse(PointerButton.Secondary),
    onDoubleTap = onDoubleTap,
    onLongPress = onLongPress,
    onPress = onPress,
    onTap = onTap,
)
