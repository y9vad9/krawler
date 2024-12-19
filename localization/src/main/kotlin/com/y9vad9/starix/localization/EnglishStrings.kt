package com.y9vad9.starix.localization

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.entity.Settings
import com.y9vad9.bcm.core.system.entity.ClubJoinAbility
import dev.inmo.tgbotapi.types.message.textsources.TextSource
import dev.inmo.tgbotapi.utils.bold
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.link
import dev.inmo.tgbotapi.utils.newLine

data class EnglishStrings(private val botTag: String) : Strings {
    override fun guestStartMessage(includedClubs: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>): List<TextSource> {
        return if (includedClubs.size == 1) {
            val club = includedClubs.first()
            val tagWithoutHashTag = club.tag.toString().replace("#", "")
            buildEntities {
                +"Welcome to the chat bot of " + link(
                    "«${club.name.value}»",
                    "https://brawlify.com/stats/club/$tagWithoutHashTag"
                ) + " club."
                +newLine
                +newLine
                +"I will accompany you throughout your time in our club – reminding you of events, "
                +"monitoring your activity, and much more!"
                +newLine
                +newLine
                +"What can I do for you?"
            }
        } else {
            buildEntities {
                +"Welcome to the chat bot for Brawl Stars Clubs Automation. This bot is responsible for the following clubs:"
                includedClubs.forEach { club ->
                    val tagWithoutHashTag = club.tag.toString().replace("#", "")
                    +"• " + link("${club.name.value}", "https://brawlify.com/stats/club/$tagWithoutHashTag")
                }

                +newLine
                +newLine

                +"I will accompany you throughout your time in our club – reminding you of events, "
                +"monitoring your activity, and much more!"

                +newLine
                +newLine


                +"What can I do for you?"
            }
        }
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 View Sources"
    override val hereToLinkChoice: String = "\uD83D\uDCAC I'm here to join chat"
    override val herePlanToJoinChoice: String = "\uD83D\uDC40 I'm only planning to join club"
    override val viewContactPersonsChoice: String = "☎️ Contacts"

    override fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): List<TextSource> = buildEntities {
        if (states.size == 1) {
            when (val ability = states.first()) {
                is ClubJoinAbility.NotAvailable -> {
                    +"Our club currently does not accept new members. But we will notify you once we have any open position for you!"
                }

                is ClubJoinAbility.NotEnoughTrophies -> {
                    val amount = ability.club.requiredTrophies.value - ability.playerTrophies.value
                    +"It also seems that you don't have enough trophies to join our club. You need $amount more trophies to join. Once you earn them, please try again!"
                    +"Don't ever give up! I'm waiting for you." + link(
                        "Watch here",
                        "https://www.youtube.com/watch?v=1D7FF5kFyWw"
                    )
                }

                is ClubJoinAbility.OnlyInvite -> {
                    +"Our club is invite-only, meaning that you can't enter it for now. If something changes, I'll notify you."
                }

                is ClubJoinAbility.Open -> {
                    val availableSeats = 30 - ability.club.members.size
                    +"Our club is open and has $availableSeats available seats that are waiting for you to join!"
                }

                is ClubJoinAbility.UponRequest -> {
                    +"If you typed everything correctly and want to join our club, you may apply directly in this bot."
                    +"What do you think?"
                }
            }
        }
    }

    override fun guestShowContactsMessage(settings: Settings): List<TextSource> {
        return buildEntities {
            +"Here are the contact persons."
        }
    }

    override val gitHubSourcesMessage: List<TextSource> = buildEntities {
        +"This bot is open-source and stored on GitHub. You're free to explore the code at "
        link("here", "https://github.com/y9vad9/bcm")
    }

    override val letsLinkBsMessage: List<TextSource> = buildEntities {
        +"Before we continue, let's proceed to linking your Brawl Stars Account to your Telegram account."
        +"To find your tag, open your profile in the game menu (button in the upper left corner of the screen). The gamer tag is located under your profile picture."
        +"If you already did this, please choose the account you want to go with from the list:"
    }

    override val playerAlreadyLinkedBySomeoneMessage: String =
        "This account is already linked to another Telegram Account. Are you sure it's your account?"
    override val playerTagNotFoundMessage: String =
        "Specified player's tag is invalid: such player does not exist. Please, retry."
    override val allPlayersChoice: String = "All players"
    override val toGroupChoice: String = "To the group"
    override val youAreAlreadyInTheChat: String = "You're already in our chat. \uD83D\uDE09"

