package me.maly.y9to.screen.feed

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
