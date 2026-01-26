package me.maly.y9to.navigation.destination

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


interface Destination {
    @Composable
    fun Content(modifier: Modifier = Modifier)
}
