package me.maly.y9to.compose.utils

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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import androidx.compose.animation.AnimatedVisibility as _AnimatedVisibility


typealias ContentPadding = PaddingValues
object EmptyContentPadding : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = 0.dp
    override fun calculateTopPadding() = 0.dp
    override fun calculateRightPadding(layoutDirection: LayoutDirection) = 0.dp
    override fun calculateBottomPadding() = 0.dp
}

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

@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
inline fun Modifier.thenIf(condition: Boolean, then: Modifier.() -> Modifier): Modifier {
    contract {
        callsInPlace(then, InvocationKind.AT_MOST_ONCE)
        condition holdsIn then
    }
    return if (condition) then(this) else this
}
