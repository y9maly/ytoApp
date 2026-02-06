package me.maly.y9to.screen.myProfile

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import me.maly.y9to.compose.FileImageRequest
import me.maly.y9to.repository.MyProfileRepository
import me.maly.y9to.types.UiMyProfile
import me.maly.y9to.types.UiUploadAvatarError
import me.maly.y9to.types.UiUploadAvatarState
import me.maly.y9to.types.UiUploadCoverError
import me.maly.y9to.types.UiUploadCoverState
import me.maly.y9to.types.UploadAvatarError
import me.maly.y9to.types.UploadAvatarState
import me.maly.y9to.types.UploadCoverError
import me.maly.y9to.types.UploadCoverState
import y9to.common.types.Birthday
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.getOrElse
import y9to.libs.stdlib.optional.none
import y9to.libs.stdlib.optional.onPresent
import y9to.libs.stdlib.optional.present


class MyProfileDefaultViewModel(
    private val repository: MyProfileRepository,
) : ViewModel(), MyProfileViewModel {
    private val canLogOut = repository.canLogOut
        .shareIn(viewModelScope, Eagerly, replay = 1)
    private val myProfile = repository.myProfile
        .shareIn(viewModelScope, Eagerly, replay = 1)
    private val uploadCoverState = repository.uploadCoverState
        .shareIn(viewModelScope, Eagerly, replay = 1)
    private val uploadAvatarState = repository.uploadAvatarState
        .shareIn(viewModelScope, Eagerly, replay = 1)

    private val editedFirstName = MutableStateFlow<Optional<String>>(none())
    private val editedLastName = MutableStateFlow<Optional<String?>>(none())
    private val editedBio = MutableStateFlow<Optional<String?>>(none())
    private val editedBirthday = MutableStateFlow<Optional<Birthday?>>(none())
    private val removingCover = MutableStateFlow(false)
    private val removingAvatar = MutableStateFlow(false)
    private val committingChanges = MutableStateFlow(false)

    override val state = moleculeFlow<MyProfileUiState>(Immediate) {
        val canLogOut = canLogOut.collectAsState(null).value
            ?: return@moleculeFlow MyProfileUiState.Loading
        val myProfile = myProfile.collectAsState(null).value
            ?: return@moleculeFlow MyProfileUiState.Loading
        val uploadCoverState = uploadCoverState.collectAsState(null).value
            ?: return@moleculeFlow MyProfileUiState.Loading
        val uploadAvatarState = uploadAvatarState.collectAsState(null).value
            ?: return@moleculeFlow MyProfileUiState.Loading
        val editedFirstName by editedFirstName.collectAsState()
        val editedLastName by editedLastName.collectAsState()
        val editedBio by editedBio.collectAsState()
        val editedBirthday by editedBirthday.collectAsState()
        val removingCover by removingCover.collectAsState()
        val removingAvatar by removingAvatar.collectAsState()
        val committingChanges by committingChanges.collectAsState()

        val uiMyProfile = UiMyProfile(
            userId = myProfile.id.long.toString(),
            firstName = myProfile.firstName,
            lastName = myProfile.lastName,
            cover = myProfile.cover?.let { FileImageRequest(it) },
            avatar = myProfile.avatar?.let { FileImageRequest(it) },
            phoneNumber = myProfile.phoneNumber,
            email = myProfile.email,
            bio = myProfile.bio,
            birthday = myProfile.birthday,
        )

        val uiFirstMyProfile = uiMyProfile.copy(
            firstName = editedFirstName.getOrElse { uiMyProfile.firstName },
            lastName = editedLastName.getOrElse { uiMyProfile.lastName },
            bio = editedBio.getOrElse { uiMyProfile.bio },
            birthday = editedBirthday.getOrElse { uiMyProfile.birthday },
            cover = uiMyProfile.cover.takeIf { !removingCover },
            avatar = uiMyProfile.avatar.takeIf { !removingAvatar },
        )

        val uiUploadCoverState = uploadCoverState.map()
        val uiUploadAvatarState = uploadAvatarState.map()

        MyProfileUiState.Content(
            myProfile = uiMyProfile,
            uiFirstMyProfile = uiFirstMyProfile,
            committingChanges = committingChanges,
            uploadCoverState = uiUploadCoverState,
            uploadAvatarState = uiUploadAvatarState,
            canLogOut = canLogOut,
        )
    }.stateIn(viewModelScope, Eagerly, MyProfileUiState.Loading)

    override val actions = MutableSharedFlow<MyProfileScreenAction>()

    override fun edit(
        firstName: Optional<String>,
        lastName: Optional<String?>,
        bio: Optional<String?>,
        birthday: Optional<Birthday?>,
    ) {
        firstName.onPresent { editedFirstName.value = firstName }
        lastName.onPresent { editedLastName.value = lastName }
        bio.onPresent { editedBio.value = bio }
        birthday.onPresent { editedBirthday.value = birthday }
    }

    override fun removeCover() {
        viewModelScope.launch {
            try {
                removingCover.value = true
                repository.edit(cover = present(null))
            } finally {
                removingCover.value = false
            }
        }
    }

    override fun removeAvatar() {
        viewModelScope.launch {
            try {
                removingAvatar.value = true
                repository.edit(avatar = present(null))
            } finally {
                removingAvatar.value = false
            }
        }
    }

    override fun uploadCover(file: PlatformFile) {
        viewModelScope.launch {
            repository.uploadCover(
                filename = file.name,
                filesize = file.size(),
                source = Buffer().apply {
                    write(file.readBytes())
                },
            )
        }
    }

    override fun uploadAvatar(file: PlatformFile) {
        viewModelScope.launch {
            repository.uploadAvatar(
                filename = file.name,
                filesize = file.size(),
                source = Buffer().apply {
                    write(file.readBytes())
                },
            )
        }
    }

    override fun cancelUploadCover() {
        repository.cancelUploadCover()
    }

    override fun cancelUploadAvatar() {
        repository.cancelUploadAvatar()
    }

    override fun retryUploadCover() {
        TODO("Not yet implemented")
    }

    override fun retryUploadAvatar() {
        TODO("Not yet implemented")
    }

    override fun applyChanges() {
        viewModelScope.launch {
            committingChanges.value = true
            try {
                val currentProfile = myProfile.first() ?: return@launch

                repository.edit(
                    firstName = editedFirstName.value
                        .presentIfDiffers(currentProfile.firstName),
                    lastName = editedLastName.value
                        .presentIfDiffers(currentProfile.lastName),
                    bio = editedBio.value
                        .presentIfDiffers(currentProfile.bio),
                    birthday = editedBirthday.value
                        .presentIfDiffers(currentProfile.birthday),
                )
            } finally {
                discardChanges()
                committingChanges.value = false
            }
        }
    }

    override fun discardChanges() {
        editedFirstName.value = none()
        editedLastName.value = none()
        editedBio.value = none()
        editedBirthday.value = none()
    }

    override fun logOut() {
        viewModelScope.launch {
            repository.logOut()
        }
    }
}

