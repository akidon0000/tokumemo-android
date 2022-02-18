package com.tokudai0000.tokumemo.AKLibrary

// SwiftのguardをKotlinに移植(オプショナルのアンラップに使用)
inline fun <T> guard(value: T?, ifNull: () -> Unit): T {
    if (value != null) return value
    ifNull()
    throw Exception("Guarded from null!")
}