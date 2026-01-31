package me.maly.y9to.screen.postDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.maly.y9to.viewModel.map
import y9to.api.types.InputPost
import y9to.api.types.PostId
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.sdk.Client


class PostDetailsDefaultViewModel(
    private val client: Client,
    private val postId: PostId,
) : ViewModel(), PostDetailsViewModel {
    private val _state = MutableStateFlow<PostDetailsUiState>(PostDetailsUiState.Loading)
    override val state = _state.asStateFlow()

    init {
        client.post.getFlow(InputPost.Id(postId)).collectIn(viewModelScope) { post ->
            if (post == null) {
                _state.value = PostDetailsUiState.PostWasDeleted
                return@collectIn
            }

            _state.value = PostDetailsUiState.Content(post.map())
        }
    }
}
