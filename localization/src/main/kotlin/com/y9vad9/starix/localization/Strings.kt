package com.y9vad9.starix.localization

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.system.entity.ClubJoinAbility
import com.y9vad9.starix.core.system.entity.ClubSettings
import com.y9vad9.starix.core.system.entity.Settings
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.foundation.time.UnixTime
import dev.inmo.tgbotapi.types.message.textsources.TextSource
import java.util.*
import kotlin.uuid.Uuid

interface Strings {
    val locale: Locale
    val languageCode: LanguageCode
    val displayName: String

    interface Guest {
        fun guestStartMessage(includedClubs: List<Club>): List<TextSource>

        val viewGitHubRepositoryChoice: String
        val hereToLinkChoice: String
        val herePlanToJoinChoice: String
        val viewContactPersonsChoice: String

        fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): List<TextSource>
        fun guestShowContactsMessage(settings: Settings): List<TextSource>

        val gitHubSourcesMessage: List<TextSource>

        val joinClubChatMessage: List<TextSource>

        val acceptRulesChoice: String

        fun clubRules(value: String): List<TextSource>
        fun chatRules(value: String): List<TextSource>
    }

    val guest: Guest

    val successfullyChangedSettingMessage: String

    val letsLinkBsMessage: List<TextSource>
    val playerNotLinkedMessage: String
    val playerNotInTheClubMessage: String
    val playerAlreadyLinkedBySomeoneMessage: String
    val youAreAlreadyInTheChat: String
    val playerTagNotFoundMessage: String
    val allPlayersChoice: String
    val toGroupChoice: String
    fun successfullyLinkedBsMessage(player: Player): List<TextSource>

    val invalidTagFormatOrSizeMessage: String

    val goBackChoice: String

    val invalidChoiceMessage: String

    fun notInTheClubMessage(states: List<ClubJoinAbility>): List<TextSource>

    val applyForClubChoice: String

    val youAreInGeneralSettingMessage: String

    interface Member {
        val youAreInMemberMenuMessage: String
        fun youAreRegisteredButNotInChatMessage(player: Player): List<TextSource>

        val enableNotificationsMessage: List<TextSource>

        val settings: Settings

        interface Settings {
            val changeLocaleChoice: String
            val changeTimeZoneChoice: String
        }
    }

    val member: Member

    fun wantToJoinClubSuccessMessage(
        player: Player,
        club: Club.View,
    ): List<TextSource>

    val commonWantJoinChatStateSuccessMessage: String

    val joinChatButton: String

    val chat: Chat

    interface Chat {
        fun leftClub(
            player: Player,
            club: Club,
            clubsLeft: List<Club>?,
        ): List<TextSource>

        fun acceptedToTheClubChat(player: Player): List<TextSource>
        fun leftClubChatMessage(player: Player): List<TextSource>
    }

    val chooseClubMessage: String
    val choosePlayersMessage: String

    val continueChoice: String

    val nothingChosenMessage: String

    val noMessageError: String

    val generalSettingsOption: String

    val viewSettingsBeginMessage: List<TextSource>

    val clubNotFoundMessage: String

    val accountIsNotLinked: String

    val constructor: Constructor

    interface Constructor {
        val customMessageFailure: String
        val positiveTrophiesFailure: String
        val telegramUserIdFailure: String
    }

    /**
     * Choices/Messages for the admins within states of club settings management.
     *
     * @see com.y9vad9.bcm.bot.fsm.admin
     */
    interface Admin {
        val youAreInAdminMenuMessage: String
        val clubSettingsOption: String
        val manageMembersOption: String

        val sendMessageSuccessMessage: String

        val provideMessageForPlayers: List<TextSource>

        val settings: Settings

        fun nonLinkedPlayersMessage(list: List<ClubMember>): List<TextSource>
        val showNonLinkedPlayersChoice: String

        val sendMessageChoice: String

        interface Settings {
            val adminsListChoice: String
            fun adminWasAddedMessage(member: ClubMember): List<TextSource>
            fun adminWasRemovedMessage(player: Player): List<TextSource>

            fun adminListWithChooseMessage(admins: List<Player>): List<TextSource>
            val chooseNewAdminMessage: String
            val addAdminOption: String
            val removeAdminOption: String

            fun clubSettingsMessage(settings: ClubSettings): List<TextSource>
            val multiplePlayersChoice: String
            fun multiplePlayersMessage(clubSettings: ClubSettings): List<TextSource>
            val trophieRequirementsChoice: String
            fun trophieRequiremensMessage(clubSettings: ClubSettings): List<TextSource>
            val joinRequestsChoice: String
            fun joinRequestsMessage(clubSettings: ClubSettings): List<TextSource>
            val joinRequestsRequirementsChoice: String
            fun joinRequestsRequirementsMessage(clubSettings: ClubSettings): List<TextSource>

            val linkedGroupChoice: String
            fun linkedGroupMessage(clubSettings: ClubSettings): List<TextSource>

            val linkChoice: String
            val unlinkChoice: String

            val clubRulesChoice: String
            fun clubRulesMessage(clubSettings: ClubSettings): List<TextSource>
            val changeClubRulesMessage: List<TextSource>
            val chatRulesChoice: String
            fun chatRulesMessage(clubSettings: ClubSettings): List<TextSource>
            val changeChatRulesMessage: List<TextSource>

            val rulesCannotBeEmptyMessage: String

            val gracePeriodChoice: String
            fun gracePeriodMessage(playersIn: Map<ClubMember, UnixTime>): List<TextSource>
            val gracePeriodSuccessMessage: String

            val playersLinkageChoice: String
            val viewPlayersLinkageMessage: List<TextSource>
            fun playerManageLinkageMessage(isLinked: Boolean, member: ClubMember): List<TextSource>
            val linkPlayerMessage: List<TextSource>
            val shouldUnlinkFirstMessage: String

            val successfullyChangedOption: String
        }
    }

    val components: Components

    interface Components {
        val calendar: Calendar
        val timeZonePicker: TimeZonePicker
        val languagePicker: LanguagePicker

        interface Calendar {
            val pickYearMessage: List<TextSource>
            val pickMonthMessage: List<TextSource>
            val pickDayMessage: List<TextSource>

            val currentYearChoice: String
            val currentMonthChoice: String
            val currentDayChoice: String

            val untilEndOfDayChoice: String
            val atTheStartOfDayChoice: String
        }

        interface TimeZonePicker {
            fun pickTimeZone(current: String): List<TextSource>
        }

        interface LanguagePicker {
            val chooseLanguageMessage: List<TextSource>
        }
    }

    /**
     * Admin-related localization.
     * @see com.y9vad9.bcm.bot.fsm.admin
     */
    val admin: Admin

    val clubIsNotLinkedToGroup: List<TextSource>

    val noPermissionMessage: String

    val changeOption: String
    val addChoice: String
    val removeChoice: String
    val enableChoice: String
    val disableChoice: String

    fun failureToMessageWithUuid(uuid: Uuid, throwable: Throwable): List<TextSource>
}
