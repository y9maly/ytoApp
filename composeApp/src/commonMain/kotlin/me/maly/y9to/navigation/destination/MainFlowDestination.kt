package me.maly.y9to.navigation.destination

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.maly.y9to.screen.mainFlow.MainFlowScreen
import org.koin.compose.koinInject


class MainFlowDestination(
    private val navigateAuthScreen: () -> Unit
) : Destination {
    @Composable
    override fun Content(modifier: Modifier) {
        MainFlowScreen(koinInject(), navigateAuthScreen, modifier)
    }
}
