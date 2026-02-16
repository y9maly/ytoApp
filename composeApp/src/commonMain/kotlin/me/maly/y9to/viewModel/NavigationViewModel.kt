package me.maly.y9to.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import me.maly.y9to.repository.AuthInfoRepository
import me.maly.y9to.repository.isAuthenticated


interface NavigationViewModel {
    val isAuthenticated: Flow<Boolean>
}
