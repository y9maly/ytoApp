@file:Suppress("UnnecessaryVariable")

package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.roundToIntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import me.maly.y9to.compose.size
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.edit
import kotlin.math.roundToInt


@Immutable
private data class HeaderLayout(
    val size: Size,
    val avatarOffset: Offset,
    val avatarSize: Size,
    val coverOffset: Offset,
    val coverSize: Size,
    val cutOffset: Offset,
    val coverDim: Float,
    val coverBlur: Dp,
    val nameOffset: Offset,
    val cutSize: Size,
    val cutAlpha: Float,
) {
    val avatarConstraints get() = Constraints.fixed(
        width = avatarSize.width.roundToInt(),
        height = avatarSize.height.roundToInt(),
    )

    val coverConstraints get() = Constraints.fixed(
        width = coverSize.width.roundToInt(),
        height = coverSize.height.roundToInt(),
    )
}

private fun MeasureScope.expanded(
    constraints: Constraints,
    height: Float,
    namePlaceable: Placeable,
): HeaderLayout {
    val nameTopOffset = 8.dp.toPx()

    val size = Size(
        width = constraints.maxWidth.toFloat(),
        height = height,
    )

    val avatarSize = avatarSize.toPx().let { Size(it, it) }

    var avatarOffset = Alignment.BottomCenter.align(
        avatarSize.roundToIntSize(),
        size.roundToIntSize(),
        layoutDirection,
    ).toOffset()
    avatarOffset = avatarOffset.copy(
        y = avatarOffset.y - namePlaceable.height - nameTopOffset
    )

    val coverSize = Size(
        width = constraints.maxWidth.toFloat(),
        height = (constraints.maxHeight - (avatarSize.height / 2)).coerceAtLeast(0f)
    )

    val cutOffset = Offset(
        x = avatarOffset.x - avatarBorderWidth.toPx(),
        y = avatarOffset.y - avatarBorderWidth.toPx()
    )
    val cutSize = Size(
        width = avatarSize.width + avatarBorderWidth.toPx()*2,
        height = avatarSize.height + avatarBorderWidth.toPx()*2,
    )

    val nameOffset = Alignment.BottomCenter.align(
        namePlaceable.size,
        size.roundToIntSize(),
        layoutDirection,
    ).toOffset()

    return HeaderLayout(
        size = size,
        avatarOffset = avatarOffset,
        avatarSize = avatarSize,
        coverOffset = Offset.Zero,
        coverSize = coverSize,
        coverDim = 0f,
        coverBlur = 0.dp,
        cutOffset = cutOffset,
        cutSize = cutSize,
        cutAlpha = 1f,
        nameOffset = nameOffset,
    )
}

private fun MeasureScope.collapsed(
    constraints: Constraints,
    namePlaceable: Placeable,
): HeaderLayout {
    val avatarStartOffset = 8.dp.toPx()
    val nameStartOffset = 8.dp.toPx()

    val avatarSize = collapsedAvatarSize.toPx().let { Size(it, it) }

    val coverSize = Size(
        width = constraints.maxWidth.toFloat(),
        height = collapsedCoverHeight.toPx()
    )

    var avatarOffset = collapsedAvatarAlignment.align(
        avatarSize.roundToIntSize(),
        coverSize.roundToIntSize(),
        layoutDirection
    ).toOffset()
    avatarOffset = avatarOffset.copy(x = avatarOffset.x + avatarStartOffset)

    val cutOffset = avatarOffset
    val cutSize = Size(avatarSize.width - 2f, avatarSize.height -2f)

    val size = coverSize

    val nameOffset = Offset(
        x = avatarStartOffset + avatarSize.width + nameStartOffset,
        y = Alignment.CenterVertically.align(namePlaceable.height, size.height.roundToInt()).toFloat()
    )

    return HeaderLayout(
        size = coverSize,
        avatarOffset = avatarOffset,
        avatarSize = avatarSize,
        coverOffset = Offset.Zero,
        coverSize = coverSize,
        coverDim = .6f,
        coverBlur = 12.dp,
        cutOffset = cutOffset,
        cutSize = cutSize,
        cutAlpha = 0f,
        nameOffset = nameOffset,
    )
}

private fun lerp(start: HeaderLayout, end: HeaderLayout, fraction: Float): HeaderLayout {
    return HeaderLayout(
        size = lerp(start.size, end.size, fraction),
        avatarSize = lerp(start.avatarSize, end.avatarSize, fraction),
        avatarOffset = lerp(start.avatarOffset, end.avatarOffset, fraction),
        coverOffset = lerp(start.coverOffset, end.coverOffset, fraction),
        coverSize = lerp(start.coverSize, end.coverSize, fraction),
        coverDim = lerp(start.coverDim, end.coverDim, fraction),
        coverBlur = lerp(start.coverBlur, end.coverBlur, fraction),
        cutSize = lerp(start.cutSize, end.cutSize, fraction),
        cutOffset = lerp(start.cutOffset, end.cutOffset, fraction),
//        cutAlpha = lerp(start.cutAlpha, end.cutAlpha, fraction),
        cutAlpha = lerp(start.cutAlpha, end.cutAlpha, EaseInQuad.transform(fraction)),
//        EaseInQuad, EaseInCubic
        nameOffset = lerp(start.nameOffset, end.nameOffset, CubicBezierEasing(0f, 0f, 0.35f, 0f).transform(fraction)),
    )
}

