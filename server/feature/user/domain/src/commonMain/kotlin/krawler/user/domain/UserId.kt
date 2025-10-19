package krawler.user.domain

import krawler.core.domain.ValueObject
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@ValueObject
@JvmInline
public value class UserId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
