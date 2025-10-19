package krawler.auth.domain

import kotlin.uuid.Uuid

@JvmInline
public value class ChallengedUserId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
