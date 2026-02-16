package me.maly.y9to.viewModel

import pro.respawn.flowmvi.api.MVIState


sealed interface FeedHeaderUiState : MVIState {
    data object Loading : FeedHeaderUiState

    data class Authenticated(
        val firstName: String,
        val lastName: String?,
    ) : FeedHeaderUiState

    data object Unauthenticated : FeedHeaderUiState

    data class Error(val message: String) : FeedHeaderUiState
}

val FeedHeaderUiState.Authenticated.displayName get() =
    lastName?.let { "$firstName $lastName" } ?: firstName
