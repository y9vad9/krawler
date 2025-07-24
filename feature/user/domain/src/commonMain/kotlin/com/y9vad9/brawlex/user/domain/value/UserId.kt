package com.y9vad9.brawlex.user.domain.value

import com.y9vad9.valdi.domain.ValueObject
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@ValueObject
@JvmInline
public value class UserId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
