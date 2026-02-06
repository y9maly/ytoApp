package me.maly.y9to.types

import me.maly.y9to.compose.ImageRequest
import y9to.common.types.Birthday


data class UiMyProfile(
    val userId: String,
    val firstName: String,
    val lastName: String?,
    val cover: ImageRequest?,
    val avatar: ImageRequest?,
    val phoneNumber: String?,
    val email: String?,
    val bio: String?,
    val birthday: Birthday?,
) {
    val displayName get() =
        lastName?.let { "$firstName $lastName" } ?: firstName
}
