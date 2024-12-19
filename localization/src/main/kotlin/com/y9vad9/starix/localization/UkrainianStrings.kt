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
import dev.inmo.tgbotapi.utils.code
import dev.inmo.tgbotapi.utils.link
import dev.inmo.tgbotapi.utils.newLine
import kotlin.collections.forEach

data class UkrainianStrings(val botTag: String) : Strings {
    override fun guestStartMessage(includedClubs: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>): List<TextSource> {
        return if (includedClubs.size == 1) {
            val club = includedClubs.first()
            val tagWithoutHashTag = club.tag.toString().replace("#", "")
            buildEntities("") {
                +"Ласкаво просимо до чат-бота клубу "
                link("«${club.name.value}»", "https://brawlify.com/stats/club/$tagWithoutHashTag")

                +newLine
                +newLine

                +"Я буду супроводжувати вас увесь час, поки ви в нашому клубі – нагадувати про події, "
                +"слідкувати за вашою активністю і багато чого іншого!"

                +newLine
                +newLine


                +"Чим я можу вам допомогти?"
            }
        } else {
            buildEntities("") {
                +"Ласкаво просимо до чат-бота для автоматизації клубів Brawl Stars. "
                +"Цей бот відповідає за наступні клуби:"
                includedClubs.forEach { club ->
                    val tagWithoutHashTag = club.tag.toString().replace("#", "")
                    link(club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                }

                +newLine
                +newLine

                +"Я буду супроводжувати вас увесь час, поки ви в нашому клубі – нагадувати про події, "
                +"слідкувати за вашою активністю і багато чого іншого!"

                +newLine
                +newLine

                +"Чим я можу вам допомогти?"
            }
        }
    }

    override val viewGitHubRepositoryChoice: String = "\uD83D\uDCC2 Переглянути вихідний код"
    override val hereToLinkChoice: String = "\uD83D\uDCAC Я тут, щоб приєднатися до чату"
    override val herePlanToJoinChoice: String = "\uD83D\uDC40 Я тільки планую приєднатися до клубу"
    override val viewContactPersonsChoice: String = "☎\uFE0F Контакти"

    override fun guestWantToJoinClubMessage(states: List<ClubJoinAbility>): List<TextSource> {
        return buildEntities("") {
            if (states.size == 1) {
                when (val ability = states.first()) {
                    is ClubJoinAbility.NotAvailable -> +"Наш клуб наразі не приймає нових учасників. Але ми сповістимо вас, коли з'явиться вільне місце!"
                    is ClubJoinAbility.NotEnoughTrophies -> {
                        val amount = ability.club.requiredTrophies.value - ability.playerTrophies.value
                        +"Здається, у вас недостатньо трофеїв для вступу в наш клуб. Вам потрібно ще $amount трофеїв. Як тільки ви їх отримаєте, спробуйте знову!"
                        link("\uD83C\uDFB5 Не здавайтеся! Я чекаю на вас.", "https://www.youtube.com/watch?v=1D7FF5kFyWw")
                    }
                    is ClubJoinAbility.OnlyInvite -> +"На жаль, наш клуб доступний лише за запрошенням, тому зараз ви не можете вступити. Якщо щось зміниться, я сповістю вас якнайшвидше."
                    is ClubJoinAbility.Open -> {
                        val availableSeats = 30 - ability.club.members.size
                        +"Наш клуб відкритий і має $availableSeats вільних місць для вас!"
                    }
                    is ClubJoinAbility.UponRequest -> +"Якщо ви все правильно ввели і хочете приєднатися до нашого клубу, ви можете подати заявку прямо в цьому боті. Що ви з цього приводу думаєте?"
                }
            } else {
                val trophiesUnmet = states.filterIsInstance<ClubJoinAbility.NotEnoughTrophies>()
                val openClubs = states.filterIsInstance<ClubJoinAbility.Open>()
                val notAvailable = states.filterIsInstance<ClubJoinAbility.NotAvailable>()
                val inviteOnly = states.filterIsInstance<ClubJoinAbility.OnlyInvite>()
                val uponRequest = states.filterIsInstance<ClubJoinAbility.UponRequest>()

                +"Ось список клубів, в які ви можете/не можете вступити:"

                if (trophiesUnmet.isNotEmpty()) {
                    +"• Недостатньо трофеїв"
                    trophiesUnmet.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                        +"– потрібно ще ${ability.club.requiredTrophies.value - ability.playerTrophies.value} трофеїв"
                    }
                    +"Ви можете вступити в ці клуби, коли наберете достатньо трофеїв."
                }

                if (openClubs.isNotEmpty()) {
                    +"• Відкриті клуби"
                    openClubs.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                        +"– ви можете вступити"
                    }
                }

                if (notAvailable.isNotEmpty()) {
                    +"• Закриті клуби"
                    notAvailable.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                    }
                    +"Ви не можете вступити в ці клуби зараз."
                }

                if (inviteOnly.isNotEmpty()) {
                    +"• Лише за запрошенням"
                    inviteOnly.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                    }
                    +"Ви не можете вступити в ці клуби без особистого запрошення."
                }

