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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import me.maly.y9to.compose.components.CircularProgressIndicatorWithIcon
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay.CanUpload
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay.Default
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay.Uploading
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.close
import y9to.composeapp.generated.resources.error
import y9to.composeapp.generated.resources.photo_camera


@Composable
internal fun Avatar(
    overlay: AvatarOverlay,
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier.clip(shape)) {
        content()
        AvatarOverlay(overlay, Modifier.fillMaxSize())
    }
}

sealed interface AvatarOverlay {
    data object Default : AvatarOverlay
    data class CanUpload(val upload: () -> Unit) : AvatarOverlay
    data class Uploading(val cancelUpload: () -> Unit) : AvatarOverlay
    data class UploadError(
        val explanation: String,
        val cancelUpload: () -> Unit,
        val tryAnotherOne: () -> Unit,
        val retry: (() -> Unit)?, // null if can't retry
    ) : AvatarOverlay
}

@Composable
private fun AvatarOverlay(
    overlay: AvatarOverlay,
    modifier: Modifier = Modifier,
) {
    val overlay by rememberUpdatedState(overlay)
    val isDimmed = overlay !is Default
    val dim by animateFloatAsState(if (isDimmed) .5f else 0f)

    Box(
        modifier
            .background(Color.Black.copy(alpha = dim))
            .clickable(enabled = overlay is CanUpload) {
                (overlay as? CanUpload)?.upload()
            },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            overlay,
            Modifier.matchParentSize(),
            transitionSpec = {
                fadeIn() + scaleIn(initialScale = .8f) togetherWith
                        fadeOut() + scaleOut(targetScale = .8f)
            },
        ) { overlay ->
            Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                when (overlay) {
                    is Default -> {}

                    is CanUpload -> {
                        Icon(
                            painter = painterResource(Res.drawable.photo_camera),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(.5f).align(Alignment.Center),
                            tint = Color.White,
                        )
                    }

                    is Uploading -> {
                        CircularProgressIndicatorWithIcon(
                            modifier = Modifier.fillMaxSize(.5f),
                            color = Color.White,
                        ) {
                            IconButton(overlay.cancelUpload, Modifier.fillMaxSize()) {
                                Icon(painterResource(Res.drawable.close), null)
                            }
                        }
                    }

                    is AvatarOverlay.UploadError -> {
                        IconButton(overlay.cancelUpload, Modifier.matchParentSize()) {
                            Icon(painterResource(Res.drawable.error), null)
                        }
                    }
                }
            }
        }
    }
}
