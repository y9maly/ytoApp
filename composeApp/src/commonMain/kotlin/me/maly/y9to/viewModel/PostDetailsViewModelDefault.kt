package me.maly.y9to.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.maly.y9to.repository.ViewPostRepository
import y9to.api.types.InputPost
import y9to.api.types.PostId
import y9to.libs.stdlib.coroutines.flow.collectIn


class PostDetailsViewModelDefault(
    private val viewPostRepository: ViewPostRepository,
    uiPostId: String,
) : ViewModel(), PostDetailsViewModel {
    private val _state = MutableStateFlow<PostDetailsUiState>(PostDetailsUiState.Loading)
    override val state = _state.asStateFlow()

    init {
        val postId = uiPostId.toLongOrNull()?.let(::PostId)

        if (postId != null) {
            viewPostRepository.getFlow(InputPost.Id(postId)).collectIn(viewModelScope) { post ->
                if (post == null) {
                    _state.value = PostDetailsUiState.PostWasDeleted
                    return@collectIn
                }

                _state.value = PostDetailsUiState.Content(post.map())
            }
        } else {
            _state.value = PostDetailsUiState.Error("Invalid post id '${uiPostId}'")
        }
    }
}
