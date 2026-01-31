package me.maly.y9to.compose

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import androidx.compose.animation.AnimatedVisibility as _AnimatedVisibility


typealias ContentPadding = PaddingValues
object EmptyContentPadding : PaddingValues by PaddingValues.Zero

/**
 * ```
 * // Works only on jvm
 * fun main() = preview { Something() }
 * ```
 */
expect fun preview(content: @Composable () -> Unit)
fun previewColumn(content: @Composable () -> Unit) = preview { Column { content() } }

/**
 * works everywhere except android
 */
expect suspend fun PointerInputScope.detectMouseSecondaryGestures(
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onPress: suspend PressGestureScope.(Offset) -> Unit = { },
    onTap: ((Offset) -> Unit)? = null
)

/**
 * ```
 * Row { Box { AnimatedVisibility(state) {} } } // Compile-time error 🙄🙄🙄😒😒😒
 * Row { Box { null.AnimatedVisibility(state) {} } } // workaround
 * ```
 */
@Composable
fun Nothing?.AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    label: String = "AnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    _AnimatedVisibility(visible, modifier, enter, exit, label, content)
}

@Composable
fun <T> Flow<T>.collectAsStateUndispatched(initial: T, context: CoroutineContext = EmptyCoroutineContext): State<T> {
    val state = remember { mutableStateOf(initial) }
    val scope = rememberCoroutineScope()
    remember {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            collect { state.value = it }
        }
    }
    return state
}

inline val Placeable.size get() = IntSize(width, height)
inline val Constraints.maxSize get() = IntSize(maxWidth, maxHeight)

infix fun PaddingValues.plusAll(other: PaddingValues) = object : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@plusAll.calculateLeftPadding(layoutDirection) + other.calculateLeftPadding(layoutDirection)
    override fun calculateTopPadding(): Dp =
        this@plusAll.calculateTopPadding() + other.calculateTopPadding()
    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@plusAll.calculateRightPadding(layoutDirection) + other.calculateRightPadding(layoutDirection)
    override fun calculateBottomPadding(): Dp =
        this@plusAll.calculateBottomPadding() + other.calculateBottomPadding()
}

fun PaddingValues.plusHorizontal(other: PaddingValues) = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@plusHorizontal.calculateLeftPadding(layoutDirection) + other.calculateLeftPadding(layoutDirection)
    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@plusHorizontal.calculateRightPadding(layoutDirection) + other.calculateRightPadding(layoutDirection)
}

fun PaddingValues.plusVertical(other: PaddingValues) = object : PaddingValues by this {
    override fun calculateTopPadding(): Dp =
        this@plusVertical.calculateTopPadding() + other.calculateTopPadding()
    override fun calculateBottomPadding(): Dp =
        this@plusVertical.calculateBottomPadding() + other.calculateBottomPadding()
}

fun PaddingValues.plusLeft(other: PaddingValues) = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@plusLeft.calculateLeftPadding(layoutDirection) + other.calculateLeftPadding(layoutDirection)
}

fun PaddingValues.plusRight(other: PaddingValues) = object : PaddingValues by this {
    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@plusRight.calculateRightPadding(layoutDirection) + other.calculateRightPadding(layoutDirection)
}

fun PaddingValues.plusTop(other: PaddingValues) = object : PaddingValues by this {
    override fun calculateTopPadding(): Dp =
        this@plusTop.calculateTopPadding() + other.calculateTopPadding()
}

fun PaddingValues.plusBottom(other: PaddingValues) = object : PaddingValues by this {
    override fun calculateBottomPadding(): Dp =
        this@plusBottom.calculateBottomPadding() + other.calculateBottomPadding()
}


fun PaddingValues.plus(horizontal: Dp = 0.dp, vertical: Dp = 0.dp) = object : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@plus.calculateLeftPadding(layoutDirection) + horizontal
    override fun calculateTopPadding(): Dp =
        this@plus.calculateTopPadding() + vertical
    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@plus.calculateRightPadding(layoutDirection) + horizontal
    override fun calculateBottomPadding(): Dp =
        this@plus.calculateBottomPadding() + vertical
}


fun PaddingValues.takeVertical() = dropHorizontal()
fun PaddingValues.takeHorizontal() = dropVertical()

fun PaddingValues.dropStart() = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Ltr) 0.dp
        else this@dropStart.calculateLeftPadding(layoutDirection)
    override fun calculateRightPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Rtl) 0.dp
        else this@dropStart.calculateLeftPadding(layoutDirection)
}

fun PaddingValues.dropEnd() = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Rtl) 0.dp
        else this@dropEnd.calculateLeftPadding(layoutDirection)
    override fun calculateRightPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Ltr) 0.dp
        else this@dropEnd.calculateLeftPadding(layoutDirection)
}

fun PaddingValues.dropTop() = object : PaddingValues by this {
    override fun calculateTopPadding() = 0.dp
}

fun PaddingValues.dropBottom() = object : PaddingValues by this {
    override fun calculateBottomPadding() = 0.dp
}


fun PaddingValues.dropHorizontal() = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = 0.dp
    override fun calculateRightPadding(layoutDirection: LayoutDirection) = 0.dp
}

fun PaddingValues.dropVertical() = object : PaddingValues by this {
    override fun calculateTopPadding() = 0.dp
    override fun calculateBottomPadding() = 0.dp
}

fun PaddingValues.takeTop() = object : PaddingValues by this {
    override fun calculateBottomPadding() = 0.dp
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = 0.dp
    override fun calculateRightPadding(layoutDirection: LayoutDirection) = 0.dp
}

fun PaddingValues.takeBottom() = object : PaddingValues by this {
    override fun calculateTopPadding() = 0.dp
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = 0.dp
    override fun calculateRightPadding(layoutDirection: LayoutDirection) = 0.dp
}

fun PaddingValues.takeStart() = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Ltr) this@takeStart.calculateLeftPadding(layoutDirection)
        else 0.dp

    override fun calculateRightPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Ltr) 0.dp
        else this@takeStart.calculateLeftPadding(layoutDirection)
}

fun PaddingValues.takeEnd() = object : PaddingValues by this {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Ltr) 0.dp
        else this@takeEnd.calculateLeftPadding(layoutDirection)

    override fun calculateRightPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Ltr) this@takeEnd.calculateLeftPadding(layoutDirection)
        else 0.dp
}