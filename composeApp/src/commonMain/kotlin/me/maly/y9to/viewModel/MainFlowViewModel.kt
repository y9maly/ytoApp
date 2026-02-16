package me.maly.y9to.viewModel

import kotlinx.coroutines.flow.Flow

interface MainFlowViewModel {
    val isAuthenticated: Flow<Boolean>
}