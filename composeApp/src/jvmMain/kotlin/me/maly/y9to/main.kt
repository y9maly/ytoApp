package me.maly.y9to

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.MaterialTheme as Material2Theme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.maly.y9to.di.navigation.appNavigationModule
import me.maly.y9to.di.sdk.appSdkModule
import org.koin.compose.KoinApplication
import org.koin.core.module.Module

private val modules = GlobalScope.async {
    Module().apply {
        includes(appNavigationModule)
        includes(appSdkModule())
    }
}

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
            MaterialExpressiveTheme(colorScheme = darkColorScheme()) {
                val upstreamDensity = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides object : Density {
                    override val density = upstreamDensity.density * 0.9f
                    override val fontScale = upstreamDensity.fontScale * 0.9f
                }) {
                    var modulesAsync by retain { mutableStateOf<Module?>(null) }

                    LaunchedEffect(Unit) {
                        modulesAsync = modules.await()
                    }

                    val modules = modulesAsync ?: return@CompositionLocalProvider

                    KoinApplication({
                        modules(modules)
                    }) {
                        Scaffold {
                            AppContent(Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}
