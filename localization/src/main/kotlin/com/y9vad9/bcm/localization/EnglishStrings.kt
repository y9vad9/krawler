package com.y9vad9.bcm.localization

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.usecase.CheckClubsAvailabilityUseCase

data object EnglishStrings : Strings {
    override val appName: String
        get() = TODO("Not yet implemented")

    override fun guestStartMessage(includedClubs: List<BrawlStarsClub>): String {
        TODO("Not yet implemented")
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 View Sources"
    override val hereToLinkChoice: String = "I'm here to join chat"
    override val herePlanToJoinChoice: String = "I'm only planning to join club"
    override val viewContactPersonsChoice: String = "☎\uFE0F Contacts"

    override fun guestWantToJoinClub(result: CheckClubsAvailabilityUseCase.Result): String {
        TODO("Not yet implemented")
    }

    override val letsLinkBsMessage: String = ""
    override val wherePlayerTagTitle: String
        get() = TODO("Not yet implemented")
    override val playerAlreadyLinkedBySomeoneMessage: String
        get() = TODO("Not yet implemented")
    override val playerNotFoundMessage: String
        get() = TODO("Not yet implemented")
    override val successfullyLinkedBsMessage: String
        get() = TODO("Not yet implemented")
    override val invalidTagFormatOrSizeMessage: String
        get() = TODO("Not yet implemented")
    override val goBackChoice: String
        get() = TODO("Not yet implemented")

    override fun leftClub(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
        clubsLeft: List<BrawlStarsClub>?,
    ): String {
        TODO("Not yet implemented")
    }

    override fun acceptedToTheClubChat(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
    ): String {
        TODO("Not yet implemented")
    }

    override fun failureToMessage(throwable: Throwable): String {
        TODO("Not yet implemented")
    }

}