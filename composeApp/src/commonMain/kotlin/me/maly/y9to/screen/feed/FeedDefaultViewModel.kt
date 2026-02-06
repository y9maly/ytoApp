package me.maly.y9to.screen.feed

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.maly.y9to.compose.FileImageRequest
import me.maly.y9to.repository.AuthInfoRepository
import me.maly.y9to.repository.CreatePostRepository
import me.maly.y9to.repository.FeedRepository
import me.maly.y9to.repository.MyProfileRepository
import me.maly.y9to.repository.isAuthenticated
import me.maly.y9to.types.UiMyProfile
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.viewModel.map
import y9to.api.types.InputPostContent
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.libs.stdlib.successOrElse
import kotlin.time.Clock


class FeedDefaultViewModel(
    private val authInfoRepository: AuthInfoRepository,
    private val myProfileRepository: MyProfileRepository,
    private val feedRepository: FeedRepository,
    private val createPostRepository: CreatePostRepository,
) : ViewModel(), FeedViewModel {
    override val profile = myProfileRepository.myProfile.map { myProfile ->
        if (myProfile == null)
            return@map null

        UiMyProfile(
            userId = myProfile.id.long.toString(),
            firstName = myProfile.firstName,
            lastName = myProfile.lastName,
            cover = myProfile.cover?.let { FileImageRequest(it) },
            avatar = myProfile.avatar?.let { FileImageRequest(it) },
            phoneNumber = myProfile.phoneNumber,
            email = myProfile.email,
            bio = myProfile.bio,
            birthday = myProfile.birthday,
        )
    }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    override val header = FeedHeaderDefaultComponent(viewModelScope, authInfoRepository, myProfileRepository)

    override val pagerFlow = feedRepository.createPager()
        .flow
        .map { pagingData ->
            pagingData.map { post ->
                post.map()
            }
        }
        .cachedIn(viewModelScope)

    override val prependPosts = mutableStateListOf<UiPost>()

    override val prePublishPreviews = mutableStateListOf<UiPostPrePublishPreview>()

    override fun publish(content: UiInputPostContent): Unit = viewModelScope.launch {
        val minimalExecutionTime = launch { delay(400) }

        var prePublishPreview: UiPostPrePublishPreview = UiPostPrePublishPreview.Pending(
            author = null,
            publishDate = Clock.System.now(),
            content = when (content) {
                is UiInputPostContent.Standalone -> UiPostContent.Standalone(content.text)
            }
        ).also { prePublishPreviews.add(it) }

        val addAuthorPreview = launch {
            val me = myProfileRepository.myProfile.first()
                ?: return@launch

            prePublishPreview = prePublishPreviews.replace(prePublishPreview, prePublishPreview.copy(
                UiPostAuthorPreview.User(
                    id = me.id.long.toString(),
                    firstName = me.firstName,
                    lastName = me.lastName,
                )
            )) ?: return@launch
        }

        val post = createPostRepository.create(
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

        minimalExecutionTime.join()
        prePublishPreviews.remove(prePublishPreview)
        prependPosts.add(0, post.map())
    }.run {}

    override fun canEdit(post: UiPost): Flow<Boolean> = isAuthor(post)

    override fun canDelete(post: UiPost): Flow<Boolean> = isAuthor(post)

    private fun isAuthor(post: UiPost) = channelFlow {
        myProfileRepository.myProfile.collect { myProfile ->
            if (myProfile == null) {
                send(false)
            } else {
                send(post.author.idOrNull == myProfile.id.long.toString())
            }
        }
    }

    override fun canReply(post: UiPost): Flow<Boolean> {
        return authInfoRepository.isAuthenticated
    }

    override fun canRepost(post: UiPost): Flow<Boolean> {
        return authInfoRepository.isAuthenticated
    }

    private fun <T : Any> MutableList<T>.replace(old: T, new: T): T? {
        val index = indexOf(old)
        if (index == -1) return null
        set(index, new)
        return new
    }
}

class FeedHeaderDefaultComponent(
    private val scope: CoroutineScope,
    private val authInfoRepository: AuthInfoRepository,
    private val myProfileRepository: MyProfileRepository,
) : FeedHeaderComponent {
    override val state = MutableStateFlow<FeedHeaderUiState>(FeedHeaderUiState.Loading)

    init {
        myProfileRepository.myProfile.collectIn(scope) { myProfile ->
            if (myProfile == null) {
                state.value = FeedHeaderUiState.Unauthenticated
            } else {
                state.value = FeedHeaderUiState.Authenticated(
                    firstName = myProfile.firstName,
                    lastName = myProfile.lastName,
                )
            }
        }
    }
}
