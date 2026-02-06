package me.maly.y9to.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlinx.io.Source
import me.maly.y9to.types.EditProfileProperties
import me.maly.y9to.types.UploadAvatarError
import me.maly.y9to.types.UploadAvatarState
import me.maly.y9to.types.UploadCoverError
import me.maly.y9to.types.UploadCoverError.*
import me.maly.y9to.types.UploadCoverState
import y9to.api.types.EditMeError
import y9to.api.types.FileId
import y9to.api.types.LogOutError
import y9to.api.types.UploadFileError
import y9to.common.types.Birthday
import y9to.libs.stdlib.Union
import y9to.libs.stdlib.asError
import y9to.libs.stdlib.asOk
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.present
import y9to.libs.stdlib.successOrElse
import y9to.sdk.Client
import y9to.sdk.upload
import kotlin.jvm.JvmName
import kotlin.properties.Delegates
import kotlin.time.Clock


class MyProfileRepositoryDefault(
    private val scope: CoroutineScope,
    private val client: Client,
    private val timeZone: TimeZone,
    private val clock: Clock,
) : MyProfileRepository {
    override val myProfile = client.user.myProfile
    override val canLogOut = flowOf(true)
    override val editProperties = flowOf(EditProfileProperties(
        maxCoverFileSize = null,
        maxAvatarFileSize = null,
        firstNameLength = 1..64,
        lastNameLength = 1..64,
        bioLength = 1..1024,
        birthdayYearRange = 1800..clock.now().toLocalDateTime(timeZone).year,
        firstNameCanBeNull = false,
        lastNameCanBeNull = true,
        bioCanBeNull = true,
    ))

    private val uploadCoverJob = MutableStateFlow<UploadCoverJob?>(null)
    private val uploadAvatarJob = MutableStateFlow<UploadAvatarJob?>(null)
    override val uploadCoverState = uploadCoverJob.mapToState()
    override val uploadAvatarState = uploadAvatarJob.mapToState()

    override suspend fun edit(
        firstName: Optional<String>,
        lastName: Optional<String?>,
        bio: Optional<String?>,
        birthday: Optional<Birthday?>,
        cover: Optional<FileId?>,
        avatar: Optional<FileId?>
    ): Union<Unit, EditMeError> {
        return client.user.editMe(
            firstName = firstName,
            lastName = lastName,
            bio = bio,
            birthday = birthday,
            cover = cover,
            avatar = avatar,
        )
    }

    override fun cancelUploadCover() {
        uploadCoverJob.update { uploadCoverJob ->
            uploadCoverJob?.cancel()
            null
        }
    }

    override fun cancelUploadAvatar() {
        uploadAvatarJob.update { uploadAvatarJob ->
            uploadAvatarJob?.cancel()
            null
        }
    }

    override suspend fun uploadCover(
        filename: String,
        filesize: Long?,
        source: Source,
    ): Union<Unit, UploadCoverError> {
        val job = scope.uploadCoverAsync(start = CoroutineStart.LAZY) {
            val minimalExecutionTime = launch { delay(800) }

            val file = try {
                client.file.upload(
                    name = filename,
                    expectedSize = filesize,
                    source = source,
                ).successOrElse { error ->
                    when (error) {
                        is UploadFileError.StorageQuotaExceeded ->
                            return@uploadCoverAsync UploadCoverError.StorageQuotaExceeded.asError()
                    }
                }
            } catch (e: IOException) {
                return@uploadCoverAsync UploadCoverError.ConnectionError(e).asError()
            }

            minimalExecutionTime.join() // just for test
            client.user.editMe(cover = present(file.id))
                .successOrElse { error ->
                    return@uploadCoverAsync when (error) {
                        is EditMeError.Unauthenticated ->
                            UnknownError(null, "Unauthenticated").asError()
                        is EditMeError.FieldErrors ->
                            UnknownError(null, error.toString()).asError()
                        is EditMeError.NothingToChange ->
                            Unit.asOk()
                    }
                }

            Unit.asOk()
        }

        uploadCoverJob.value?.cancel()
        uploadCoverJob.value = job
        job.start()

        return job.await()
            .onSuccess { uploadCoverJob.value = null }
    }

    override suspend fun uploadAvatar(
        filename: String,
        filesize: Long?,
        source: Source
    ): Union<Unit, UploadAvatarError> {
        val job = scope.uploadAvatarAsync(start = CoroutineStart.LAZY) {
            val minimalExecutionTime = launch { delay(800) }

            val file = try {
                client.file.upload(
                    name = filename,
                    expectedSize = filesize,
                    source = source,
                ).successOrElse { error ->
                    when (error) {
                        is UploadFileError.StorageQuotaExceeded ->
                            return@uploadAvatarAsync UploadAvatarError.StorageQuotaExceeded.asError()
                    }
                }
            } catch (e: IOException) {
                return@uploadAvatarAsync UploadAvatarError.ConnectionError(e).asError()
            }

            minimalExecutionTime.join() // just for test
            client.user.editMe(avatar = present(file.id))
                .successOrElse { error ->
                    return@uploadAvatarAsync when (error) {
                        is EditMeError.Unauthenticated ->
                            UploadAvatarError.UnknownError(null, "Unauthenticated").asError()
                        is EditMeError.FieldErrors ->
                            UploadAvatarError.UnknownError(null, error.toString()).asError()
                        is EditMeError.NothingToChange ->
                            Unit.asOk()
                    }
                }

            Unit.asOk()
        }

        uploadAvatarJob.value?.cancel()
        uploadAvatarJob.value = job
        job.start()

        return job.await()
            .onSuccess { uploadAvatarJob.value = null }
    }

    override suspend fun logOut(): Boolean {
        if (!canLogOut.first())
            return false
        client.auth.logOut().successOrElse { error ->
            when (error) {
                is LogOutError.AlreadyUnauthorized ->
                    return true
            }
        }
        return true
    }
}

