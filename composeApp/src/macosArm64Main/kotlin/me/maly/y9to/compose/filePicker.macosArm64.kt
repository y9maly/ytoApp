package me.maly.y9to.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch


@Composable
actual fun <PickerResult, ConsumedResult> rememberFilePickerLauncher(
    type: FileKitType,
    mode: FileKitMode<PickerResult, ConsumedResult>,
    title: String?,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings,
    onResult: (ConsumedResult) -> Unit
): PickerResultLauncher {
    val scope = rememberCoroutineScope()

    val currentType by rememberUpdatedState(type)
    val currentMode by rememberUpdatedState(mode)
    val currentTitle by rememberUpdatedState(title)
    val currentDirectory by rememberUpdatedState(directory)
    val currentDialogSettings by rememberUpdatedState(dialogSettings)
    val currentOnConsumed by rememberUpdatedState(onResult)

    return remember(scope) {
        object : PickerResultLauncher {
            override fun launch() {
                scope.launch {
                    val result = FileKit.openFilePicker(
                        type = currentType,
                        mode = currentMode,
                        title = currentTitle,
                        directory = currentDirectory,
                        dialogSettings = currentDialogSettings,
                    )

                    mode.consumeResult(result, currentOnConsumed)
                }
            }
        }
    }
}
