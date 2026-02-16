package me.maly.y9to.viewModel

import kotlinx.coroutines.flow.StateFlow


interface ViewProfileViewModel {
    val state: StateFlow<ViewProfileUiState>
}
