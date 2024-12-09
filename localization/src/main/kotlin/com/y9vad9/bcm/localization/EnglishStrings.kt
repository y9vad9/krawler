package com.y9vad9.bcm.localization

import com.y9vad9.bcm.domain.entity.ClubJoinAbility
import com.y9vad9.bcm.domain.entity.Settings
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag

data object EnglishStrings : Strings {
    override fun guestStartMessage(includedClubs: List<BrawlStarsClub>): String {
        return if (includedClubs.size == 1) {
            val club = includedClubs.first()
            val tagWithoutHashTag = club.tag.toString().replace("#", "")

            "Welcome to the chat bot of <a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">«${club.name.value}»</a> club.\n\n" +
                "What can I do for you?"
        } else {
            buildString {
                append("Welcome to the chat bot for Brawl Stars Clubs Automation. ")
                append("This bot is responsible for the following clubs:\n")
                includedClubs.forEach { club ->
                    val tagWithoutHashTag = club.tag.toString().replace("#", "")
                    append("\t • <a href=\"$tagWithoutHashTag\">${club.name.value}</a>\n")
                }
                append("\nWhat can I do for you?")
            }
        }
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 View Sources"
    override val hereToLinkChoice: String = "I'm here to join chat"
    override val herePlanToJoinChoice: String = "I'm only planning to join club"
    override val viewContactPersonsChoice: String = "☎\uFE0F Contacts"
    override fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): String = buildString {
        if (states.size == 1) {
            append(
                when (val ability = states.first()) {
                    is ClubJoinAbility.NotAvailable -> "Our club currently does accept new members. But we will notify you once we'll have any open position for you!"
                    is ClubJoinAbility.NotEnoughTrophies -> {
                        val amount = ability.club.bs.requiredTrophies.value - ability.playerTrophies.value
                        "It also seems that you don't have enough trophies to join our club. You need $amount more trophies to join." +
                            " Once you will earn them, please try again!" +
                            "\n\n<a href=\"https://www.youtube.com/watch?v=1D7FF5kFyWw\">\uD83C\uDFB5</a>Don't ever give up! I'm waiting for you."
                    }
                    is ClubJoinAbility.OnlyInvite ->
                        "Our club unfortunately is invite-only, meaning that you can't enter it for now. " +
                            "If something changes, I'll notify you as soon as possible."
                    is ClubJoinAbility.Open -> {
                        val availableSeats = 30 - ability.club.bs.members.size
                        "Our club is open and has $availableSeats available seats that are waiting you to join!"
                    }
                    is ClubJoinAbility.UponRequest -> {
                        "If you typed everything correctly and want to join our club, you may apply directly in this bot" +
                            ".\n\n What do you think?"
                    }
                }
            )

            return@buildString
        }

        val trophiesUnmet = states.filterIsInstance<ClubJoinAbility.NotEnoughTrophies>()
        val openClubs = states.filterIsInstance<ClubJoinAbility.Open>()
        val notAvailable = states.filterIsInstance<ClubJoinAbility.NotAvailable>()
        val inviteOnly = states.filterIsInstance<ClubJoinAbility.OnlyInvite>()
        val uponRequest = states.filterIsInstance<ClubJoinAbility.UponRequest>()

        append("The following lists are of clubs you can/cannot join:\n")

        if (trophiesUnmet.isNotEmpty()) {
            append("\t• <b>Not enough trophies</b>")
            trophiesUnmet.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                val amount = ability.club.bs.requiredTrophies.value - ability.playerTrophies.value
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                append(" – requires $amount more trophies to join\n")
            }
            append("\tYou may join them once you have enough trophies.\n")
        }

