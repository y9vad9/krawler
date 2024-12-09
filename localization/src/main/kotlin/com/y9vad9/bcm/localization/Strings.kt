package com.y9vad9.bcm.localization

import com.y9vad9.bcm.domain.entity.ClubJoinAbility
import com.y9vad9.bcm.domain.entity.ClubSettings
import com.y9vad9.bcm.domain.entity.Settings
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag

interface Strings {
    val appName: String

    fun guestStartMessage(
        includedClubs: List<BrawlStarsClub>,
    ): String

    val viewGitHubRepositoryChoice: String
    val hereToLinkChoice: String
    val herePlanToJoinChoice: String
    val viewContactPersonsChoice: String

    fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): String
    fun guestShowContactsMessage(settings: Settings): String

    val gitHubSourcesMessage: String

    val letsLinkBsMessage: String
    val wherePlayerTagTitle: String
    val playerAlreadyLinkedBySomeoneMessage: String
    val playerNotFoundMessage: String
    val successfullyLinkedBsMessage: String

    val invalidTagFormatOrSizeMessage: String

    val goBackChoice: String

    val invalidChoiceMessage: String

    fun notInTheClubMessage(states: List<ClubJoinAbility>): String
    val notInClubAndNoClubsAvailableToJoinMessage: String

    val applyForClubChoice: String

    val acceptRulesChoice: String

    fun clubRules(value: String): String
    fun chatRules(value: String): String

    val youAreInMemberMenuMessage: String
    fun youAreRegisteredButNotInChatMessage(player: BrawlStarsPlayer): String

    val bsPlayerAlreadyLinkedMessage: String

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
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
        clubsLeft: List<BrawlStarsClub>?,
    ): String

    /**
     * Message that signals that user came from bot's invite link to the chat.
     */
    fun acceptedToTheClubChat(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
    ): String

    fun failureToMessage(throwable: Throwable): String
}