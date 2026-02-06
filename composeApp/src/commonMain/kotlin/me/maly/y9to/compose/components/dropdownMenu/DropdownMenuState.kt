package me.maly.y9to.compose.components.dropdownMenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round


@Composable
fun <T : Any> rememberDropdownMenuState() = remember {
    DropdownMenuState<T>()
}

class DropdownMenuState<T : Any>() {
    var value by mutableStateOf<T?>(null)
    var position by mutableStateOf(IntOffset.Zero)

    fun hide() {
        this.value = null
    }

    fun show(value: T, position: Offset) = show(value, position.round())

    fun show(value: T, position: IntOffset) {
        this.value = value
        this.position = position
    }
}
