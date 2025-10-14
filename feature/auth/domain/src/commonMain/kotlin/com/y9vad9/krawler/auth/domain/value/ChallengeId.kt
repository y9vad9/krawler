package com.y9vad9.krawler.auth.domain.value

import kotlin.uuid.Uuid

@JvmInline
public value class ChallengeId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
