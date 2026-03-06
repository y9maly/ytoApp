package me.maly.y9to

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.maly.y9to.compose.asyncImage.AsyncImageOverride
import me.maly.y9to.compose.asyncImage.CoilOverride
import me.maly.y9to.compose.fileImageRequests
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.di.navigation.appNavigationModule
import me.maly.y9to.di.repository.repositoryModule
import me.maly.y9to.di.sdk.appSdkModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.Module
import y9to.sdk.Client


private fun modules() = GlobalScope.async {
    Module().apply {
        includes(appSdkModule())
        includes(repositoryModule)
        includes(appNavigationModule)
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    MaterialExpressiveTheme(colorScheme = darkColorScheme()) {
        val upstreamDensity = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides object : Density {
            override val density = upstreamDensity.density * 0.85f
            override val fontScale = upstreamDensity.fontScale * 0.85f
        }) {
            var modulesAsync by retain { mutableStateOf<Module?>(null) }

            LaunchedEffect(Unit) {
                modulesAsync = modules().await()
            }

            val modules = modulesAsync

            if (modules == null) {
                Scaffold {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LoadingIndicator()
                    }
                }
                return@CompositionLocalProvider
            }

            KoinApplication({
                modules(modules)
            }) {
                val client: Client = koinInject()
                val coilPlatformContext = LocalPlatformContext.current
                val asyncImageOverride = remember(client, coilPlatformContext) {
                    AsyncImageOverride.CoilOverride(
                        imageLoader = ImageLoader.Builder(coilPlatformContext)
                            .components {
                                fileImageRequests(client)
                            }
                            .build()
                    )
                }

                CompositionLocalProvider(
                    AsyncImageOverride.Local provides asyncImageOverride
                ) {
                    Scaffold {
                        AppContent(
                            modifier = Modifier.fillMaxSize(),
                            baseContentPadding = PaddingValues(horizontal = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
