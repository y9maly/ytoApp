package me.maly.y9to.screen.myProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


class MyProfileState {
    var editing by mutableStateOf(false)
}

@Composable
fun rememberMyProfileScreenState() = remember { MyProfileState() }
