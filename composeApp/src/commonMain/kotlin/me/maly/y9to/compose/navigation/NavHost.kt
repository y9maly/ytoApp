package me.maly.y9to.compose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.serialization.KSerializer
import me.maly.y9to.navigation.destination.Destination


@Composable
fun NavHost(
    backStack: List<Destination>,
    destinations: Set<Destination>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = entryProvider {
            destinations.forEach { destination ->
                entry(destination) {
                    it.Content(Modifier.fillMaxSize())
                }
            }
        })
}
