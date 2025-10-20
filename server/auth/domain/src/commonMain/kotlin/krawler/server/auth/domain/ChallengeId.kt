package krawler.server.auth.domain

import kotlin.uuid.Uuid

@JvmInline
public value class ChallengeId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
