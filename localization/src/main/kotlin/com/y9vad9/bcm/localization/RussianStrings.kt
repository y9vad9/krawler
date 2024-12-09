package com.y9vad9.bcm.localization

import com.y9vad9.bcm.domain.entity.ClubJoinAbility
import com.y9vad9.bcm.domain.entity.Settings
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag

data object RussianStrings : Strings {
    override fun guestStartMessage(includedClubs: List<BrawlStarsClub>): String {
        return if (includedClubs.size == 1) {
            val club = includedClubs.first()
            val tagWithoutHashTag = club.tag.toString().replace("#", "")

            "Добро пожаловать в чат-бот клуба <a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">«${club.name.value}»</a>.\n\n" +
                "Чем я могу вам помочь?"
        } else {
            buildString {
                append("Добро пожаловать в чат-бот для автоматизации клубов Brawl Stars. ")
                append("Этот бот отвечает за следующие клубы:\n")
                includedClubs.forEach { club ->
                    val tagWithoutHashTag = club.tag.toString().replace("#", "")
                    append("\t • <a href=\"$tagWithoutHashTag\">${club.name.value}</a>\n")
                }
                append("\nЧем я могу вам помочь?")
            }
        }
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 Просмотреть исходный код"
    override val hereToLinkChoice: String = "Я здесь, чтобы присоединиться к чату"
    override val herePlanToJoinChoice: String = "Я только планирую присоединиться к клубу"
    override val viewContactPersonsChoice: String = "☎\uFE0F Контакты"
    override fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): String = buildString {
        if (states.size == 1) {
            append(
                when (val ability = states.first()) {
                    is ClubJoinAbility.NotAvailable -> "Наш клуб в настоящее время не принимает новых участников. Но мы уведомим вас, как только появится свободное место!"
                    is ClubJoinAbility.NotEnoughTrophies -> {
                        val amount = ability.club.bs.requiredTrophies.value - ability.playerTrophies.value
                        "Кажется, у вас недостаточно трофеев для вступления в наш клуб. Вам нужно ещё $amount трофеев. " +
                            "Как только вы их получите, попробуйте снова!" +
                            "\n\n<a href=\"https://www.youtube.com/watch?v=1D7FF5kFyWw\">\uD83C\uDFB5</a>Не сдавайтесь! Я жду вас."
                    }

                    is ClubJoinAbility.OnlyInvite ->
                        "К сожалению, наш клуб доступен только по приглашению, поэтому сейчас вы не можете вступить. " +
                            "Если что-то изменится, я уведомлю вас как можно скорее."

                    is ClubJoinAbility.Open -> {
                        val availableSeats = 30 - ability.club.bs.members.size
                        "Наш клуб открыт и имеет $availableSeats свободных мест для вас!"
                    }

                    is ClubJoinAbility.UponRequest -> {
                        "Если вы всё ввели правильно и хотите присоединиться к нашему клубу, вы можете подать заявку прямо в этом боте" +
                            ".\n\nЧто вы об этом думаете?"
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

        append("Следующий список клубов, в которые вы можете/не можете вступить:\n")

        if (trophiesUnmet.isNotEmpty()) {
            append("\t• <b>Недостаточно трофеев</b>")
            trophiesUnmet.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                val amount = ability.club.bs.requiredTrophies.value - ability.playerTrophies.value
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                append(" – требуется ещё $amount трофеев\n")
            }
            append("\tВы можете вступить в эти клубы, когда наберёте достаточно трофеев.\n")
        }

        if (openClubs.isNotEmpty()) {
            append("\t• <b>Открытые клубы</b>")
            openClubs.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                append(" – вы можете вступить\n")
            }
        }

        if (notAvailable.isNotEmpty()) {
            append("\t• <b>Полные клубы</b>")
            notAvailable.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tВы не можете вступить в эти клубы сейчас.\n")
        }

        if (inviteOnly.isNotEmpty()) {
            append("\t• <b>Только по приглашению</b>")
            inviteOnly.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tВы не можете вступить в эти клубы без личного приглашения.\n")
        }

        if (uponRequest.isNotEmpty()) {
            append("\t• <b>Открытые для заявок</b>")
            uponRequest.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tВы можете вступить в эти клубы, подав заявку через кнопку ниже.\n")
        }
    }

    override fun guestShowContactsMessage(settings: Settings): String {
        TODO("Еще не реализовано")
    }

    override val gitHubSourcesMessage: String = buildString {
        append("Этот бот с открытым исходным кодом хранится на GitHub. Вы можете изучить")
        append(" код <a href=\"https://github.com/y9vad9/bcm\">здесь</a>.")
    }
    override val letsLinkBsMessage: String = buildString {
        append("Прежде чем мы продолжим, давайте привяжем ваш аккаунт Brawl Stars")
        append(" к вашему аккаунту Telegram.\n\n")
        append("Чтобы найти ваш тег, откройте ваш профиль в игровом меню (кнопка в левом верхнем углу экрана). ")
        append("Игровой тег находится под вашей фотографией профиля.\n\n")
        append("Если вы уже сделали это, пожалуйста, выберите аккаунт, с которым хотите продолжить, из списка:")
    }
    override val playerAlreadyLinkedBySomeoneMessage: String =
        "Этот аккаунт уже привязан к другому аккаунту Telegram. Вы уверены, что это ваш аккаунт?"
    override val playerNotFoundMessage: String =
        "Указанный игровой тег недействителен: такого игрока не существует. Пожалуйста, попробуйте снова."

    override fun successfullyLinkedBsMessage(player: BrawlStarsPlayer): String {
        return buildString {
            append("\uD83D\uDC4B Приятно познакомиться, ${player.name}! Ваш аккаунт Brawl Stars успешно привязан.")
        }
    }

    override val invalidTagFormatOrSizeMessage: String =
        "Тег должен быть длиной ${PlayerTag.REQUIRED_SIZE}, без '#'. Вы правильно его отформатировали? \n\nПожалуйста, попробуйте снова."
    override val goBackChoice: String = "⬅️ Назад"
    override val invalidChoiceMessage: String = "Нет такого выбора, пожалуйста, выберите из предложенных вариантов:"

    override fun notInTheClubMessage(states: List<ClubJoinAbility>): String = buildString {
        val clubOrClubs = if (states.size == 1) "клубе" else "каком-либо клубе"
        append("\uD83D\uDE16 Не могу найти вас в $clubOrClubs – вы действительно в нем и правильно все ввели?\n\n")
        append(guestWantToJoinClubMessage(states))
    }

    override val applyForClubChoice: String = "\uD83D\uDCDD Подать заявку в доступные клубы"
    override val acceptRulesChoice: String = "✅ Принять правила"

    override fun clubRules(value: String): String {
        return "<b>Основные правила клуба</b>: \n$value"
    }

    override fun chatRules(value: String): String {
        return "<b>Основные правила чата</b>: \n$value"
    }

    override val youAreInMemberMenuMessage: String = "Вы в меню участника! Пожалуйста, выберите, что хотите сделать сегодня:"

    override fun youAreRegisteredButNotInChatMessage(player: BrawlStarsPlayer): String {
        return "\uD83E\uDD14 ${player.name}, похоже, вы уже в системе, но не в чате." +
            " Но не переживайте! Давайте перейдем к правилам, и как только все будет готово, я предоставлю вам ссылку."
    }

    override val commonWantJoinChatStateSuccessMessage: String =
        "Все вроде в порядке! Давайте перейдем к правилам, и как только все будет готово, я дам вам ссылку на наш чат."

    override fun joinClubChatMessage(inviteLink: String): String {
        return "Вот и все! Вот ссылка, чтобы присоединиться: $inviteLink.\n\nКак только я получу ваш запрос на вступление, я быстро его приму."
    }

    override fun leftClub(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
        clubsLeft: List<BrawlStarsClub>?,
    ): String {
        val clubTagWithoutHashTag = club.tag.toString().replace("#", "")
        return buildString {
            append("<a href=\"https://brawlify.com/stats/profile/$clubTagWithoutHashTag\">${player.name}</a> ")
            append("покинул(а) ${club.name}")

            if (clubsLeft.isNullOrEmpty())
                append(" и скоро будет исключен(а).")
            else append(", но остался(ась) в других клубах, связанных с этим чатом.")
        }
    }

    override fun acceptedToTheClubChat(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
    ): String {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildString {
            append("Внимание, все! Добро пожаловать <a href=\"https://brawlify.com/stats/profile/$playerTagWithoutHashTag\">$player</a> в наш чат! ")
            append("\n\nЕго/ее статистика:")
            append("\t• \uD83C\uDFC6 Общие трофеи: ${player.trophies.value} (макс: ${player.highestTrophies.value})")
            append("\t• \uD83C\uDFB3 Победы в Три-Против-Трех: ${player.threeVsThreeVictories.value}")
            append("\t• \uD83C\uDFC7 Победы в Дуо: ${player.duoVictories.value}")
            append("\t• \uD83E\uDD3A Победы в Соло: ${player.soloVictories.value}")
            append("\n\nЧувствуйте себя как дома. \uD83D\uDE09")
        }
    }

    override fun failureToMessage(throwable: Throwable): String {
        return "К сожалению, произошла неизвестная ошибка. Пожалуйста, попробуйте позже или перезапустите бота с помощью /start."
    }

}