package com.y9vad9.starix.core.system.entity

import com.y9vad9.starix.core.system.entity.value.LanguageCode
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class LocalizableEntity<T>(private val map: Map<LanguageCode, T>) {
    constructor(vararg pairs: Pair<LanguageCode, T>) : this(map = mapOf(*pairs))

    operator fun get(languageCode: LanguageCode) = map[languageCode]

    fun asMap(): Map<LanguageCode, T> = map.toMap()
}

fun <T> LocalizableEntity<T>.getOrReturnAnyFirst(languageCode: LanguageCode): T? {
    return get(languageCode) ?: asMap().values.firstOrNull()
}

val <T> LocalizableEntity<T>.ru: T? get() = get(LanguageCode.RUSSIAN)
val <T> LocalizableEntity<T>.en: T? get() = get(LanguageCode.ENGLISH)
val <T> LocalizableEntity<T>.uk: T? get() = get(LanguageCode.UKRAINIAN)