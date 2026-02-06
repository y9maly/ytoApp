package me.maly.y9to.compose

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings.Companion
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType


interface PickerResultLauncher {
    fun launch()
}

@Composable
expect fun <PickerResult, ConsumedResult> rememberFilePickerLauncher(
    type: FileKitType = FileKitType.File(),
    mode: FileKitMode<PickerResult, ConsumedResult>,
    title: String? = null,
    directory: PlatformFile? = null,
    dialogSettings: FileKitDialogSettings = FileKitDialogSettings.createDefault(),
    onResult: (ConsumedResult) -> Unit,
): PickerResultLauncher
