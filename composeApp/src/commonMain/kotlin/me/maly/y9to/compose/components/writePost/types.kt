package me.maly.y9to.compose.components.writePost

import androidx.compose.runtime.Composable


sealed interface WriteAccount {
    val avatar: @Composable () -> Unit
    val displayName: String

    data class Personal(
        override val avatar: @Composable () -> Unit,
        val firstName: String,
        val lastName: String?,
    ) : WriteAccount {
        override val displayName
            get() = lastName?.let { "$firstName $it" } ?: firstName
    }
}
