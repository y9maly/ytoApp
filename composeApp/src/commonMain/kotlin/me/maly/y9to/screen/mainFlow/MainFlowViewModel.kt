package me.maly.y9to.screen.mainFlow

import kotlinx.coroutines.flow.Flow


interface MainFlowViewModel {
    val isAuthenticated: Flow<Boolean>
}
