package com.y9vad9.bcm.localization

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer

interface Strings {
    val appName: String

    val guestStartMessage: String
    fun guestWantToJoinClub(clubs: List<Club>): String

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
}