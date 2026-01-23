package me.maly.y9to.types


sealed interface UiLoginState {
    data object Loading : UiLoginState
    data object WaitPhoneNumberOrEmail : UiLoginState
    data object WaitConfirmCode : UiLoginState
    data object Authenticated : UiLoginState
}
