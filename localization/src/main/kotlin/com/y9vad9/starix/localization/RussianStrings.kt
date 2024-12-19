package com.y9vad9.starix.localization

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.entity.Settings
import com.y9vad9.bcm.core.system.entity.ClubJoinAbility
import dev.inmo.tgbotapi.types.message.textsources.TextSource
import dev.inmo.tgbotapi.types.message.textsources.bold
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.code
import dev.inmo.tgbotapi.utils.link
import dev.inmo.tgbotapi.utils.newLine

data class RussianStrings(private val botTag: String) : Strings {
    override fun guestStartMessage(includedClubs: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>): List<TextSource> {
        return if (includedClubs.size == 1) {
            val club = includedClubs.first()
            val tagWithoutHashTag = club.tag.toString().replace("#", "")
            buildEntities("") {
                +"\uD83E\uDD16 Добро пожаловать в чат-бот клуба "
                link("«${club.name.value}»", "https://brawlify.com/stats/club/$tagWithoutHashTag")
                +"."
                +newLine
                +newLine

                +"Я буду сопровождать тебя всё время пока ты в нашем клубе – напоминать о событиях, "
                +"следить за твоей активностью и много чего другого!"

                +newLine
                +newLine
                +"Чем я могу вам помочь?"
            }
        } else {
            buildEntities("") {
                +"Добро пожаловать в чат-бот для автоматизации клубов Brawl Stars. "
                +"Этот бот отвечает за следующие клубы: "
                includedClubs.forEach { club ->
                    val tagWithoutHashTag = club.tag.toString().replace("#", "")
                    link(club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                }

                +"Я буду сопровождать тебя всё время пока ты в нашем клубе – напоминать о событиях, "
                +"следить за твоей активностью и много чего другого!"

                +"Чем я могу вам помочь?"
            }
        }
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 Просмотреть исходный код"
    override val hereToLinkChoice: String = "\uD83D\uDCAC Я здесь, чтобы присоединиться к чату"
    override val herePlanToJoinChoice: String = "\uD83D\uDC40 Я только планирую присоединиться к клубу"
    override val viewContactPersonsChoice: String = "☎\uFE0F Контакты"

    override fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): List<TextSource> {
        return buildEntities("") {
            if (states.size == 1) {
                when (val ability = states.first()) {
                    is ClubJoinAbility.NotAvailable -> +"Наш клуб в настоящее время не принимает новых участников. Но мы уведомим вас, как только появится свободное место!"
                    is ClubJoinAbility.NotEnoughTrophies -> {
                        val amount = ability.club.requiredTrophies.value - ability.playerTrophies.value
                        +"Кажется, у вас недостаточно трофеев для вступления в наш клуб. Вам нужно ещё $amount трофеев. Как только вы их получите, попробуйте снова!"
                        link("\uD83C\uDFB5 Не сдавайтесь! Я жду вас.", "https://www.youtube.com/watch?v=1D7FF5kFyWw")
                    }
                    is ClubJoinAbility.OnlyInvite -> +"К сожалению, наш клуб доступен только по приглашению, поэтому сейчас вы не можете вступить. Если что-то изменится, я уведомлю вас как можно скорее."
                    is ClubJoinAbility.Open -> {
                        val availableSeats = 30 - ability.club.members.size
                        +"Наш клуб открыт и имеет $availableSeats свободных мест для вас!"
                    }
                    is ClubJoinAbility.UponRequest -> +"Если хочешь присоединиться к нашему клубу, ты можешь подать заявку прямо в этом боте. Что думаешь? \uD83D\uDE0A\uFE0F\uFE0F\uFE0F\uFE0F\uFE0F\uFE0F"
                }
            } else {
                val trophiesUnmet = states.filterIsInstance<ClubJoinAbility.NotEnoughTrophies>()
                val openClubs = states.filterIsInstance<ClubJoinAbility.Open>()
                val notAvailable = states.filterIsInstance<ClubJoinAbility.NotAvailable>()
                val inviteOnly = states.filterIsInstance<ClubJoinAbility.OnlyInvite>()
                val uponRequest = states.filterIsInstance<ClubJoinAbility.UponRequest>()

                +"Следующий список клубов, в которые вы можете/не можете вступить:"

                if (trophiesUnmet.isNotEmpty()) {
                    +"• Недостаточно трофеев"
                    trophiesUnmet.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                        +"– требуется ещё ${ability.club.requiredTrophies.value - ability.playerTrophies.value} трофеев"
                    }
                    +"Вы можете вступить в эти клубы, когда наберёте достаточно трофеев."
                }

                if (openClubs.isNotEmpty()) {
                    +"• Открытые клубы"
                    openClubs.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                        +"– вы можете вступить"
                    }
                }

                if (notAvailable.isNotEmpty()) {
                    +"• Полные клубы"
                    notAvailable.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                    }
                    +"Вы не можете вступить в эти клубы сейчас."
                }

                if (inviteOnly.isNotEmpty()) {
                    +"• Только по приглашению"
                    inviteOnly.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                    }
                    +"Вы не можете вступить в эти клубы без личного приглашения."
                }

