package me.maly.y9to

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.maly.y9to.screen.auth.AuthScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "y9to",
    ) {
//        App()

        AuthScreen()
    }
}