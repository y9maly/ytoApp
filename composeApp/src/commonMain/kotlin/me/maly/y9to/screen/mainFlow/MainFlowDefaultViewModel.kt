package me.maly.y9to.screen.mainFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import me.maly.y9to.repository.AuthInfoRepository
import me.maly.y9to.repository.isAuthenticated


class MainFlowDefaultViewModel(
    authInfoRepository: AuthInfoRepository,
) : ViewModel(), MainFlowViewModel {
    override val isAuthenticated = authInfoRepository.isAuthenticated
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}
