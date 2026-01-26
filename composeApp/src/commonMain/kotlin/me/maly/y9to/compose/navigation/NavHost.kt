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
    initialDestination: Destination,
    destinations: Set<Destination>,
    serializer: KSerializer<Destination>?,
    modifier: Modifier = Modifier,
) {
    val backStack = if (serializer != null) {
        rememberSerializable(
            serializer = SnapshotStateListSerializer(serializer)
        ) { mutableStateListOf(initialDestination) }
    } else {
        retain { mutableStateListOf(initialDestination) }
    }

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
