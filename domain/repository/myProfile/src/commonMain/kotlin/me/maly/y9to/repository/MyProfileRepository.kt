package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.io.Source
import me.maly.y9to.types.EditProfileProperties
import me.maly.y9to.types.UploadAvatarError
import me.maly.y9to.types.UploadAvatarState
import me.maly.y9to.types.UploadCoverError
import me.maly.y9to.types.UploadCoverState
import y9to.api.types.EditMeError
import y9to.api.types.FileId
import y9to.api.types.MyProfile
import y9to.common.types.Birthday
import y9to.libs.stdlib.Union
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.none


interface MyProfileRepository {
    val myProfile: Flow<MyProfile?> // null id unauthenticated
    val canLogOut: Flow<Boolean>
    val editProperties: Flow<EditProfileProperties>
    val uploadCoverState: Flow<UploadCoverState>
    val uploadAvatarState: Flow<UploadAvatarState>

    suspend fun edit(
        firstName: Optional<String> = none(),
        lastName: Optional<String?> = none(),
        bio: Optional<String?> = none(),
        birthday: Optional<Birthday?> = none(),
        cover: Optional<FileId?> = none(),
        avatar: Optional<FileId?> = none(),
    ): Union<Unit, EditMeError>

    fun cancelUploadCover()
    fun cancelUploadAvatar()
    suspend fun uploadCover(filename: String, filesize: Long?, source: Source): Union<Unit, UploadCoverError>
    suspend fun uploadAvatar(filename: String, filesize: Long?, source: Source): Union<Unit, UploadAvatarError>

    /**
     * @return false if [canLogOut] == false
     */
    suspend fun logOut(): Boolean
}
