package me.maly.y9to.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.maly.y9to.compose.FileImageRequest
import me.maly.y9to.repository.ViewUserRepository
import me.maly.y9to.types.UiProfile
import y9to.api.types.UserId
import y9to.libs.stdlib.coroutines.flow.collectLatestIn


class ViewProfileDefaultViewModel(
    private val uiUserId: String,
    private val repository: ViewUserRepository,
) : ViewModel(), ViewProfileViewModel {
    override val state = MutableStateFlow<ViewProfileUiState>(ViewProfileUiState.Loading)

    init {
        val userId = uiUserId.toLongOrNull()?.let(::UserId)

        if (userId != null) {
            repository.getFlow(userId).collectLatestIn(viewModelScope) { user ->
                if (user == null) {
                    state.value = ViewProfileUiState.Error("Invalid user id $uiUserId")
                    return@collectLatestIn
                }

                val uiProfile = UiProfile(
                    userId = uiUserId,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    cover = user.cover?.let(::FileImageRequest),
                    avatar = user.avatar?.let(::FileImageRequest),
                    phoneNumber = user.phoneNumber,
                    email = user.email,
                    bio = user.bio,
                    birthday = user.birthday,
                )

                state.value = ViewProfileUiState.Content(uiProfile)
            }
        } else {
            state.value = ViewProfileUiState.Error("Invalid user id $uiUserId")
        }
    }
}
