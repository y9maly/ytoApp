package me.maly.y9to.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher as underlyingRememberFilePickerLauncher


@Composable
actual fun <PickerResult, ConsumedResult> rememberFilePickerLauncher(
    type: FileKitType,
    mode: FileKitMode<PickerResult, ConsumedResult>,
    title: String?,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings,
    onResult: (ConsumedResult) -> Unit
): PickerResultLauncher {
    val underlyingLauncher = underlyingRememberFilePickerLauncher(type, mode, title, directory, dialogSettings, onResult)

    return remember(underlyingLauncher) {
        object : PickerResultLauncher {
            override fun launch() {
                underlyingLauncher.launch()
            }
        }
    }
}
