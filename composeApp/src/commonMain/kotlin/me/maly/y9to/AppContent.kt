package me.maly.y9to

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.EmptyContentPadding
import me.maly.y9to.compose.viewModel.injectViewModel
import me.maly.y9to.navigation.destination.Destination
import me.maly.y9to.navigation.destination.MainFlowDestination
import me.maly.y9to.screen.navigation.NavigationScreen


@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    baseContentPadding: ContentPadding = EmptyContentPadding,
) {
    val backStack = retain { mutableStateListOf<Destination>().apply {
        add(MainFlowDestination)
    } }

    NavigationScreen(
        vm = injectViewModel(),
        backStack = backStack,
        modifier = modifier,
        baseContentPadding = baseContentPadding,
    )
}
