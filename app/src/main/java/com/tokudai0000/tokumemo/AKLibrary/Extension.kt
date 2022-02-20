package com.tokudai0000.tokumemo.AKLibrary

// SwiftのguardをKotlinに移植(オプショナルのアンラップに使用)
// **使い方**
// val args = guard(arguments) {
//    throw IllegalStateException("argumentsがnullです")
// }
// val hoge = args.getSerializable("HOGE") as Hoge
// *********
inline fun <T> guard(value: T?, ifNull: () -> Unit): T {
    if (value != null) return value
    ifNull()
    throw Exception("Guarded from null!")
}