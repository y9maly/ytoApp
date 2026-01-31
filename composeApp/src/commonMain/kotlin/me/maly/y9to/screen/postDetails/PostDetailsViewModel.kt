package me.maly.y9to.screen.postDetails

import kotlinx.coroutines.flow.StateFlow


interface PostDetailsViewModel {
    val state: StateFlow<PostDetailsUiState>
}
