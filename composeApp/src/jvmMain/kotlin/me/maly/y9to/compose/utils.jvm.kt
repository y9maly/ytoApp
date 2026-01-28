package me.maly.y9to.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.singleWindowApplication

actual fun preview(content: @Composable (() -> Unit)) {
    singleWindowApplication { content() }
}