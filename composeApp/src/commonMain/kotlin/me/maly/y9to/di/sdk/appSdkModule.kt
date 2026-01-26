package me.maly.y9to.di.sdk

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.core.module.Module
import org.koin.dsl.module
import y9to.sdk.Client
import y9to.sdk.createSdkClient


suspend fun appSdkModule(): Module {
    val client = createSdkClient("localhost", 8103, "/")

    return module {
        single { client }
    }
}
