package me.maly.y9to.screen.auth

import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store


interface AuthViewModel {
    val store: Store<AuthScreenState, AuthScreenIntent, AuthScreenAction>
}
