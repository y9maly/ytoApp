package me.maly.y9to.compose.utils

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.Flow


class NoHoverInteractionSource(
    private val underlying: MutableInteractionSource = MutableInteractionSource()
) : MutableInteractionSource {
    override val interactions: Flow<Interaction> = underlying.interactions

    override suspend fun emit(interaction: Interaction) {
        if (interaction is HoverInteraction)
            return
        underlying.emit(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        if (interaction is HoverInteraction)
            return true
        return underlying.tryEmit(interaction)
    }
}
