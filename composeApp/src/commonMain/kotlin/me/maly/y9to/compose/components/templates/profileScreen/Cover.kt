package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.maly.y9to.compose.ContentPadding
import me.maly.y9to.compose.EmptyContentPadding
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.CanUpload
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.Uploading
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay.Default
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.compose.dropEnd


@Composable
internal fun Cover(
    overlay: CoverOverlay,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        content()
        CoverOverlay(overlay, Modifier.matchParentSize())
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
        val cancelUploading: () -> Unit,
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
        .background(Color.Black.copy(alpha = dim))
        .clickable(enabled = overlay is CanUpload) {
            (overlay as? CanUpload)?.upload()
        },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(overlay, Modifier.matchParentSize()) { overlay ->
            when (overlay) {
                is Default -> {}

                is CanUpload -> {
                    Text("Click here", color = Color.White)
                }

                is Uploading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.White,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Uploading…", color = Color.White)
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