private fun UploadCoverState.map() = when (this) {
    is UploadCoverState.None -> UiUploadCoverState.None
    is UploadCoverState.Error -> UiUploadCoverState.Error(error.map())
    is UploadCoverState.Uploading -> UiUploadCoverState.Uploading
}

private fun UploadCoverError.map() = when (this) {
    is UploadCoverError.TooBigFile -> UiUploadCoverError.TooBigFile
    is UploadCoverError.StorageQuotaExceeded -> UiUploadCoverError.StorageQuotaExceeded
    is UploadCoverError.ConnectionError -> UiUploadCoverError.ConnectionError
    is UploadCoverError.UnknownError -> UiUploadCoverError.UnknownError(message)
}

private fun UploadAvatarState.map() = when (this) {
    is UploadAvatarState.None -> UiUploadAvatarState.None
    is UploadAvatarState.Error -> UiUploadAvatarState.Error(error.map())
    is UploadAvatarState.Uploading -> UiUploadAvatarState.Uploading
}

private fun UploadAvatarError.map() = when (this) {
    is UploadAvatarError.TooBigFile -> UiUploadAvatarError.TooBigFile
    is UploadAvatarError.StorageQuotaExceeded -> UiUploadAvatarError.StorageQuotaExceeded
    is UploadAvatarError.ConnectionError -> UiUploadAvatarError.ConnectionError
    is UploadAvatarError.UnknownError -> UiUploadAvatarError.UnknownError(message)
}

private fun <T> Optional<T>.presentIfDiffers(other: T): Optional<T> =
    presentIf(this != other)

private fun <T> Optional<T>.presentIf(condition: Boolean): Optional<T> =
    if (condition) this
    else none()
