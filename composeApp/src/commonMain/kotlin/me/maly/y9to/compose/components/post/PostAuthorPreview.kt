package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import me.maly.y9to.types.UiPostAuthorPreview


@Composable
fun PostAuthorPreview(
    preview: UiPostAuthorPreview,
    modifier: Modifier = Modifier,
) {
    when (preview) {
        is UiPostAuthorPreview.User -> PostAuthorPreview(preview, modifier)
        is UiPostAuthorPreview.DeletedUser -> PostAuthorPreview(preview, modifier)
    }
}

@Composable
fun PostAuthorPreview(
    preview: UiPostAuthorPreview.User,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(preview.displayName, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PostAuthorPreview(
    preview: UiPostAuthorPreview.DeletedUser,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text("Deleted user ${preview.displayName}", fontFamily = FontFamily.Cursive)
    }
}
