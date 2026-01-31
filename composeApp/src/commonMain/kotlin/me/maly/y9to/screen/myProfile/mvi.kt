package me.maly.y9to.screen.myProfile

import me.maly.y9to.screen.myProfile.MyProfileUiState.Edit
import me.maly.y9to.screen.myProfile.MyProfileUiState.View
import me.maly.y9to.types.UiMyProfile
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import y9to.common.types.Birthday
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.none


sealed interface MyProfileUiState : MVIState {
    data object Loading : MyProfileUiState

    sealed interface Content : MyProfileUiState {
        val myProfile: UiMyProfile
    }

    data class View(override val myProfile: UiMyProfile) : Content

    data class Edit(
        override val myProfile: UiMyProfile,
        val firstName: String,
        val lastName: String?,
        val bio: String?,
        val birthday: Birthday?,
    ) : Content

    data class Error(val message: String) : MyProfileUiState
}

val MyProfileUiState.Content.uiFirstFirstName get() =
    if (this is Edit) firstName
    else myProfile.firstName

val MyProfileUiState.Content.uiFirstLastName get() =
    if (this is Edit) lastName
    else myProfile.lastName

val MyProfileUiState.Content.uiFirstDisplayName get() =
    if (this is Edit) lastName?.let { "$firstName $lastName" } ?: firstName
    else myProfile.displayName

val MyProfileUiState.Content.uiFirstBio get() =
    if (this is Edit) bio
    else myProfile.bio

val MyProfileUiState.Content.uiFirstBirthday get() =
    if (this is Edit) birthday
    else myProfile.birthday

fun MyProfileUiState.Content.copy(
    myProfile: UiMyProfile = this.myProfile,
) = when (this) {
    is Edit -> copy(myProfile = myProfile)
    is View -> copy(myProfile = myProfile)
}

sealed interface MyProfileScreenIntent : MVIIntent {
    data object EnterEditMode : MyProfileScreenIntent

    data class Edit(
        val firstName: Optional<String> = none(),
        val lastName: Optional<String?> = none(),
        val bio: Optional<String?> = none(),
        val birthday: Optional<Birthday?> = none(),
    ) : MyProfileScreenIntent

    data class ExitEditMode(val applyChanges: Boolean) : MyProfileScreenIntent

    data object LogOut : MyProfileScreenIntent
}
