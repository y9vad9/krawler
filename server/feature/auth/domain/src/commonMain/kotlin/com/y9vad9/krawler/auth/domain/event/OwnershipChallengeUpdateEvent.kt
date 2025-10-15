package com.y9vad9.krawler.auth.domain.event

import com.y9vad9.krawler.auth.domain.value.ChallengeId

public sealed interface OwnershipChallengeUpdateEvent {
    public data class VerificationFailed(public val challengeId: ChallengeId) : OwnershipChallengeUpdateEvent
    public data class VerificationCompleted(public val challengeId: ChallengeId) : OwnershipChallengeUpdateEvent
}
