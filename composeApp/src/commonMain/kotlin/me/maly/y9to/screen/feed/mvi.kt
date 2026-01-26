package me.maly.y9to.screen.feed

import pro.respawn.flowmvi.api.MVIState


sealed interface FeedHeaderState : MVIState {
    data object Loading : FeedHeaderState

    data class Authenticated(
        val firstName: String,
        val lastName: String?,
    ) : FeedHeaderState

    data object Unauthenticated : FeedHeaderState

    data class Error(val message: String) : FeedHeaderState
}
