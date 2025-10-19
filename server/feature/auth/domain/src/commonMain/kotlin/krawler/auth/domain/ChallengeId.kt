package krawler.auth.domain

import krawler.core.domain.ValueObject
import kotlin.uuid.Uuid

@ValueObject
@JvmInline
public value class ChallengeId(public val uuid: Uuid) {
    override fun toString(): String = uuid.toString()
}
