package me.maly.y9to.screen.postDetails

import me.maly.y9to.types.UiPost


sealed interface PostDetailsUiState {
    data object Loading : PostDetailsUiState

    data class Content(val post: UiPost) : PostDetailsUiState

    data object PostWasDeleted : PostDetailsUiState

    data class Error(val message: String) : PostDetailsUiState
}
