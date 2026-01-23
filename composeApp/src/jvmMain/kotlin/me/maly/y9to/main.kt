package me.maly.y9to

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.GlobalScope
import me.maly.y9to.screen.auth.AuthScreen
import me.maly.y9to.screen.auth.store

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "y9to",
    ) {
//        App()

        store.start(GlobalScope)
        AuthScreen(Modifier.fillMaxSize(), store)
    }
}