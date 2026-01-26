package me.maly.y9to.navigation.destination

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.maly.y9to.screen.feed.FeedScreen
import org.koin.compose.koinInject


class FeedDestination : Destination {
    @Composable
    override fun Content(modifier: Modifier) {
        FeedScreen(koinInject(), modifier)
    }
}