//

private fun CoroutineScope.uploadCoverAsync(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend UploadCoverJobScope.() -> Union<Unit, UploadCoverError>
): UploadCoverJob {
    var job by Delegates.notNull<UploadCoverJob>()
    job = UploadCoverJob(async(start = start) {
        block(UploadCoverJobScope(
            scope = this,
            setProgress = { job.setProgress(it) }
        ))
    })
    return job
}

private fun CoroutineScope.uploadAvatarAsync(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend UploadAvatarJobScope.() -> Union<Unit, UploadAvatarError>
): UploadAvatarJob {
    var job by Delegates.notNull<UploadAvatarJob>()
    job = UploadAvatarJob(async(start = start) {
        block(UploadAvatarJobScope(
            scope = this,
            setProgress = {
                job.setProgress(it)
            }
        ))
    })
    return job
}

private class UploadCoverJobScope(
    scope: CoroutineScope,
    setProgress: (Float) -> Unit,
) : CoroutineScope by scope

private class UploadAvatarJobScope(
    scope: CoroutineScope,
    setProgress: (Float) -> Unit,
) : CoroutineScope by scope

private class UploadCoverJob(private val deferred: Deferred<Union<Unit, UploadCoverError>>) {
    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    suspend fun await(): Union<Unit, UploadCoverError> {
        return deferred.await()
    }

    fun setProgress(value: Float) {
        require(value in 0f..1f)
        _progress.value = value
    }

    fun start() = deferred.start()
    fun cancel() = deferred.cancel()
}

private class UploadAvatarJob(private val deferred: Deferred<Union<Unit, UploadAvatarError>>) {
    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    suspend fun await(): Union<Unit, UploadAvatarError> {
        return deferred.await()
    }

    fun setProgress(value: Float) {
        require(value in 0f..1f)
        _progress.value = value
    }

    fun start() = deferred.start()
    fun cancel() = deferred.cancel()
}

@JvmName("mapToUploadCoverState")
private fun Flow<UploadCoverJob?>.mapToState() = channelFlow<UploadCoverState> {
    collectLatest { job ->
        if (job == null) {
            send(UploadCoverState.None)
            return@collectLatest
        }

        val sendProgressJob = launch {
            job.progress.collect { progress ->
                send(UploadCoverState.Uploading(progress))
            }
        }

        val result = job.await()
        sendProgressJob.cancel()
        result.onError { error ->
            send(UploadCoverState.Error(error, canRetry = true))
        }
    }
}

@JvmName("mapToUploadAvatarState")
private fun Flow<UploadAvatarJob?>.mapToState() = channelFlow<UploadAvatarState> {
    collectLatest { job ->
        if (job == null) {
            send(UploadAvatarState.None)
            return@collectLatest
        }

        val sendProgressJob = launch {
            job.progress.collect { progress ->
                send(UploadAvatarState.Uploading(progress))
            }
        }

        val result = job.await()
        sendProgressJob.cancel()
        result.onError { error ->
            send(UploadAvatarState.Error(error, canRetry = true))
        }
    }
}
