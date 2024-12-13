package com.y9vad9.bcm.localization

import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.system.entity.Settings
import com.y9vad9.bcm.core.user.entity.ClubJoinAbility

interface Strings {
    fun guestStartMessage(
        includedClubs: List<Club>,
    ): String

    val viewGitHubRepositoryChoice: String
    val hereToLinkChoice: String
    val herePlanToJoinChoice: String
    val viewContactPersonsChoice: String

    fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): String
    fun guestShowContactsMessage(settings: Settings): String

    val gitHubSourcesMessage: String

    val letsLinkBsMessage: String
    val playerAlreadyLinkedBySomeoneMessage: String
    val playerNotFoundMessage: String
    fun successfullyLinkedBsMessage(player: Player): String

    val invalidTagFormatOrSizeMessage: String

    val goBackChoice: String

    val invalidChoiceMessage: String

    fun notInTheClubMessage(states: List<ClubJoinAbility>): String

    val applyForClubChoice: String

    val acceptRulesChoice: String

    fun clubRules(value: String): String
    fun chatRules(value: String): String

    val youAreInMemberMenuMessage: String
    fun youAreRegisteredButNotInChatMessage(player: Player): String

    val commonWantJoinChatStateSuccessMessage: String

    fun joinClubChatMessage(inviteLink: String): String

    /**
     * Message about leaving / being kicked from club (we cannot determine it
     * from API).
     *
     * If [clubsLeft] is not null, it means that user remains in the chat
     * due to presence in another club that is linked to the same chat –
     * so it requires notification in the chat message.
     */
    fun leftClub(
        player: Player,
        club: Club,
        clubsLeft: List<Club>?,
    ): String

    /**
     * Message that signals that user came from bot's invite link to the chat.
     */
    fun acceptedToTheClubChat(
        player: Player,
        club: Club,
    ): String

    fun failureToMessage(throwable: Throwable): String
}