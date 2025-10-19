package krawler.auth.domain

public sealed interface OwnershipChallengeUpdateEvent {
    public data class VerificationFailed(public val challengeId: ChallengeId) : OwnershipChallengeUpdateEvent
    public data class VerificationCompleted(public val challengeId: ChallengeId) : OwnershipChallengeUpdateEvent
}
