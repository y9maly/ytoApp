package me.maly.y9to.compose.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDateTime
import me.maly.y9to.compose.time.toLocalDateTime
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.NoHoverInteractionSource
import me.maly.y9to.compose.utils.thenIf
import me.maly.y9to.types.UiPostAction
import me.maly.y9to.types.UiPostAuthorPreview
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.cat1
import y9to.composeapp.generated.resources.deletedUser
import y9to.composeapp.generated.resources.repeat
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostHeader(
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    isRepost: Boolean,
    action: UiPostAction?,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = ripple(),
    contentPadding: ContentPadding = ContentPadding.Zero,
) = PostHeader(
    author = author,
    publishDate = publishDate,
    isRepost = isRepost,
    action = action,
    modifier = modifier,
    menu = menu,
    onClick = onClick,
    interactionSource = interactionSource,
    indication = indication,
    contentPadding = contentPadding,
    avatar = {
        val painter = when (author) {
            null -> null
            is UiPostAuthorPreview.User -> painterResource(Res.drawable.cat1)
            is UiPostAuthorPreview.DeletedUser -> painterResource(Res.drawable.deletedUser)
        }

        AnimatedContent(painter) { painter ->
            if (painter == null) {
                LoadingIndicator(Modifier.size(42.dp))
                return@AnimatedContent
            }

            Image(
                painter,
                if (author == null) "Avatar of unknown user"
                else "Avatar of user ${author.displayName}",
                Modifier.size(42.dp).clip(CircleShape)
            )
        }
    }
)

@Composable
fun PostHeader(
    avatar: @Composable () -> Unit,
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    isRepost: Boolean,
    action: UiPostAction?,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = ripple(),
    contentPadding: ContentPadding = ContentPadding.Zero,
) = Box(modifier.height(IntrinsicSize.Min)) {
    val density = LocalDensity.current
    val onClickLatest by rememberUpdatedState(onClick)
    val publishLocalDate = publishDate.toLocalDateTime()

    val interactionSource = interactionSource ?: remember { NoHoverInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 200, delayMillis = 80)
    )

    var containerCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    var authorClickableAnchorOffset by remember { mutableStateOf(0f) }

    Row(
        modifier = Modifier
            .onGloballyPositioned { containerCoordinates = it }
            .fillMaxWidth()
            .indication(interactionSource, indication)
            .padding(contentPadding)
            .scale(scale)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(42.dp)) {
            avatar()
        }

        Spacer(Modifier.width(8.dp))

        Column(
            Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isRepost) {
                    Icon(painterResource(Res.drawable.repeat), null, Modifier.size(24.dp))
                    Spacer(Modifier.width(4.dp))
                }

                when (author) {
                    is UiPostAuthorPreview.User, null -> {
                        Text(author?.displayName ?: "...", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                    }

                    is UiPostAuthorPreview.DeletedUser -> {
                        Text(author.displayName, fontStyle = FontStyle.Italic, fontSize = 16.sp)
                    }
                }

                // author clickable anchor
                Box(Modifier.offset(x = 8.dp).onGloballyPositioned {
                    authorClickableAnchorOffset = (containerCoordinates ?: return@onGloballyPositioned)
                        .localPositionOf(it)
                        .x
                })

                Text(" • ${publishLocalDate.formatToString()}", fontSize = 14.sp)
            }

            when (action) {
                null -> {}

                is UiPostAction.Deleted -> {
                    val deletionLocalDate = action.timestamp.toLocalDateTime()
                    Text("Deleted ${deletionLocalDate.formatToString()}", fontSize = 12.sp)
                }

                is UiPostAction.Edited -> {
                    val lastEditLocalDate = action.timestamp.toLocalDateTime()
                    Text("Edited ${lastEditLocalDate.formatToString()}", fontSize = 12.sp)
                }
            }
        }

        menu()
    }

    // author clickable area
    Box(Modifier
        .fillMaxHeight()
        .width(with(density) { authorClickableAnchorOffset.toDp() })
        .pointerHoverIcon(PointerIcon.Hand)
        .clickable(interactionSource, null, onClick != null) { onClickLatest?.invoke() }
        .thenIf(LocalInspectionMode.current) {
            drawBehind {
                drawLine(Color.Red, Offset(size.width, 0f), Offset(size.width, size.height), 1f)
            }
        }
    )
}

private fun LocalDateTime.formatToString() = buildString {
    append(day)
    append(" ")
    append(month.name.lowercase())
    append(" at ")
    if (hour <= 9)
        append(0)
    append(hour)
    append(":")
    if (minute <= 9)
        append(0)
    append(minute)
}
