package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay.CanUpload
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay.Default
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay.Uploading
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
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
    data class Uploading(val cancelUploading: () -> Unit) : AvatarOverlay
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
        AnimatedContent(overlay) { state ->
            when (state) {
                is Default -> {}
                
                is CanUpload -> {
                    Icon(
                        painter = painterResource(Res.drawable.photo_camera),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.5f).align(Alignment.Center),
                        tint = Color.White,
                    )
                }
                
                is Uploading -> {
                    CircularProgressIndicator(
                        Modifier.fillMaxSize(0.5f).align(Alignment.Center),
                        Color.White
                    )
                }
            }
        }
    }
}
