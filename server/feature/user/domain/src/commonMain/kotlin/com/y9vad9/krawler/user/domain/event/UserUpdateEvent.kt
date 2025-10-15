package com.y9vad9.krawler.user.domain.event

import com.y9vad9.krawler.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.krawler.user.domain.entity.LinkedTelegram
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.krawler.user.domain.value.LinkedTelegramUserName
import com.y9vad9.krawler.user.domain.value.UserId

public sealed interface UserUpdateEvent {
    public data class NameChanged(val userId: UserId, val newName: LinkedTelegramUserName) : UserUpdateEvent
    public data class LinkedTelegramChanged(val userId: UserId, val newLinkedTelegram: LinkedTelegram) : UserUpdateEvent
    public data class PlayerAdded(val userId: UserId, val player: BrawlStarsPlayer) : UserUpdateEvent
    public data class PlayerRefreshed(val userId: UserId, val player: BrawlStarsPlayer) : UserUpdateEvent
    public data class PlayerRemoved(val userId: UserId, val playerTag: BrawlStarsPlayerTag) : UserUpdateEvent
    public data class AllPlayersRemoved(val userId: UserId) : UserUpdateEvent
}
