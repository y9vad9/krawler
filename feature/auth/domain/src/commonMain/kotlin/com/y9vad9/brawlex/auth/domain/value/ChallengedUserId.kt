package com.y9vad9.brawlex.auth.domain.value

import com.y9vad9.valdi.domain.ValueObject
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@ValueObject
@JvmInline
public value class ChallengedUserId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
