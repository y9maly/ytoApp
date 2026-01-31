package me.maly.y9to.screen.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import y9to.api.types.AuthState
import y9to.sdk.Client


interface NavigationViewModel {
    val isAuthenticated: Flow<Boolean>
}

class NavigationDefaultViewModel(private val client: Client) : ViewModel(), NavigationViewModel {
    override val isAuthenticated = client.auth.authState
        .map { it is AuthState.Authorized }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}
