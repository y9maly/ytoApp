package me.maly.y9to.compose.asyncImage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale


interface AsyncImageOverride {
    @Composable
    operator fun Scope.invoke()

    @ConsistentCopyVisibility
    data class Scope internal constructor(
        val model: Any?,
        val contentDescription: String?,
        val modifier: Modifier,
        val alignment: Alignment,
        val contentScale: ContentScale,
        val alpha: Float,
        val colorFilter: ColorFilter?,
        val filterQuality: FilterQuality,
        val clipToBounds: Boolean,
    )

    companion object {
        val Local = compositionLocalOf<AsyncImageOverride> {
            error("Please provide 'AsyncImageOverride.Local'")
        }
    }
}