internal object HeaderDefaults {
    fun displayName(string: String): @Composable (collapsedFraction: Float) -> Unit = { collapsedFraction ->
        Text(
            text = string,
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = lerp(colorScheme.onSurface, Color.White, collapsedFraction)
        )
    }

    fun displayNameWithEdit(
        string: String,
        editable: Boolean,
        onEdit: () -> Unit
    ): @Composable (collapsedFraction: Float) -> Unit = { collapsedFraction ->
        Row(Modifier.height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = string,
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium,
                color = lerp(colorScheme.onSurface, Color.White, collapsedFraction)
            )

            AnimatedVisibility(editable, Modifier.align(Alignment.CenterVertically)) {
                Row {
                    Spacer(Modifier.width(4.dp))

                    IconButton(onEdit, Modifier.fillMaxHeight().aspectRatio(1f / 1f)) {
                        Icon(painterResource(Res.drawable.edit), null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun Header(
    scrollBehavior: TopAppBarScrollBehavior,
    cover: @Composable () -> Unit,
    avatar: @Composable () -> Unit,
    coverOverlay: CoverOverlay,
    avatarOverlay: AvatarOverlay,
    displayName: @Composable (collapsedFraction: Float) -> Unit,
    modifier: Modifier = Modifier,
    expandedHeight: Dp = 220.dp,
    collapsedHeight: Dp = 64.dp,
) {
    SubcomposeLayout(modifier) { constraints ->
        val collapsedFraction = scrollBehavior.state.heightOffset / scrollBehavior.state.heightOffsetLimit

        val namePlaceable = subcompose(0) {
            displayName(collapsedFraction)
        }.single().measure(constraints.copyMaxDimensions())

        val expandedConstraints = Constraints.fixedHeight(expandedHeight.roundToPx())
        val collapsedConstraints = Constraints.fixedHeight(collapsedHeight.roundToPx())
        val expanded = expanded(constraints.constrain(expandedConstraints), expandedHeight.toPx(), namePlaceable)
        val collapsed = collapsed(constraints.constrain(collapsedConstraints), namePlaceable)
        scrollBehavior.state.heightOffsetLimit = collapsed.size.height - expanded.size.height
        scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset.coerceIn(minimumValue = scrollBehavior.state.heightOffsetLimit, maximumValue = 0f)
        val current = lerp(expanded, collapsed, collapsedFraction)

        val avatarPlaceable = subcompose(1) {
            Avatar(
                avatarOverlay,
                avatarShape,
                Modifier.fillMaxSize(),
                avatar,
            )
        }.single().measure(current.avatarConstraints)

        val coverPlaceable = subcompose(2) {
            Box {
                Cover(
                    coverOverlay,
                    Modifier
                        .matchParentSize()
                        .blur(current.coverBlur)
                        .cut(
                            cutSize = current.cutSize,
                            alpha = current.cutAlpha,
                            shape = avatarBorderShape,
                            offset = current.cutOffset
                        ),
                    cover,
                )

                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = current.coverDim)))
            }
        }.single().measure(current.coverConstraints)

        layout(current.size.width.roundToInt(), current.size.height.roundToInt()) {
            coverPlaceable.place(current.coverOffset.round())
            avatarPlaceable.place(current.avatarOffset.round())
            namePlaceable.place(current.nameOffset.round())
        }
    }
}


internal fun Modifier.cut(
    cutSize: Size,
    shape: Shape,
    alpha: Float = 1f,
    alignment: Alignment = Alignment.TopStart,
    offset: Offset = Offset.Zero,
    scale: Float = 1f,
) = this
    .graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }
    .drawWithCache {
        val scaledCutSize =
            if (scale == 1f)
                cutSize
            else
                Size(
                    width = cutSize.width * scale,
                    height = cutSize.height * scale
                )

        val outline = shape.createOutline(
            size = scaledCutSize,
            layoutDirection = layoutDirection,
            density = this
        )
        val blendMode = if (alpha < 1f) BlendMode.DstOut else BlendMode.Clear
        val space = this.size
        val align = alignment.align(
            size = cutSize.roundToIntSize(),
            space = IntSize(space.width.roundToInt(), space.height.roundToInt()),
            layoutDirection = layoutDirection
        )

        onDrawWithContent {
            drawContent()
            drawContext.transform.translate(
                align.x.toFloat() + offset.x + ((cutSize.width - scaledCutSize.width) / 2),
                align.y.toFloat() + offset.y + ((cutSize.height - scaledCutSize.height) / 2)
            )
            drawOutline(
                outline = outline,
                color = Color.Red,
                alpha = alpha,
                blendMode = blendMode
            )
        }
    }
