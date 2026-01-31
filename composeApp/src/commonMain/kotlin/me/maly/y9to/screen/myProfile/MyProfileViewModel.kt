package me.maly.y9to.screen.myProfile

import pro.respawn.flowmvi.api.Store


interface MyProfileViewModel {
    val store: Store<MyProfileUiState, MyProfileScreenIntent, Nothing>
}