                if (uponRequest.isNotEmpty()) {
                    +"• Відкриті для заявок"
                    uponRequest.forEach { ability ->
                        val tagWithoutHashTag = ability.club.tag.toString().replace("#", "")
                        link(ability.club.name.value, "https://brawlify.com/stats/club/$tagWithoutHashTag")
                    }
                    +"Ви можете вступити в ці клуби, подавши заявку через кнопку нижче."
                }
            }
        }
    }

    override fun guestShowContactsMessage(settings: Settings): List<TextSource> {
        TODO("Не реалізовано")
    }

    override val gitHubSourcesMessage: List<TextSource> = buildEntities("") {
        +"Цей бот з відкритим кодом зберігається на GitHub. Ви можете дослідити код "
        link("тут", "https://github.com/y9vad9/bcm") + "."
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

    override val chooseClubMessage: String = "Оберіть клуб, до якого будемо відправляти повідомлення:"
    override val choosePlayersMessage: String = "Оберіть гравців (несколько), яким відправим повідомлення:"
    override val continueChoice: String = "Продовжити"
    override val nothingChosenMessage: String = "Нічого не обрано, будь-ласка оберіть."
    override val sendMessageSuccessMessage: String = "Повідомлення було успішно відправлено."
    override val provideMessageForPlayers: List<TextSource> = buildEntities {
        +"Тепер напишіть повідомлення для користувачів, повідомлення написане вами буде надіслано всім у приватні"
        +" повідомлення бота. Можна використовувати зображення, відео, що завгодно, але тільки в рамках одного повідомлення."
    }

    override val noMessageError: String = "Вы не написали никакого текста в сообщении, пожалуйста напишите."

    override val showNonLinkedPlayersChoice: String = "Показать игроков вне системы"
    override val sendMessageChoice: String = "Отправить сообщение"


    override val letsLinkBsMessage: List<TextSource> = buildEntities("") {
        +"Перед тим як продовжити, давайте прив'яжемо ваш акаунт Brawl Stars до вашого акаунта Telegram."
        +newLine
        +newLine
        +"Щоб знайти ваш тег, відкрийте ваш профіль у ігровому меню (кнопка в лівому верхньому куті екрану). Ігровий тег знаходиться під вашою фотографією профілю."
        +" Ігровий тег для прикладу виглядає наступним чином: " + code("#ABCD12345") +"."
    }

    override val playerAlreadyLinkedBySomeoneMessage: String =
        "Цей акаунт вже прив'язаний до іншого акаунта Telegram. Ви впевнені, що це ваш акаунт?"

    override val youAreAlreadyInTheChat: String = "Ти вже є в нашому чаті. \uD83D\uDE09"


    override val playerTagNotFoundMessage: String =
        "Вказаний ігровий тег недійсний: такого гравця не існує. Будь ласка, спробуйте знову."
    override val allPlayersChoice: String = "Всі гравці"
    override val toGroupChoice: String = "До групи"

    override fun successfullyLinkedBsMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        return buildEntities("") {
            +"Приємно познайомитись, ${player.name.value}! Ваш акаунт Brawl Stars успішно прив'язаний."
        }
    }

    override fun wantToJoinClubSuccessMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player, club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View): List<TextSource> {
        val clubTag = club.tag.toString().replace("#", "")
        return buildEntities {
            +"\uD83C\uDF7E Знайшов тебе в нашому клубові «"
            link(club.name.value, "https://brawlify.com/stats/club/$clubTag")
            +"»! Перейдім до правил, після чого я додам тебе до нашого чату."
        }
    }

    override val invalidTagFormatOrSizeMessage: String =
        "Тег має бути довжиною ${PlayerTag.REQUIRED_SIZE}, без '#'. Ви правильно його відформатували? Будь ласка, спробуйте знову."

    override val goBackChoice: String = "⬅️ Назад"
    override val invalidChoiceMessage: String = "Немає такого вибору, будь ласка, виберіть із запропонованих варіантів:"

    override fun notInTheClubMessage(states: List<ClubJoinAbility>): List<TextSource> {
        val clubOrClubs = if (states.size == 1) "клубі" else "якомусь клубі"
        return buildEntities("") {
            +"Не можу знайти вас в $clubOrClubs – ви дійсно в ньому і правильно все ввели?"
            guestWantToJoinClubMessage(states)
        }
    }

    override val applyForClubChoice: String = "\uD83D\uDCDD Подати заявку в доступні клуби"
    override val acceptRulesChoice: String = "✅ Прийняти правила"

    override fun clubRules(value: String): List<TextSource> = buildEntities {
        bold("Основні правила клубу") + ": \n$value"
    }

    override fun chatRules(value: String): List<TextSource> = buildEntities {
        bold("Основні правила чату") + ": \n$value"
    }

    override val youAreInMemberMenuMessage: String =
        "Ви в меню учасника! Будь ласка, виберіть, що хочете зробити сьогодні:"
    override val youAreInAdminMenuMessage: String = "Ти в адмін-меню, обери що хочеш робити:"

    override fun youAreRegisteredButNotInChatMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> = buildEntities {
        +"Здається, ${player.name}, ви вже в системі, але не в чаті. Давайте перейдемо до правил, і як тільки все буде готово, я надам вам посилання."
    }

    override val commonWantJoinChatStateSuccessMessage: String =
        "Все начебто в порядку! Давайте перейдемо до правил, і як тільки все буде готово, я дам вам посилання на наш чат."

    override val joinClubChatMessage: List<TextSource> = buildEntities {
        +"Дякую за прийняття наших правил. Все, що залишилось – це натиснути кнопку під цим повідомленням."
        +" Як тільки я отримаю твою заявку на приєднання – я автоматично прийму її."
    }

    override val joinChatButton: String = "\uD83D\uDD17 Приєднатись до чату"

    override fun leftClub(player: com.y9vad9.starix.core.brawlstars.entity.player.Player, club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club, clubsLeft: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>?): List<TextSource> {
        val clubTagWithoutHashTag = club.tag.toString().replace("#", "")
        return buildEntities {
            link("${player.name}", "https://brawlify.com/stats/profile/") + " покинув(ла) ${club.name}"
            if (clubsLeft.isNullOrEmpty()) +" і скоро буде виключений(а)."
            else +", але залишився(лася) в інших клубах, пов'язаних з цим чатом."
        }
    }

    override fun acceptedToTheClubChat(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities("") {
            +"Увага, всі! Привітайте ${player.name} у нашому чаті! "
            +"\n\nЙого/її статистика:"
            +"\t• Загальні трофеї: ${player.trophies.value} (макс: ${player.highestTrophies.value})"
            +"\t• Перемоги в Три-Проти-Трех: ${player.threeVsThreeVictories.value}"
            +"\t• Перемоги в Дуо: ${player.duoVictories.value}"
            +"\t• Перемоги в Соло: ${player.soloVictories.value}"
            +"\n\nПочувайте себе як вдома."
        }
    }

    override fun leftClubChatMessage(player: com.y9vad9.starix.core.brawlstars.entity.player.Player): List<TextSource> {
        val playerTagWithoutHashTag = player.tag.toString().replace("#", "")
        return buildEntities("") {
            link(player.name.value, "https://brawlify.com/stats/profile/$playerTagWithoutHashTag")
            +" покинув наш чат. \uD83D\uDE41"
        }
    }

    override val enableNotificationsMessage: List<TextSource> = buildEntities {
        +"Не забудь включити на мені сповіщення: я автоматично повідомлятиму тебе у разі твоєї неактивності і про багато іншого!"
        +newLine
        +newLine
        +"Щоб зробити це, відкрий мій профіль → три точки → сповіщення → увімкнути."
    }

    override fun failureToMessage(throwable: Throwable): String {
        return "На жаль, сталася невідома помилка. Будь ласка, спробуйте пізніше або перезапустіть бота за допомогою /start."
    }
}