    override fun successfullyLinkedBsMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        return buildEntities {
            +"Nice to meet you, ${player.name.value}! Your Brawl Stars Account was successfully linked."
        }
    }

    override fun nonLinkedPlayersMessage(list: List<ClubMember>): List<TextSource> {
        TODO("Not yet implemented")
    }

    override val showNonLinkedPlayersChoice: String
        get() = TODO("Not yet implemented")
    override val sendMessageChoice: String
        get() = TODO("Not yet implemented")

    override fun wantToJoinClubSuccessMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player, club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View): List<TextSource> {
        val clubTag = club.tag.toString().replace("#", "")
        return buildEntities {
            +"\uD83C\uDF7E Perfect! Found you in our club «"
            link(club.name.value, "https://brawlify.com/stats/club/$clubTag")
            +"»! Let's proceed to our rules and I'll add you to our chat."
        }
    }

    override val invalidTagFormatOrSizeMessage: String =
        "Tag should be of length ${PlayerTag.REQUIRED_SIZE}, without '#'. Did you format it correctly? Please retry."
    override val goBackChoice: String = "⬅️ Go back"
    override val invalidChoiceMessage: String = "There's no such choice, please choose from the provided options:"

    override fun notInTheClubMessage(states: List<ClubJoinAbility>): List<TextSource> = buildEntities {
        val clubOrClubs = if (states.size == 1) "the club" else "any of the clubs"
        +"Can't find you in $clubOrClubs – are you actually in and typed everything correctly?"
        +guestWantToJoinClubMessage(states)
    }

    override val applyForClubChoice: String = "📝 Apply for available clubs"
    override val acceptRulesChoice: String = "✅ Accept rules"

    override fun clubRules(value: String): List<TextSource> {
        return buildEntities {
            bold("Main club rules: ")
            +value
        }
    }

    override fun chatRules(value: String): List<TextSource> {
        return buildEntities {
            bold("Main chat rules: ")
            +value
        }
    }

    override val youAreInMemberMenuMessage: String =
        "You're in your member's menu! Please choose what you want to do today:"
    override val youAreInAdminMenuMessage: String
        get() = TODO("Not yet implemented")

    override fun youAreRegisteredButNotInChatMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        return buildEntities {
            +"${player.name}, it seems like you're already in the system, but not in the chat. But don't worry! Let's proceed to the rules, and once we're done – I'll provide you a link."
        }
    }

    override val commonWantJoinChatStateSuccessMessage: String =
        "Everything seems alright! Let's proceed to rules, and once we're done – I'll provide you a link to our chat."

    override val joinClubChatMessage: List<TextSource> = buildEntities {
        +"Thanks for accepting our rules. All is left for you is to click the button below to send a chat join request."
        +"Once I receive your join request – I'll accept it quickly."
    }

    override val joinChatButton: String = "\uD83D\uDD17 Join chat"

    override fun leftClub(
        player: com.y9vad9.starix.core.brawlstars.entity.player.Player,
        club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club,
        clubsLeft: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>?,
    ): List<TextSource> {
        val clubTagWithoutHashTag = club.tag.toString().replace("#", "")
        return buildEntities {
            +"${player.name} left " + link("${club.name}", "https://brawlify.com/stats/profile/$clubTagWithoutHashTag")
            if (clubsLeft.isNullOrEmpty()) {
                +" and should be kicked shortly."
            } else {
                +" but remain in other club(s) linked to this chat."
            }
        }
    }

    override fun acceptedToTheClubChat(
        player: com.y9vad9.starix.core.brawlstars.entity.player.Player,
    ): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities {
            +"Attention, everyone! Welcome " + link(
                player.name.value,
                "https://brawlify.com/stats/profile/$playerTagWithoutHashTag"
            ) + " to our chat!"
            +"Their stats:"
            +"• General trophies: ${player.trophies.value} (max: ${player.highestTrophies.value})"
            +"• Three-VS-Three victories: ${player.threeVsThreeVictories.value}"
            +"• Duo victories: ${player.duoVictories.value}"
            +"• Solo victories: ${player.soloVictories.value}"
            +"Make yourself at home."
        }
    }

    override fun leftClubChatMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities("") {
            link(player.name.value, "https://brawlify.com/stats/profile/$playerTagWithoutHashTag")
            +" left our chat. \uD83D\uDE41"
        }
    }

    override val enableNotificationsMessage: List<TextSource> = buildEntities {
        +"Don't forget to turn on notifications for me: I will automatically notify you if you are inactive and about much more!"
        +newLine
        +newLine
        +"To do that, open my profile → three dots → notifications → enable."
    }
    override val chooseClubMessage: String
        get() = TODO("Not yet implemented")
    override val choosePlayersMessage: String
        get() = TODO("Not yet implemented")
    override val continueChoice: String
        get() = TODO("Not yet implemented")
    override val nothingChosenMessage: String
        get() = TODO("Not yet implemented")
    override val sendMessageSuccessMessage: String
        get() = TODO("Not yet implemented")
    override val provideMessageForPlayers: List<TextSource>
        get() = TODO("Not yet implemented")
    override val noMessageError: String
        get() = TODO("Not yet implemented")

    override fun failureToMessage(throwable: Throwable): String {
        return "Unfortunately, an unknown error has occurred. Please try again later or restart the bot via /start."
    }
}
