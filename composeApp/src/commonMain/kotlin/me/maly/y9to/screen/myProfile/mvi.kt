package me.maly.y9to.screen.myProfile

import me.maly.y9to.types.UiMyProfile
import me.maly.y9to.types.UiUploadAvatarState
import me.maly.y9to.types.UiUploadCoverState
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIState


sealed interface MyProfileUiState : MVIState {
    data object Loading : MyProfileUiState

    data class Content(
        val myProfile: UiMyProfile,
        val uiFirstMyProfile: UiMyProfile,
        val committingChanges: Boolean, // true when commit() was called and wait server response
        val uploadCoverState: UiUploadCoverState,
        val uploadAvatarState: UiUploadAvatarState,
        val canLogOut: Boolean,
    ) : MyProfileUiState

    data class Error(val message: String) : MyProfileUiState
}

sealed interface MyProfileScreenAction : MVIAction {
    data class ShowMessage(val text: String) : MyProfileScreenAction
}

val MyProfileUiState.Content.uiFirstFirstName get() = uiFirstMyProfile.firstName
val MyProfileUiState.Content.uiFirstLastName get() = uiFirstMyProfile.lastName
val MyProfileUiState.Content.uiFirstDisplayName get() =
    uiFirstLastName?.let { "$uiFirstFirstName $it" }
    ?: uiFirstFirstName

val MyProfileUiState.Content.uiFirstBio get() = uiFirstMyProfile.bio

val MyProfileUiState.Content.uiFirstBirthday get() = uiFirstMyProfile.birthday
