package me.maly.y9to

import androidx.compose.material.darkColors
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.material.MaterialTheme as Material2Theme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun main() = application {
    val windowState = rememberWindowState(
        width = 402.dp,
        height = 732.dp,
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "y9to",
        state = windowState,
    ) {
        Material2Theme(darkColors()) {
            App()
        }
    }
}
