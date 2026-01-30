package me.maly.y9to.screen.feed

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.maly.y9to.types.UiMyProfile
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview
import y9to.api.types.AuthState
import y9to.api.types.InputPostContent
import y9to.api.types.MyProfile
import y9to.api.types.Post
import y9to.api.types.PostAuthorPreview
import y9to.api.types.PostContent
import y9to.api.types.RepostPreview
import y9to.libs.stdlib.PagingKey
import y9to.libs.stdlib.SpliceKey
import y9to.libs.stdlib.Union
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.libs.stdlib.successOrElse
import y9to.sdk.Client
import y9to.sdk.types.FeedPagingOptions
import kotlin.time.Clock


class FeedDefaultViewModel(private val client: Client) : ViewModel(), FeedViewModel {
    override val profile = client.user.myProfile.map {
        it ?: return@map null

        UiMyProfile(
            userId = it.id.long.toString(),
            firstName = it.firstName,
            lastName = it.lastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            bio = it.bio,
            birthday = it.birthday,
        )
    }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    override val header = FeedHeaderDefaultComponent(viewModelScope, client)

    override val pagerFlow = Pager(PagingConfig(pageSize = 2)) {
        FeedSource(client)
    }.flow.cachedIn(viewModelScope)

    override val newPosts = mutableStateListOf<UiPost>()

    override val prePublishPreviews = mutableStateListOf<UiPostPrePublishPreview>()

    override fun publish(content: UiInputPostContent): Unit = viewModelScope.launch {
        var prePublishPreview: UiPostPrePublishPreview = UiPostPrePublishPreview.Pending(
            author = null,
            publishDate = Clock.System.now(),
            content = when (content) {
                is UiInputPostContent.Standalone -> UiPostContent.Standalone(content.text)
            }
        ).also { prePublishPreviews.add(it) }

        val addAuthorPreview = launch {
            val me = client.user.myProfile.first()
                ?: return@launch
            prePublishPreview = prePublishPreviews.replace(prePublishPreview, prePublishPreview.copy(
                UiPostAuthorPreview.User(
                    id = me.id.long.toString(),
                    firstName = me.firstName,
                    lastName = me.lastName,
                )
            )) ?: return@launch
        }

        val post = client.post.create(
            replyTo = null,
            content = when (content) {
                is UiInputPostContent.Standalone -> InputPostContent.Standalone(content.text)
            }
        ).successOrElse { error ->
            prePublishPreview = prePublishPreviews.replace(prePublishPreview, prePublishPreview.copyAsError(
                errorMessage = error.toString()
            )) ?: return@launch
            return@launch
        }

        addAuthorPreview.cancel()
        prePublishPreviews.remove(prePublishPreview)

        newPosts.add(0, post.map())
    }.run {}

    private fun <T : Any> MutableList<T>.replace(old: T, new: T): T? {
        val index = indexOf(old)
        if (index == -1) return null
        set(index, new)
        return new
    }
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
