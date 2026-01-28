package me.maly.y9to.screen.mainFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import y9to.api.types.AuthState
import y9to.sdk.Client


class MainFlowDefaultViewModel(client: Client) : ViewModel(), MainFlowViewModel {
    override val isAuthenticated = client.auth.authState
        .map { it is AuthState.Authorized }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}
