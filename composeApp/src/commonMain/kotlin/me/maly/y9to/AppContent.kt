package me.maly.y9to

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import me.maly.y9to.compose.navigation.NavHost
import me.maly.y9to.navigation.destination.AuthDestination
import me.maly.y9to.navigation.destination.Destination
import me.maly.y9to.navigation.destination.FeedDestination
import me.maly.y9to.navigation.destination.MainFlowDestination


@Composable
fun AppContent(
    modifier: Modifier = Modifier,
) {
    val backStack = retain { mutableStateListOf<Destination>() }

    val destinations = retain {
        val authDestination = AuthDestination()
        val feedDestination = FeedDestination()
        val mainFlowDestination = MainFlowDestination(
            navigateAuthScreen = {
                backStack.clear()
                backStack.add(authDestination)
            }
        )

        backStack.add(mainFlowDestination)

        setOf(
            authDestination,
            feedDestination,
            mainFlowDestination,
        )
    }

    NavHost(
        backStack = backStack,
        modifier = modifier,
        destinations = destinations,
    )
}
