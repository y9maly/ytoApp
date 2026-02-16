package me.maly.y9to.viewModel

import kotlinx.coroutines.flow.StateFlow
import me.maly.y9to.viewModel.PostDetailsUiState

interface PostDetailsViewModel {
    val state: StateFlow<PostDetailsUiState>
}