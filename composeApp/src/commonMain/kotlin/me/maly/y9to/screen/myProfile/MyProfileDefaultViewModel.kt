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
    override val store = store<MyProfileScreenState, MyProfileScreenIntent, Nothing>(
        MyProfileScreenState.Loading,
        viewModelScope
    ) {
        whileSubscribed {
            client.user.myProfile.collect { myProfile ->
                if (myProfile == null) {
                    updateState {
                        MyProfileScreenState.Error("Unauthenticated")
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
                        is MyProfileScreenState.Content -> copy(myProfile = myProfile)
                        is MyProfileScreenState.Error,
                        is MyProfileScreenState.Loading -> MyProfileScreenState.View(myProfile)
                    }
                }
            }
        }

        reduce { intent ->
            when (intent) {
                is MyProfileScreenIntent.EnterEditMode -> updateState<MyProfileScreenState.View, _> {
                    MyProfileScreenState.Edit(
                        myProfile = myProfile,
                        firstName = myProfile.firstName,
                        lastName = myProfile.lastName,
                        bio = myProfile.bio,
                        birthday = myProfile.birthday,
                    )
                }

                is MyProfileScreenIntent.Edit -> updateState<MyProfileScreenState.Edit, _> {
                    copy(
                        firstName = intent.firstName.getOrElse { firstName },
                        lastName = intent.lastName.getOrElse { lastName },
                        bio = intent.bio.getOrElse { bio },
                        birthday = intent.birthday.getOrElse { birthday },
                    )
                }

                is MyProfileScreenIntent.ExitEditMode -> updateState<MyProfileScreenState.Edit, _> {
                    viewModelScope.launch {
                        client.user.editMe(
                            firstName = firstName.presentIfDiffers(myProfile.firstName),
                            lastName = lastName.presentIfDiffers(myProfile.lastName),
                            bio = bio?.takeIf { it.isNotBlank() }.presentIfDiffers(myProfile.bio),
                            birthday = birthday.presentIfDiffers(myProfile.birthday),
                        )
                    }

                    MyProfileScreenState.View(
                        myProfile.copy(
                            firstName = firstName,
                            lastName = lastName,
                            bio = bio?.takeIf { it.isNotBlank() },
                            birthday = birthday,
                        )
                    )
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
