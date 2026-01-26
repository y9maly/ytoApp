package me.maly.y9to

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.maly.y9to.compose.navigation.NavHost
import me.maly.y9to.navigation.destination.AuthDestination
import me.maly.y9to.navigation.destination.FeedDestination


private val authDestination = AuthDestination()
private val feedDestination = FeedDestination()
private val appDestinations = setOf(
    authDestination,
    feedDestination,
)

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        initialDestination = feedDestination,
        destinations = appDestinations,
        serializer = null,
    )
}
