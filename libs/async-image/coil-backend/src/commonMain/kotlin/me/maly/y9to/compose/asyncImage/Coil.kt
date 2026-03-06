@file:Suppress("FunctionName")

package me.maly.y9to.compose.asyncImage

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.AsyncImage as CoilAsyncImage


fun AsyncImageOverride.Companion.CoilOverride(
    imageLoader: ImageLoader,
): AsyncImageOverride {
    return AsyncImageCoilOverride(imageLoader)
}

internal class AsyncImageCoilOverride(
    private val imageLoader: ImageLoader,
) : AsyncImageOverride {
    @Composable
    override fun AsyncImageOverride.Scope.invoke() {
        CoilAsyncImage(
            model = model,
            contentDescription = contentDescription,
            imageLoader = imageLoader,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            clipToBounds = clipToBounds,
        )
    }
}
