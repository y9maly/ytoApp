package me.maly.y9to

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform