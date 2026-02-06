@file:Suppress("RedundantNullableReturnType")

package me.maly.y9to.compose

import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.key.Keyer
import coil3.request.Options
import okio.Buffer
import y9to.api.types.FileId
import y9to.libs.io.internals.DelicateIoApi
import y9to.libs.io.segment.snapshot
import y9to.sdk.Client


interface /* Ui- */ ImageRequest

data class FileImageRequest(val id: FileId) : ImageRequest


fun ComponentRegistry.Builder.fileImageRequests(client: Client) {
    add(FileImageRequestFetcher.Factory(client))
    add(FileImageRequestKeyer())
}


// --- это временно ---


private class FileImageRequestFetcher(
    private val client: Client,
    private val data: FileImageRequest,
    private val options: Options,
    private val imageLoader: ImageLoader,
) : Fetcher {
    @OptIn(DelicateIoApi::class)
    override suspend fun fetch(): FetchResult? {
        val okioSource = Buffer().apply {
            val success = client.file.download(data.id) {
                while (true) {
                    val segment = read() ?: break
                    write(segment.snapshot().byteArray)
                }
            }

            if (!success)
                return null
        }

        val imageSource = ImageSource(
            source = okioSource,
            fileSystem = options.fileSystem,
        )

        return SourceFetchResult(
            source = imageSource,
            mimeType = null,
            dataSource = DataSource.NETWORK,
        )
    }

    class Factory(
        private val client: Client,
    ) : Fetcher.Factory<FileImageRequest> {
        override fun create(
            data: FileImageRequest,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher? {
            return FileImageRequestFetcher(client, data, options, imageLoader)
        }
    }
}

private class FileImageRequestKeyer : Keyer<FileImageRequest> {
    override fun key(data: FileImageRequest, options: Options): String? {
        return data.id.long.toString()
    }
}
