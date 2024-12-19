package com.y9vad9.starix.core.system.entity

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies

sealed interface ClubJoinAbility {
    val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club

    data class NotEnoughTrophies(override val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club, val playerTrophies: Trophies) : ClubJoinAbility

    /**
     * The system of clubs/club has no available seats at all.
     */
    data class NotAvailable(override val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club) : ClubJoinAbility

    /**
     * The system of clubs/club has only invite mode, but with available seats.
     */
    data class OnlyInvite(override val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club) : ClubJoinAbility

    /**
     * Club accepts join requests via bot.
     */
    data class UponRequest(override val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club) : ClubJoinAbility

    /**
     * The System of clubs/club has open mode and free seats.
     */
    data class Open(override val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club) : ClubJoinAbility
}

fun List<ClubJoinAbility>.anyAtLeastForRequest(): Boolean {
    return any { ability ->
        ability is ClubJoinAbility.OnlyInvite || ability is ClubJoinAbility.UponRequest ||
            ability is ClubJoinAbility.Open
    }
}