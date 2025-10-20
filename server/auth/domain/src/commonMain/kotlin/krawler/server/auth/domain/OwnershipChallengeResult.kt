package krawler.server.auth.domain

public sealed interface OwnershipChallengeResult {
    public data object BattleBeforeTask : OwnershipChallengeResult
    public data object TaskExpired : OwnershipChallengeResult
    public data object AttemptsExceeded : OwnershipChallengeResult
    public data object InvalidBotsAmount : OwnershipChallengeResult
    public data object InvalidBrawler : OwnershipChallengeResult
    public data object InvalidEventType : OwnershipChallengeResult
    public data object Success : OwnershipChallengeResult
}
