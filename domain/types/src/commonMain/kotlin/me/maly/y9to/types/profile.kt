package me.maly.y9to.types


sealed interface UploadCoverState {
    data object None : UploadCoverState
    data class Uploading(val progress: Float) : UploadCoverState
    data class Error(val error: UploadCoverError, val canRetry: Boolean) : UploadCoverState
}

sealed interface UploadAvatarState {
    data object None : UploadAvatarState
    data class Uploading(val progress: Float) : UploadAvatarState
    data class Error(val error: UploadAvatarError, val canRetry: Boolean) : UploadAvatarState
}

data class EditProfileProperties(
    val maxCoverFileSize: Long?,     // in bytes; null - unknown, unlimited
    val maxAvatarFileSize: Long?,    // in bytes; null - unknown, unlimited
    val firstNameLength: IntRange,   // 1..64
    val lastNameLength: IntRange,    // 1..64
    val bioLength: IntRange,         // 1..512
    val birthdayYearRange: IntRange, // 1900..<currentYear>
    val firstNameCanBeNull: Boolean, // false
    val lastNameCanBeNull: Boolean,  // true
    val bioCanBeNull: Boolean,       // true
)

sealed interface UploadCoverError {
    data object TooBigFile : UploadCoverError
    data object StorageQuotaExceeded : UploadCoverError
    data class ConnectionError(val cause: Throwable?) : UploadCoverError
    data class UnknownError(val cause: Throwable?, val message: String) : UploadCoverError
}

sealed interface UploadAvatarError {
    data object TooBigFile : UploadAvatarError
    data object StorageQuotaExceeded : UploadAvatarError
    data class ConnectionError(val cause: Throwable?) : UploadAvatarError
    data class UnknownError(val cause: Throwable?, val message: String) : UploadAvatarError
}
