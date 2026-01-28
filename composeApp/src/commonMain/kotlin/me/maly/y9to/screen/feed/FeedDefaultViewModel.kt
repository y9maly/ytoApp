package me.maly.y9to.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.cachedIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview
import y9to.api.types.AuthState
import y9to.api.types.Post
import y9to.api.types.PostAuthorPreview
import y9to.api.types.PostContent
import y9to.api.types.RepostPreview
import y9to.libs.stdlib.PagingKey
import y9to.libs.stdlib.SpliceKey
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.sdk.Client
import y9to.sdk.types.FeedPagingOptions


class FeedDefaultViewModel(client: Client) : ViewModel(), FeedViewModel {
    override val header = FeedHeaderDefaultComponent(viewModelScope, client)

    override val pagerFlow = Pager(PagingConfig(pageSize = 5)) {
        FeedSource(client)
    }.flow.cachedIn(viewModelScope)
}

class FeedHeaderDefaultComponent(
    private val scope: CoroutineScope,
    private val client: Client,
) : FeedHeaderComponent {
    override val state = MutableStateFlow<FeedHeaderState>(FeedHeaderState.Loading)

    init {
        combine(client.auth.authState, client.user.myProfile) { authState, myProfile ->
            if (authState is AuthState.Unauthorized) {
                state.value = FeedHeaderState.Unauthenticated
            } else if (myProfile == null) {
                state.value = FeedHeaderState.Loading
            } else {
                state.value = FeedHeaderState.Authenticated(
                    firstName = myProfile.firstName,
                    lastName = myProfile.lastName,
                )
            }
        }.collectIn(scope)
    }
}

private class FeedSource(private val client: Client) : PagingSource<PagingKey, UiPost>() {
    override suspend fun load(params: LoadParams<PagingKey>): LoadResult<PagingKey, UiPost> {
        return try {
            val pagingKey = params.key
            val key =
                if (pagingKey != null) SpliceKey.Continue(pagingKey)
                else SpliceKey.Initialize(FeedPagingOptions.GlobalFeed())
            delay(250)
            val result = client.feed.splice(key, params.loadSize)
            delay(250)

            LoadResult.Page(
                data = result.list.map { it.map() },
                prevKey = pagingKey,
                nextKey = result.nextPagingKey,
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<PagingKey, UiPost>): PagingKey? {
        TODO()
    }

    private fun Post.map(): UiPost {
        return UiPost(
            id = id.long.toString(),
            author = UiPostAuthorPreview.User(
                id = author.id.long.toString(),
                firstName = author.firstName,
                lastName = author.lastName,
            ),
            publishDate = publishDate,
            lastEditDate = lastEditDate,
            content = content.map(),
        )
    }

    private fun PostContent.map(): UiPostContent = when (this) {
        is PostContent.Standalone -> UiPostContent.Standalone(text)
        is PostContent.Repost -> UiPostContent.Repost(
            comment = comment,
            originalPreview = when (val preview = preview) {
                is RepostPreview.Post -> UiRepostPreview.Post(
                    id = preview.postId.long.toString(),
                    author = when (val author = preview.author) {
                        is PostAuthorPreview.User -> UiPostAuthorPreview.User(
                            id = author.id.toString(),
                            firstName = author.firstName,
                            lastName = author.lastName,
                        )

                        is PostAuthorPreview.DeletedUser -> UiPostAuthorPreview.DeletedUser(
                            firstName = author.firstName,
                            lastName = author.lastName,
                        )
                    },
                    publishDate = preview.publishDate,
                    lastEditDate = preview.lastEditDate,
                    content = preview.content.map(),
                )

                is RepostPreview.DeletedPost -> UiRepostPreview.DeletedPost(
                    deletionDate = preview.deletionDate,
                    author = when (val author = preview.author) {
                        is PostAuthorPreview.User -> UiPostAuthorPreview.User(
                            id = author.id.toString(),
                            firstName = author.firstName,
                            lastName = author.lastName,
                        )

                        is PostAuthorPreview.DeletedUser -> UiPostAuthorPreview.DeletedUser(
                            firstName = author.firstName,
                            lastName = author.lastName,
                        )
                    },
                )
            }
        )
    }
}
