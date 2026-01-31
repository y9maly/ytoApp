package me.maly.y9to.screen.auth

import pro.respawn.flowmvi.api.Store


interface AuthViewModel {
    val store: Store<AuthUiState, AuthScreenIntent, AuthScreenAction>
}
