package me.maly.y9to.compose.utils

import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope

actual suspend fun PointerInputScope.detectMouseSecondaryGestures(
    onDoubleTap: ((Offset) -> Unit)?,
    onLongPress: ((Offset) -> Unit)?,
    onPress: suspend PressGestureScope.(Offset) -> Unit,
    onTap: ((Offset) -> Unit)?
) {
}