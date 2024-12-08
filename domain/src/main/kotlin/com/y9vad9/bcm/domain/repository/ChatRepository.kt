package com.y9vad9.bcm.domain.repository

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramGroupId
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.Link

interface ChatRepository {
    suspend fun isMemberOfGroup(groupId: TelegramGroupId, userId: TelegramUserId): Boolean
    suspend fun kick(chatId: TelegramGroupId, userId: TelegramUserId): Boolean
    suspend fun sendGroupMessage(groupId: TelegramGroupId, message: GroupMessage): Boolean

    suspend fun createInviteLink(groupId: TelegramGroupId): Link

    sealed interface GroupMessage {
        /**
         * Message about leaving / being kicked from club (we cannot determine it
         * from API).
         *
         * If [clubsLeft] is not null, it means that user remains in the chat
         * due to presence in another club that is linked to the same chat –
         * so it requires notification in the chat message.
         */
        data class LeftClub(
            val player: BrawlStarsPlayer,
            val club: Club,
            val clubsLeft: List<Club>?,
        ) : GroupMessage

        /**
         * Message that signals that user came from bot's invite link to the chat.
         */
        data class JoinRequestAccepted(
            val player: BrawlStarsPlayer,
            val club: BrawlStarsClub,
            val chatLink: Link,
            val clubLink: Link,
        ) : GroupMessage
    }
}