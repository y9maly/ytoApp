package me.maly.y9to.navigation.destination

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.maly.y9to.screen.auth.AuthScreen
import org.koin.compose.koinInject


class AuthDestination : Destination {
    @Composable
    override fun Content(modifier: Modifier) {
        AuthScreen(koinInject(), modifier)
    }
}