        if (openClubs.isNotEmpty()) {
            append("\t• <b>Open clubs</b>")
            openClubs.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                append(" – you can join\n")
            }
        }

        if (notAvailable.isNotEmpty()) {
            append("\t• <b>Full clubs</b>")
            notAvailable.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tYou can't join these clubs now.\n")
        }

        if (inviteOnly.isNotEmpty()) {
            append("\t• <b>Invite only</b>")
            inviteOnly.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tYou can't join these clubs unless you have personal invite.\n")
        }

        if (uponRequest.isNotEmpty()) {
            append("\t• <b>Open for join requests</b>")
            uponRequest.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tYou can join open-for-join-requests clubs by clicking the button below.\n")
        }
    }

    override fun guestShowContactsMessage(settings: Settings): String {
        TODO("Not yet implemented")
    }

    override val gitHubSourcesMessage: String = buildString {
        append("This bot is open-source and stored on the GitHub. You're free to explore")
        append(" code <a href=\"https://github.com/y9vad9/bcm\">here</a>.")
    }
    override val letsLinkBsMessage: String = buildString {
        append("Before we continue, let's proceed to linking your Brawl Stars Account")
        append(" to your telegram account.\n\n")
        append("To find your tag, open your profile in the game menu (button in the upper left corner of the screen). ")
        append("The gamer tag is located under your profile picture.\n\n")
        append("If you already did this, please choose the account that you want to go with from the list:")
    }
    override val playerAlreadyLinkedBySomeoneMessage: String =
        "This account is already linked to another Telegram Account. Are you sure it's your account?"
    override val playerNotFoundMessage: String =
        "Specified player's tag is invalid: such player does not exist. Please, retry."
    override fun successfullyLinkedBsMessage(player: BrawlStarsPlayer): String {
        return buildString {
            append("\uD83D\uDC4B Nice to meet you, ${player.name}! Your Brawl Stars Account successfully linked.")
        }
    }
    override val invalidTagFormatOrSizeMessage: String =
        "Tag should be of length ${PlayerTag.REQUIRED_SIZE}, without '#'. Did you format it correctly? \n\nPlease, retry."
    override val goBackChoice: String = "⬅\uFE0F Go back"
    override val invalidChoiceMessage: String = "There's no such choice, please choose from the provided options:"

    override fun notInTheClubMessage(states: List<ClubJoinAbility>): String = buildString {
        val clubOrClubs = if (states.size == 1) "the club" else "any of the clubs"
        append("\uD83D\uDE16 Can't find you in $clubOrClubs – are you actually in and typed everything correctly?\n\n")
        append(guestWantToJoinClubMessage(states))
    }

    override val applyForClubChoice: String = "\uD83D\uDCDD Apply for available clubs"
    override val acceptRulesChoice: String = "✅ Accept rules"

    override fun clubRules(value: String): String {
        return "<b>Main club rules</b>: \n$value"
    }

    override fun chatRules(value: String): String {
        return "<b>Main chat rules</b>: \n$value"
    }

    override val youAreInMemberMenuMessage: String = "You're in your member's menu! Please choice what do you want to do today:"

    override fun youAreRegisteredButNotInChatMessage(player: BrawlStarsPlayer): String {
        return "\uD83E\uDD14 ${player.name}, it seems like you're already in the system, but not in the chat." +
            " But don't worry! Let's proceed to the rules and once we done – I'll provide you a link."
    }

    override val commonWantJoinChatStateSuccessMessage: String =
        "Everything seems alright! Let's proceed to rules and once we done – I'll provide you a link to our chat."

    override fun joinClubChatMessage(inviteLink: String): String {
        return "And here we go! Here's the link for you to join: $inviteLink.\n\nOnce I receive your join request – I'll accept it quickly."
    }

    override fun leftClub(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
        clubsLeft: List<BrawlStarsClub>?,
    ): String {
        val clubTagWithoutHashTag = club.tag.toString().replace("#", "")
        return buildString {
            append("<a href=\"https://brawlify.com/stats/profile/$clubTagWithoutHashTag\">${player.name}</a> ")
            append("left ${club.name}")

            if (clubsLeft.isNullOrEmpty())
                append(" and should be kicked shortly.")
            else append(", but remain in other club(s) that is/are linked to this chat.")
        }
    }

    override fun acceptedToTheClubChat(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
    ): String {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildString {
            append("Attention, everyone! Welcome <a href=\"https://brawlify.com/stats/profile/$playerTagWithoutHashTag\">$player</a> in our chat! ")
            append("\n\nTheir stats:")
            append("\t• \uD83C\uDFC6 General trophies: ${player.trophies.value} (max: ${player.highestTrophies.value})")
            append("\t• \uD83C\uDFB3 Three-VS-Three victories: ${player.threeVsThreeVictories.value}")
            append("\t• \uD83C\uDFC7 Duo victories: ${player.duoVictories.value}")
            append("\t• \uD83E\uDD3A Solo victories: ${player.soloVictories.value}")
            append("\n\nMake yourself at home. \uD83D\uDE09")
        }
    }

    override fun failureToMessage(throwable: Throwable): String {
        return "Unfortunately, unknown error has happened. Please try again later or restart bot via /start."
    }
}