                if (uponRequest.isNotEmpty()) {
                    +"• Открытые для заявок"
                    uponRequest.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                    }
                    +"Вы можете вступить в эти клубы, подав заявку через кнопку ниже."
                }
            }
        }
    }

    override fun guestShowContactsMessage(settings: Settings): List<TextSource> {
        TODO("Еще не реализовано")
    }

    override val gitHubSourcesMessage: List<TextSource> = buildEntities("") {
        +"Этот бот с открытым исходным кодом хранится на GitHub. Вы можете изучить код "
        link("тут", "https://github.com/y9vad9/bcm") + "."
    }

    override val letsLinkBsMessage: List<TextSource> = buildEntities("") {
        +"Прежде чем мы продолжим, давайте привяжем ваш аккаунт Brawl Stars к вашему аккаунту Telegram."
        +newLine
        +newLine
        +"Чтобы найти ваш тег, откройте ваш профиль в игровом меню Brawl Stars (кнопка в левом верхнем углу экрана). Игровой тег находится под вашей фотографией профиля."
        +" Игровой тэг для примера выглядит следующим образом: " + code("#ABCD12345") +"."
    }

    override val playerAlreadyLinkedBySomeoneMessage: String =
        "Этот аккаунт уже привязан к другому аккаунту Telegram. Вы уверены, что это ваш аккаунт?"

    override val youAreAlreadyInTheChat: String = "Ты уже есть в чате нашего клуба. \uD83D\uDE09"

    override val playerTagNotFoundMessage: String =
        "Указанный игровой тег недействителен: такого игрока не существует. Пожалуйста, попробуйте снова."
    override val allPlayersChoice: String = "Все игроки"
    override val toGroupChoice: String = "В группу"

    override fun successfullyLinkedBsMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        return buildEntities("") {
            +"Приятно познакомиться, ${player.name.value}! Ваш аккаунт Brawl Stars успешно привязан."
        }
    }

    override fun nonLinkedPlayersMessage(list: List<ClubMember>): List<TextSource> = buildEntities("") {
        if (list.isEmpty()) {
            +"Все игроки находятся в системе."
            return@buildEntities
        }
        +"Следующие игроки не существуют в нашей системе:"
        list.forEach { player ->
            val playerTag = player.tag.toString().replace("#", "")
            +newLine
            +"\t• " + link(player.name.value, "https://brawlify.com/stats/profile/${playerTag}")
        }
        +newLine
        +newLine
        +"Текст для объявления в клубе:"
        +newLine
        val playersText = list.joinToString(", ") { it.name.value }
        code("$playersText – все в нашем клубе должны находится в клубном чате в телеграм (иначе – исключение), для того чтобы туда попасть, " +
            "напишите пожалуйста телеграм боту $botTag.")
    }

    override val chooseClubMessage: String = "Выберите клуб, чтобы отправить сообщение:"
    override val choosePlayersMessage: String = "Выберите игроков (несколько), которым отправим сообщение:"
    override val continueChoice: String = "Продолжить"
    override val nothingChosenMessage: String = "Ничего не было выбрано, пожалуйста выберите."
    override val sendMessageSuccessMessage: String = "Сообщение было успешно отправлено и будет доставлено в течении нескольких минут."
    override val provideMessageForPlayers: List<TextSource> = buildEntities {
        +"Теперь напишите сообщение для пользователей, сообщение написаное вами будет переслано всем в приватные"
        +" сообщения бота. Можно использовать картинки, видео, что угодно, однако только в рамках одного сообщения."
    }

    override val noMessageError: String = "Вы не написали никакого текста в сообщении, пожалуйста напишите."

    override val showNonLinkedPlayersChoice: String = "Показать игроков вне системы"
    override val sendMessageChoice: String = "Отправить сообщение"

    override val invalidTagFormatOrSizeMessage: String =
        "Тег должен быть длиной ${PlayerTag.REQUIRED_SIZE} (не учитывая '#'). Вы правильно его отформатировали? Пожалуйста, попробуйте снова."

    override val goBackChoice: String = "⬅️ Назад"
    override val invalidChoiceMessage: String = "Нет такого выбора, пожалуйста, выберите из предложенных вариантов:"

    override fun notInTheClubMessage(states: List<ClubJoinAbility>): List<TextSource> {
        val clubOrClubs = if (states.size == 1) "клубе" else "каком-либо клубе"
        return buildEntities("") {
            +"Не могу найти вас в $clubOrClubs – вы действительно в нем и правильно все ввели?"
            guestWantToJoinClubMessage(states)
        }
    }

    override val applyForClubChoice: String = "\uD83D\uDCDD Подать заявку в доступные клубы"
    override val acceptRulesChoice: String = "✅ Принять правила"

    override fun clubRules(value: String): List<TextSource> = buildEntities {
        +bold("Основные правила клуба") + ": \n$value"
    }

    override fun chatRules(value: String): List<TextSource> = buildEntities {
        +bold("Основные правила чата") + ": \n$value"
    }

    override val youAreInMemberMenuMessage: String =
        "Вы в меню участника! Пожалуйста, выберите, что хотите сделать сегодня:"
    override val youAreInAdminMenuMessage: String = "Ты в админ меню, вот что можно делать:"

    override fun youAreRegisteredButNotInChatMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> = buildEntities {
        +"Похоже, ${player.name.value}, вы уже в системе, но не в чате. Давайте перейдем к правилам, и как только все будет готово, я предоставлю вам ссылку."
    }

    override fun wantToJoinClubSuccessMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player, club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View): List<TextSource> {
        val clubTag = club.tag.toString().replace("#", "")
        return buildEntities {
            +"\uD83C\uDF7E Нашел тебя в нашем клубе «"
            link(club.name.value, "https://brawlify.com/stats/club/$clubTag")
            +"»! \n\nПерейдём к правилам нашего клуба, после чего я добавлю тебя в наш чат."
        }
    }

    override val commonWantJoinChatStateSuccessMessage: String =
        "Все вроде в порядке! Давайте перейдем к правилам, и как только все будет готово, я дам вам ссылку на наш чат."

    override val joinClubChatMessage: List<TextSource> = buildEntities {
        +"Спасибо за принятие правил! Осталось только нажать кнопку ниже чтобы подать заявку в чат, которую я приму в автоматическом порядке:"
    }

    override val joinChatButton: String = "\uD83D\uDD17 Присоединиться к чату"

    override fun leftClub(player: com.y9vad9.starix.core.brawlstars.entity.player.Player, club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club, clubsLeft: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>?): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities {
            link(player.name.value, "https://brawlify.com/stats/profile/$playerTagWithoutHashTag") + " покинул(а) ${club.name}"
            if (clubsLeft.isNullOrEmpty()) +" и скоро будет исключен(а)."
            else +", но остался(ась) в других клубах, связанных с этим чатом."
        }
    }

    override fun acceptedToTheClubChat(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities("") {
            +"Поприветствуйте " + link(player.name.value, "https://brawlify.com/stats/profile/$playerTagWithoutHashTag") + " в нашем чате! "
            +bold("\n\nЕго/ее статистика:\n")
            +"\t• 🏆 Общие трофеи: ${player.trophies.value} (макс: ${player.highestTrophies.value})\n"
            +"\t• \uD83C\uDF1F Победы в Три-Против-Трех: ${player.threeVsThreeVictories.value}\n"
            +"\t• \uD83E\uDD1D Победы в Дуо: ${player.duoVictories.value}\n"
            +"\t• ⚔\uFE0F Победы в Соло: ${player.soloVictories.value}\n"
            +"\nЧувствуй себя как дома!"
        }
    }

    override fun leftClubChatMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities("") {
            link(player.name.value, "https://brawlify.com/stats/profile/$playerTagWithoutHashTag")
            +" покинул клубный чат. \uD83D\uDE41"
        }
    }

    override val enableNotificationsMessage: List<TextSource> = buildEntities {
        +bold("Не забудь включить на мне уведомления") +": я буду автоматически уведомлять тебя в случае твоей неактивности и о многом другом!"
        +newLine
        +newLine
        +"Чтобы сделать это зайди на мой профиль → три точки → уведомления → включить."
    }

    override fun failureToMessage(throwable: Throwable): String {
        return "К сожалению, произошла неизвестная ошибка. Пожалуйста, попробуйте позже или перезапустите бота с помощью /start."
    }
}
