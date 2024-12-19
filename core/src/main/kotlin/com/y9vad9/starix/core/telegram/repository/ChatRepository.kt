package com.y9vad9.starix.core.telegram.repository

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.common.entity.value.Link
import com.y9vad9.bcm.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId

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
            val player: com.y9vad9.starix.core.brawlstars.entity.player.Player,
            val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club,
            val clubsLeft: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>?,
        ) : GroupMessage

        /**
         * Message that signals that user came from bot's invite link to the chat.
         */
        data class JoinRequestAccepted(
            val player: com.y9vad9.starix.core.brawlstars.entity.player.Player,
            val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club,
            val chatLink: Link,
            val clubLink: Link,
        ) : GroupMessage
    }
}