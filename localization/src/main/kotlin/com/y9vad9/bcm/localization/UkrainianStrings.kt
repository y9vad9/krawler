package com.y9vad9.bcm.localization

import com.y9vad9.bcm.domain.entity.ClubJoinAbility
import com.y9vad9.bcm.domain.entity.Settings
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag

data object UkrainianStrings : Strings {
    override fun guestStartMessage(includedClubs: List<BrawlStarsClub>): String {
        return if (includedClubs.size == 1) {
            val club = includedClubs.first()
            val tagWithoutHashTag = club.tag.toString().replace("#", "")

            "Ласкаво просимо до чат-бота клубу <a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">«${club.name.value}»</a>.\n\n" +
                "Чим я можу вам допомогти?"
        } else {
            buildString {
                append("Ласкаво просимо до чат-бота для автоматизації клубів Brawl Stars. ")
                append("Цей бот відповідає за наступні клуби:\n")
                includedClubs.forEach { club ->
                    val tagWithoutHashTag = club.tag.toString().replace("#", "")
                    append("\t • <a href=\"$tagWithoutHashTag\">${club.name.value}</a>\n")
                }
                append("\nЧим я можу вам допомогти?")
            }
        }
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 Переглянути джерела"
    override val hereToLinkChoice: String = "Я тут, щоб приєднатися до чату"
    override val herePlanToJoinChoice: String = "Я тільки планую приєднатися до клубу"
    override val viewContactPersonsChoice: String = "☎\uFE0F Контакти"
    override fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): String = buildString {
        if (states.size == 1) {
            append(
                when (val ability = states.first()) {
                    is ClubJoinAbility.NotAvailable -> "Наш клуб зараз не приймає нових учасників. Але ми повідомимо вас, як тільки у нас з'явиться вільне місце для вас!"
                    is ClubJoinAbility.NotEnoughTrophies -> {
                        val amount = ability.club.bs.requiredTrophies.value - ability.playerTrophies.value
                        "Здається, у вас недостатньо трофеїв для приєднання до нашого клубу. Вам потрібно ще $amount трофеїв, щоб приєднатися." +
                            " Коли ви їх заробите, будь ласка, спробуйте знову!" +
                            "\n\n<a href=\"https://www.youtube.com/watch?v=1D7FF5kFyWw\">\uD83C\uDFB5</a>Не здавайтесь! Я чекаю на вас."
                    }
                    is ClubJoinAbility.OnlyInvite ->
                        "Наш клуб, на жаль, лише за запрошенням, що означає, що ви не можете приєднатися до нього зараз. " +
                            "Якщо щось зміниться, я повідомлю вас якнайшвидше."
                    is ClubJoinAbility.Open -> {
                        val availableSeats = 30 - ability.club.bs.members.size
                        "Наш клуб відкритий і має $availableSeats вільних місць, які чекають на ваше приєднання!"
                    }
                    is ClubJoinAbility.UponRequest -> {
                        "Якщо ви все правильно ввели і хочете приєднатися до нашого клубу, ви можете подати заявку безпосередньо через цього бота" +
                            ".\n\n Що ви думаєте?"
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

        append("Ось списки клубів, до яких ви можете/не можете приєднатися:\n")

        if (trophiesUnmet.isNotEmpty()) {
            append("\t• <b>Недостатньо трофеїв</b>")
            trophiesUnmet.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                val amount = ability.club.bs.requiredTrophies.value - ability.playerTrophies.value
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                append(" – потрібно ще $amount трофеїв, щоб приєднатися\n")
            }
            append("\tВи можете приєднатися, коли буде достатньо трофеїв.\n")
        }

        if (openClubs.isNotEmpty()) {
            append("\t• <b>Відкриті клуби</b>")
            openClubs.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                append(" – ви можете приєднатися\n")
            }
        }

        if (notAvailable.isNotEmpty()) {
            append("\t• <b>Закриті клуби</b>")
            notAvailable.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tВи не можете приєднатися до цих клубів зараз.\n")
        }

        if (inviteOnly.isNotEmpty()) {
            append("\t• <b>Тільки за запрошенням</b>")
            inviteOnly.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tВи не можете приєднатися до цих клубів без особистого запрошення.\n")
        }

        if (uponRequest.isNotEmpty()) {
            append("\t• <b>Відкрито для заявок</b>")
            uponRequest.forEach { ability ->
                val tagWithoutHashTag = ability.club.bs.tag.toString().replace("#", "")
                append("\t\t<a href=\"https://brawlify.com/stats/club/$tagWithoutHashTag\">${ability.club.bs.name.value}</a>")
                appendLine()
            }
            append("\tВи можете приєднатися до клубів, відкритих для заявок, натиснувши кнопку нижче.\n")
        }
    }

    override fun guestShowContactsMessage(settings: Settings): String {
        TODO("Не реалізовано")
    }

    override val gitHubSourcesMessage: String = buildString {
        append("Цей бот є відкритим кодом і зберігається на GitHub. Ви можете дослідити")
        append(" код <a href=\"https://github.com/y9vad9/bcm\">тут</a>.")
    }
    override val letsLinkBsMessage: String = buildString {
        append("Перш ніж продовжити, давайте зв'яжемо ваш акаунт Brawl Stars")
        append(" з вашим акаунтом Telegram.\n\n")
        append("Щоб знайти свій тег, відкрийте свій профіль у меню гри (кнопка у верхньому лівому кутку екрану). ")
        append("Тег гравця знаходиться під вашою аватаркою.\n\n")
        append("Якщо ви вже це зробили, будь ласка, виберіть акаунт, з яким хочете працювати:")
    }
    override val playerAlreadyLinkedBySomeoneMessage: String =
        "Цей акаунт вже пов'язаний з іншим акаунтом Telegram. Ви впевнені, що це ваш акаунт?"
    override val playerNotFoundMessage: String =
        "Вказаний тег гравця неправильний: такого гравця не існує. Будь ласка, спробуйте ще раз."
    override fun successfullyLinkedBsMessage(player: BrawlStarsPlayer): String {
        return buildString {
            append("\uD83D\uDC4B Радий зустрічі, ${player.name}! Ваш акаунт Brawl Stars успішно прив'язано.")
        }
    }
    override val invalidTagFormatOrSizeMessage: String =
        "Тег повинен бути довжиною ${PlayerTag.REQUIRED_SIZE}, без '#'. Ви правильно його відформатували? \n\nБудь ласка, спробуйте знову."
    override val goBackChoice: String = "⬅\uFE0F Назад"
    override val invalidChoiceMessage: String = "Немає такого вибору, виберіть один із запропонованих варіантів:"

    override fun notInTheClubMessage(states: List<ClubJoinAbility>): String = buildString {
        val clubOrClubs = if (states.size == 1) "клуб" else "будь-який з клубів"
        append("\uD83D\uDE16 Не можу знайти вас у $clubOrClubs – чи ви насправді в ньому та правильно ввели все?\n\n")
        append(guestWantToJoinClubMessage(states))
    }

    override val applyForClubChoice: String = "\uD83D\uDCDD Подати заявку на доступні клуби"
    override val acceptRulesChoice: String = "✅ Прийняти правила"

    override fun clubRules(value: String): String {
        return "<b>Основні правила клубу</b>: \n$value"
    }

    override fun chatRules(value: String): String {
        return "<b>Основні правила чату</b>: \n$value"
    }

    override val youAreInMemberMenuMessage: String = "Ви в меню учасника! Будь ласка, виберіть, що ви хочете зробити сьогодні:"

    override fun youAreRegisteredButNotInChatMessage(player: BrawlStarsPlayer): String {
        return "\uD83E\uDD14 ${player.name}, здається, ви вже в системі, але не в чаті." +
            " Але не хвилюйтеся! Давайте перейдемо до правил, і після того, як ми все закінчимо, я надам вам посилання."
    }

    override val commonWantJoinChatStateSuccessMessage: String =
        "Все здається в порядку! Давайте перейдемо до правил, і після того, як ми все закінчимо, я надам вам посилання на наш чат."

    override fun joinClubChatMessage(inviteLink: String): String {
        return "Ось і все! Ось посилання для приєднання: $inviteLink"
    }

    override fun leftClub(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
        clubsLeft: List<BrawlStarsClub>?,
    ): String {
        val clubTagWithoutHashTag = club.tag.toString().replace("#", "")
        return buildString {
            append("<a href=\"https://brawlify.com/stats/profile/$clubTagWithoutHashTag\">${player.name}</a> ")
            append("покинув клуб ${club.name}")

            if (clubsLeft.isNullOrEmpty())
                append(" і найближчим часом буде видалений.")
            else append(", але залишився в інших клубах, пов'язаних з цим чатом.")
        }
    }

    override fun acceptedToTheClubChat(
        player: BrawlStarsPlayer,
        club: BrawlStarsClub,
    ): String {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildString {
            append("Увага, всі! Ласкаво просимо <a href=\"https://brawlify.com/stats/profile/$playerTagWithoutHashTag\">$player</a> в наш чат! ")
            append("\n\nЇхні статистики:")
            append("\t• \uD83C\uDFC6 Загальні трофеї: ${player.trophies.value} (макс: ${player.highestTrophies.value})")
            append("\t• \uD83C\uDFB3 Перемоги в трьох проти трьох: ${player.threeVsThreeVictories.value}")
            append("\t• \uD83C\uDFC7 Перемоги в дуо: ${player.duoVictories.value}")
            append("\t• \uD83E\uDD3A Перемоги в соло: ${player.soloVictories.value}")
            append("\n\nВідчувайте себе як вдома. \uD83D\uDE09")
        }
    }

    override fun failureToMessage(throwable: Throwable): String {
        return "На жаль, сталася невідома помилка. Спробуйте пізніше або перезапустіть бота за допомогою /start."
    }

}
