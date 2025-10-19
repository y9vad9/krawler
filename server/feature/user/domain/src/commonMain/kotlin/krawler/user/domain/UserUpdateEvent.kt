package krawler.user.domain

public sealed interface UserUpdateEvent {
    public data class NameChanged(val userId: UserId, val newName: LinkedTelegramUserName) : UserUpdateEvent
    public data class LinkedTelegramChanged(val userId: UserId, val newLinkedTelegram: LinkedTelegram) : UserUpdateEvent
    public data class PlayerAdded(val userId: UserId, val player: BrawlStarsPlayer) : UserUpdateEvent
    public data class PlayerRefreshed(val userId: UserId, val player: BrawlStarsPlayer) : UserUpdateEvent
    public data class PlayerRemoved(val userId: UserId, val playerTag: BrawlStarsPlayerTag) : UserUpdateEvent
    public data class AllPlayersRemoved(val userId: UserId) : UserUpdateEvent
}
