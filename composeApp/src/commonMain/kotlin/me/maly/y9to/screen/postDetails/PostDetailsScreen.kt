package me.maly.y9to.screen.postDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import me.maly.y9to.compose.components.post.PostCard
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_back


@Composable
fun PostDetailsScreen(
    vm: PostDetailsViewModel,
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null,
    navigatePostDetails: ((postId: String) -> Unit)? = null,
) {
    val state by vm.state.collectAsState()

    when (val state = state) {
        is PostDetailsUiState.Content -> PostDetailsScreen(
            modifier = modifier,
            state = state,
            navigateBack = navigateBack,
            navigatePostDetails = navigatePostDetails,
        )

        else -> Text(state.toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailsScreen(
    state: PostDetailsUiState.Content,
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null,
    navigatePostDetails: ((postId: String) -> Unit)? = null,
) {
    val post = state.post

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (navigateBack == null)
                        return@TopAppBar

                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    Text("Post")
                }
            )
        }
    ) { scaffoldPadding ->
        Box(Modifier.padding(scaffoldPadding)) {
            PostCard(
                post = post,
                modifier = Modifier.fillMaxWidth(),
                gotoPostDetails = navigatePostDetails,
            )
        }
    }
}
