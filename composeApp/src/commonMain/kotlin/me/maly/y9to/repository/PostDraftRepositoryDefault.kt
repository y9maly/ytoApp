package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import me.maly.y9to.types.PostDraft


class PostDraftRepositoryDefault : PostDraftRepository {
    override val draft: Flow<PostDraft?>
        get() = TODO("Not yet implemented")

    override suspend fun deleteDraft() {
        TODO("Not yet implemented")
    }

    override suspend fun updateDraft(draft: PostDraft) {
        TODO("Not yet implemented")
    }
}
