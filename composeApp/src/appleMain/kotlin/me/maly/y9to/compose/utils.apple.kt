package me.maly.y9to.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerInputScope


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
