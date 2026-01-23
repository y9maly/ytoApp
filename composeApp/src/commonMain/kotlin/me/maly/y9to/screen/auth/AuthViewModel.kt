package me.maly.y9to.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import me.maly.y9to.types.UiLoginState
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInError
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.libs.stdlib.successOrElse


class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

}
