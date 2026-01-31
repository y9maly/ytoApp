package me.maly.y9to.screen.myProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.maly.y9to.types.UiMyProfile
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.getOrElse
import y9to.libs.stdlib.optional.none
import y9to.libs.stdlib.optional.present
import y9to.sdk.Client


class MyProfileDefaultViewModel(private val client: Client) : ViewModel(), MyProfileViewModel {
    override val store = store<MyProfileUiState, MyProfileScreenIntent, Nothing>(
        MyProfileUiState.Loading,
        viewModelScope
    ) {
        whileSubscribed {
            client.user.myProfile.collect { myProfile ->
                if (myProfile == null) {
                    updateState {
                        MyProfileUiState.Error("Unauthenticated")
                    }
                    return@collect
                }

                val myProfile = UiMyProfile(
                    userId = myProfile.id.long.toString(),
                    firstName = myProfile.firstName,
                    lastName = myProfile.lastName,
                    phoneNumber = myProfile.phoneNumber,
                    email = myProfile.email,
                    bio = myProfile.bio,
                    birthday = myProfile.birthday,
                )

                updateState {
                    when (this) {
                        is MyProfileUiState.Content -> copy(myProfile = myProfile)
                        is MyProfileUiState.Error,
                        is MyProfileUiState.Loading -> MyProfileUiState.View(myProfile)
                    }
                }
            }
        }

        reduce { intent ->
            when (intent) {
                is MyProfileScreenIntent.EnterEditMode -> updateState<MyProfileUiState.View, _> {
                    MyProfileUiState.Edit(
                        myProfile = myProfile,
                        firstName = myProfile.firstName,
                        lastName = myProfile.lastName,
                        bio = myProfile.bio,
                        birthday = myProfile.birthday,
                    )
                }

                is MyProfileScreenIntent.Edit -> updateState<MyProfileUiState.Edit, _> {
                    copy(
                        firstName = intent.firstName.getOrElse { firstName },
                        lastName = intent.lastName.getOrElse { lastName },
                        bio = intent.bio.getOrElse { bio },
                        birthday = intent.birthday.getOrElse { birthday },
                    )
                }

                is MyProfileScreenIntent.ExitEditMode -> updateState<MyProfileUiState.Edit, _> {
                    viewModelScope.launch {
                        client.user.editMe(
                            firstName = firstName.presentIfDiffers(myProfile.firstName),
                            lastName = lastName.presentIfDiffers(myProfile.lastName),
                            bio = bio?.takeIf { it.isNotBlank() }.presentIfDiffers(myProfile.bio),
                            birthday = birthday.presentIfDiffers(myProfile.birthday),
                        )
                    }

                    MyProfileUiState.View(
                        myProfile.copy(
                            firstName = firstName,
                            lastName = lastName,
                            bio = bio?.takeIf { it.isNotBlank() },
                            birthday = birthday,
                        )
                    )
                }

                MyProfileScreenIntent.LogOut -> {
                    client.auth.logOut()
                }
            }
        }
    }
}

private fun <T> T.presentIfDiffers(other: T): Optional<T> =
    presentIf(this != other)

private fun <T> T.presentIf(condition: Boolean): Optional<T> =
    if (condition) present(this)
    else none()
