package me.maly.y9to.types


sealed interface UiUploadCoverState {
    data object None : UiUploadCoverState
    data object Uploading : UiUploadCoverState
    data class Error(val error: UiUploadCoverError) : UiUploadCoverState
}

sealed interface UiUploadAvatarState {
    data object None : UiUploadAvatarState
    data object Uploading : UiUploadAvatarState
    data class Error(val error: UiUploadAvatarError) : UiUploadAvatarState
}

sealed interface UiUploadCoverError {
    data object TooBigFile : UiUploadCoverError
    data object StorageQuotaExceeded : UiUploadCoverError
    data object ConnectionError : UiUploadCoverError
    data class UnknownError(val message: String) : UiUploadCoverError
}

sealed interface UiUploadAvatarError {
    data object TooBigFile : UiUploadAvatarError
    data object StorageQuotaExceeded : UiUploadAvatarError
    data object ConnectionError : UiUploadAvatarError
    data class UnknownError(val message: String) : UiUploadAvatarError
}
