package com.y9vad9.bcm.data

import com.y9vad9.bcm.core.common.entity.value.Link
import com.y9vad9.bcm.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.telegram.repository.ChatRepository
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import com.y9vad9.bcm.localization.Strings
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.chat.invite_links.createChatInviteLinkWithLimitedMembers
import dev.inmo.tgbotapi.extensions.api.chat.members.banChatMember
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.chat.members.setChatAdministratorCustomTitle
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.utils.extensions.isLeftOrKicked
import dev.inmo.tgbotapi.extensions.utils.requireGroupChat
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.PreviewFeature

class ChatRepositoryImpl(
    private val bot: TelegramBot,
    private val strings: Strings,
) : ChatRepository {
    @OptIn(PreviewFeature::class)
    override suspend fun isMemberOfGroup(
        groupId: TelegramGroupId,
        userId: TelegramUserId,
    ): Boolean {
        val group = bot.getChat(groupId.asChatId()).requireGroupChat()
        return !bot.getChatMember(group, userId.asUserId()).isLeftOrKicked
    }

    override suspend fun kick(
        chatId: TelegramGroupId,
        userId: TelegramUserId,
    ): Boolean {
        bot.setChatAdministratorCustomTitle(chatId.asChatId(), userId.asUserId(), "")
        return bot.banChatMember(chatId.asChatId(), userId.asUserId())
    }

    override suspend fun sendGroupMessage(
        groupId: TelegramGroupId,
        message: ChatRepository.GroupMessage,
    ): Boolean = try {
        bot.send(
            chatId = groupId.asChatId(),
            text = when (message) {
                is ChatRepository.GroupMessage.JoinRequestAccepted ->
                    strings.acceptedToTheClubChat(message.player, message.club)

                is ChatRepository.GroupMessage.LeftClub ->
                    strings.leftClub(
                        player = message.player,
                        club = message.club,
                        clubsLeft = message.clubsLeft,
                    )
            }
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun createInviteLink(groupId: TelegramGroupId): Link {
        return bot.createChatInviteLinkWithLimitedMembers(
            chatId = groupId.asChatId(),
            membersLimit = 1,
        ).inviteLink.let { Link.createUnsafe(it) }
    }

}

private fun TelegramGroupId.asChatId(): ChatId = ChatId(RawChatId(value))
private fun TelegramUserId.asUserId(): UserId = UserId(RawChatId(value))