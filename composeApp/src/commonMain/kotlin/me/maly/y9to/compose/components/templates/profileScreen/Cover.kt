package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.maly.y9to.compose.ContentPadding
import me.maly.y9to.compose.EmptyContentPadding
import me.maly.y9to.compose.components.CircularProgressIndicatorWithIcon
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.CanUpload
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.Default
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.UploadError
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.Uploading
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.compose.dropEnd
import me.maly.y9to.compose.takeHorizontal
import me.maly.y9to.compose.takeTop
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.close
import y9to.composeapp.generated.resources.replay


@Composable
internal fun Cover(
    overlay: CoverOverlay,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    Box(modifier) {
        content()
        CoverOverlay(overlay, Modifier.matchParentSize(), contentPadding)
    }
}

sealed interface CoverOverlay {
    val leadingIcon: (@Composable () -> Unit)?
    val trailingIcon: (@Composable () -> Unit)?

    data class Default(
        override val leadingIcon: @Composable (() -> Unit)? = null,
        override val trailingIcon: @Composable (() -> Unit)? = null,
    ) : CoverOverlay

    data class CanUpload(
        override val leadingIcon: @Composable (() -> Unit)? = null,
        override val trailingIcon: @Composable (() -> Unit)? = null,
        val upload: () -> Unit,
    ) : CoverOverlay

    data class Uploading(
        override val leadingIcon: @Composable (() -> Unit)? = null,
        override val trailingIcon: @Composable (() -> Unit)? = null,
        val cancelUpload: () -> Unit,
    ) : CoverOverlay

    data class UploadError(
        override val leadingIcon: @Composable (() -> Unit)? = null,
        override val trailingIcon: @Composable (() -> Unit)? = null,
        val explanation: String,
        val cancelUpload: () -> Unit,
        val tryAnotherOne: () -> Unit,
        val retry: (() -> Unit)?, // null if can't retry
    ) : CoverOverlay
}

@Composable
private fun CoverOverlay(
    overlay: CoverOverlay,
    modifier: Modifier = Modifier,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val overlay by rememberUpdatedState(overlay)
    val isDimmed = overlay !is Default
    val dim by animateFloatAsState(if (isDimmed) .5f else 0f)

    Box(modifier
        .padding(contentPadding.takeHorizontal())
        .background(Color.Black.copy(alpha = dim))
        .clickable(enabled = overlay is CanUpload || overlay is UploadError) {
            (overlay as? CanUpload)?.upload()
            (overlay as? UploadError)?.tryAnotherOne()
        },
    ) {
        AnimatedContent(
            modifier = Modifier.matchParentSize(),
            targetState = overlay,
            transitionSpec = {
                fadeIn() + scaleIn(initialScale = .8f) togetherWith
                        fadeOut() + scaleOut(targetScale = .8f)
            },
        ) { overlay ->
            Box(
                Modifier
                    .padding(contentPadding.takeTop())
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (overlay) {
                    is Default -> {}

                    is CanUpload -> {
                        Box(Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Text("Tap to change the cover", color = Color.White)
                        }
                    }

                    is Uploading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicatorWithIcon(
                                modifier = Modifier.size(42.dp),
                                color = Color.White,
                            ) {
                                IconButton(overlay.cancelUpload) {
                                    Icon(painterResource(Res.drawable.close), null)
                                }
                            }

                            Spacer(Modifier.width(8.dp))
                            Text("Uploading…", color = Color.White)
                        }
                    }

                    is UploadError -> {
                        Row(Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(overlay.cancelUpload) {
                                Icon(painterResource(Res.drawable.close), null)
                            }

                            Text(overlay.explanation, color = Color.White)

                            overlay.retry?.let { retry ->
                                IconButton(retry) {
                                    Icon(painterResource(Res.drawable.replay), null)
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedContent(
            overlay.leadingIcon,
            Modifier
                .align(Alignment.TopStart)
                .padding(contentPadding.dropEnd().dropBottom())
        ) {
            it?.invoke()
        }

        AnimatedContent(
            overlay.trailingIcon,
            Modifier
                .align(Alignment.TopEnd)
                .padding(contentPadding.dropEnd().dropBottom())
        ) {
            it?.invoke()
        }
    }
}
