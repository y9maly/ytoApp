package me.maly.y9to.screen.myProfile

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import y9to.common.types.Birthday
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.none


interface MyProfileViewModel {
    val state: StateFlow<MyProfileUiState>
    val actions: Flow<MyProfileScreenAction>

    fun edit(
        firstName: Optional<String> = none(),
        lastName: Optional<String?> = none(),
        bio: Optional<String?> = none(),
        birthday: Optional<Birthday?> = none(),
    )

    fun removeCover()
    fun removeAvatar()
    fun uploadCover(file: PlatformFile)
    fun uploadAvatar(file: PlatformFile)
    fun cancelUploadCover()
    fun cancelUploadAvatar()
    fun retryUploadCover()
    fun retryUploadAvatar()

    fun applyChanges()
    fun discardChanges()

    fun logOut()
}
