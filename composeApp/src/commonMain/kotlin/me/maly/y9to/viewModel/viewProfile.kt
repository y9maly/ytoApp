package me.maly.y9to.viewModel

import me.maly.y9to.types.UiProfile


sealed interface ViewProfileUiState {
    data object Loading : ViewProfileUiState

    data class Content(
        val profile: UiProfile,
    ) : ViewProfileUiState

    data class Error(
        val message: String,
    ) : ViewProfileUiState
}
