package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import me.maly.y9to.types.PostDraft


interface PostDraftRepository {
    val draft: Flow<PostDraft?>

    suspend fun deleteDraft()
    suspend fun updateDraft(draft: PostDraft)
}
