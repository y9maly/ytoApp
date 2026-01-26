package me.maly.y9to.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import pro.respawn.flowmvi.api.Store
import y9to.api.types.AuthState
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.sdk.Client


interface FeedHeaderComponent {
    val state: StateFlow<FeedHeaderState>
}

class FeedHeaderComponentDefault(private val client: Client) : ViewModel(), FeedHeaderComponent {
    override val state = MutableStateFlow<FeedHeaderState>(FeedHeaderState.Loading)

    init {
        combine(client.auth.authState, client.user.me) { authState, me ->
            if (authState is AuthState.Unauthorized) {
                state.value = FeedHeaderState.Unauthenticated
            } else if (me == null) {
                state.value = FeedHeaderState.Loading
            } else {
                state.value = FeedHeaderState.Authenticated(
                    firstName = me.firstName,
                    lastName = me.lastName,
                )
            }
        }.collectIn(viewModelScope)
    }
}
