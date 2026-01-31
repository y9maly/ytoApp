package me.maly.y9to.compose.viewModel

import androidx.compose.runtime.Composable
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.ParametersDefinition


@Composable
inline fun <reified VM : Any> injectViewModel(noinline parameters: ParametersDefinition): VM {
    val koinScope = currentKoinScope()
    return viewModel { koinScope.get<VM>(parameters = parameters) }
}

@Composable
inline fun <reified VM : Any> injectViewModel(): VM {
    val koinScope = currentKoinScope()
    return viewModel { koinScope.get<VM>() }
}
