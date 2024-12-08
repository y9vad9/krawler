package com.y9vad9.gamerslegion

import com.y9vad9.gamerslegion.brawlstars.BrawlStarsClient
import com.y9vad9.gamerslegion.database.MembersRepository
import com.y9vad9.gamerslegion.telegram.state.FSMState
import com.y9vad9.gamerslegion.usecase.*
import dev.inmo.tgbotapi.extensions.api.chat.members.promoteChatAdministrator
import dev.inmo.tgbotapi.extensions.api.chat.members.setChatAdministratorCustomTitle
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitAnyContent
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitTextedContent
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndFSMAndStartLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.LinkPreviewOptions
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.inline.urlInlineButton
import dev.inmo.tgbotapi.types.chat.member.LeftChatMember
import dev.inmo.tgbotapi.types.chat.member.MemberChatMember
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.CommonChatMemberUpdatedUpdate
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database

fun main(): Unit = runBlocking {
    val bsClient = BrawlStarsClient(
        HttpClient(CIO) {
            defaultRequest {
                url("https://api.brawlstars.com/v1")
                accept(ContentType.Application.Json)

                bearerAuth("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6Ijk3NDQ5YTBjLTNmOTMtNDcyZC1iNjI4LTk1YjczNWI5OGI1ZiIsImlhdCI6MTczMzQzNDU0OCwic3ViIjoiZGV2ZWxvcGVyLzUyYTFlZThkLWY5YWItNzBjNC1kYjQ5LTM0Y2EzZmQxN2IyYiIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMTc2LjQuMTg3LjE4MiJdLCJ0eXBlIjoiY2xpZW50In1dfQ.iSIr0I8iMRShzDn_kHbX9ML005m5xjfiJH5Wh08zvXTCyr5jpK9lZUgp2f7k6KfETWjyJHyLqgjgfEFhLGmpPQ")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    )

    val db = Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;")

    val membersRepo = MembersRepository(db)
    val addMemberUseCase = AddMemberUseCase(bsClient, membersRepo)
    val checkUserStatus = CheckUserStatusUseCase(membersRepo)
    val checkMembersAvailability = CheckMembersAvailabilityUseCase(bsClient, membersRepo)
    val getMemberById = GetMemberByIdUseCase(membersRepo)
    val getBsStats = GetBSStatsUseCase(bsClient)

    val chatInviteLink = System.getenv("CHAT_INVITE_LINK")

    telegramBotWithBehaviourAndFSMAndStartLongPolling<FSMState>(
        token = System.getenv("BOT_TOKEN"),
        onStateHandlingErrorHandler = { state, e ->
            e.printStackTrace()
            null
        },
    ) {
        onCommand("start") {
            return@onCommand when (val result = checkUserStatus.execute(it.chat.id.chatId.long)) {
                is CheckUserStatusUseCase.Result.Admin -> {
                    bot.send(it.chat.id, Messages.adminPanelMessage(result.member))
                    startChain(FSMState.Admin.Initial(it.chat.id))
                }

                is CheckUserStatusUseCase.Result.Member -> {
                    bot.send(
                        chatId = it.chat.id,
                        entities = Messages.userPanelMessage(result.member),
                        replyMarkup = replyKeyboard {
                            add(
                                listOf(
                                    SimpleKeyboardButton(Messages.rateMe()),
                                    SimpleKeyboardButton(Messages.readRules())
                                )
                            )
                        }
                    )
                    startChain(FSMState.Member.Initial(it.chat.id))
                }

                CheckUserStatusUseCase.Result.New -> {
                    bot.send(
                        it.chat.id,
                        Messages.HELLO_MESSAGE,
                        replyMarkup = replyKeyboard(oneTimeKeyboard = true) {
                            add(listOf(SimpleKeyboardButton(Messages.alreadyInClubVariant())))
                            add(listOf(SimpleKeyboardButton(Messages.willJoinClubVariant())))
                        }
                    )
                    startChain(FSMState.Initial(it.chat.id))
                }
            }
        }

        strictlyOn<FSMState.Initial> {
            val message = waitTextedContent().first().text

            return@strictlyOn when (message) {
                Messages.alreadyInClubVariant() -> {
                    bot.send(
                        it.context,
                        Messages.I_WANT_TO_JOIN_ANSWER,
                        replyMarkup = inlineKeyboard {
                            add(
                                listOf(
                                    urlInlineButton(
                                        "Где найти свой игровой тэг?",
                                        "https://support.supercell.com/brawl-stars/ru/articles/player-tag.html"
                                    )
                                )
                            )
                        }
                    )
                    FSMState.ProvidePlayerTag(it.context)
                }

                Messages.willJoinClubVariant() -> {
                    when (val result = checkMembersAvailability.execute()) {
                        is CheckMembersAvailabilityUseCase.Result.ApiFailure -> {
                            result.exception.printStackTrace()
                            bot.send(it.context, "Произошла ошибка, попробуйте пожалуйста позже.")
                        }

                        is CheckMembersAvailabilityUseCase.Result.CanJoin -> {
                            bot.send(it.context, Messages.canJoinMessage(result.availableSeats))
                        }

                        CheckMembersAvailabilityUseCase.Result.CannotJoin -> {
                            bot.send(
                                it.context,
                                Messages.cannotJoinMessage(),
                                linkPreviewOptions = LinkPreviewOptions.Disabled,
                                replyMarkup = inlineKeyboard {
                                    add(
                                        listOf(
                                            urlInlineButton(
                                                "Связаться с президентом клуба",
                                                "https://t.me/RRE1EYY"
                                            )
                                        )
                                    )
                                })
                        }
                    }
                    bot.send(it.context, Messages.instructionToRestart())
                    null
                }

                else -> {
                    bot.send(
                        it.context,
                        "Такого варианта нет, пожалуйста выберите из возможных:",
                        replyMarkup = replyKeyboard(oneTimeKeyboard = true) {
                            add(listOf(SimpleKeyboardButton(Messages.alreadyInClubVariant())))
                            add(listOf(SimpleKeyboardButton(Messages.willJoinClubVariant())))
                        }
                    )
                    it
                }
            }
        }

        strictlyOn<FSMState.ProvidePlayerTag> {
            val reply = waitAnyContent().first { content ->
                val condition = content is TextContent
                if (!condition)
                    bot.send(it.context, "Отправьте пожалуйста только ваш тэг :)")
                condition
            } as TextContent

            when (val result = addMemberUseCase.execute(it.context.chatId.long, reply.text)) {
                AddMemberUseCase.Result.AlreadyIn -> {
                    bot.send(
                        it.context,
                        "Данный игрок уже находится в клубном чате; поменяли телеграм аккаунт? Свяжитесь с @y9vad9"
                    )
                    it
                }

                is AddMemberUseCase.Result.ApiFailure -> {
                    result.exception.printStackTrace()
                    bot.send(
                        it.context,
                        "Произошла ошибка на стороне Brawl Stars: попробуйте пожалуйста позже или свяжитесь с @y9vad9."
                    )
                    it
                }

                AddMemberUseCase.Result.InvalidTag -> {
                    bot.send(it.context, "Вы ввели тэг несуществующего игрока или отправили в неверном формате.")
                    it
                }

                AddMemberUseCase.Result.NotInTheClub.CannotJoin -> {
                    bot.send(
                        it.context,
                        Messages.youAreNotInClubButCannotJoinMessage(),
                        linkPreviewOptions = LinkPreviewOptions.Disabled,
                        replyMarkup = inlineKeyboard {
                            add(listOf(urlInlineButton("Связаться с президентом клуба", "https://t.me/RRE1EYY")))
                        }
                    )
                    it
                }

                is AddMemberUseCase.Result.NotInTheClub.ButCanJoin -> {
                    bot.send(
                        it.context,
                        Messages.youAreNotInClubButCanJoinMessage(result.availableSeats),
                        linkPreviewOptions = LinkPreviewOptions.Disabled,
                        replyMarkup = inlineKeyboard {
                            add(listOf(urlInlineButton("Связаться с президентом клуба", "https://t.me/RRE1EYY")))
                        }
                    )
                    it
                }

                is AddMemberUseCase.Result.Success -> {
                    bot.send(it.context, Messages.validTagMessage(result.member))
                    bot.send(
                        chatId = it.context,
                        entities = Messages.CLUB_RULES_MESSAGE,
                        replyMarkup = replyKeyboard(oneTimeKeyboard = true) {
                            add(listOf(SimpleKeyboardButton(Messages.ACCEPT_RULES)))
                        }
                    )

                    FSMState.ShouldAcceptClubRules(it.context)
                }
            }
        }

        strictlyOn<FSMState.ShouldAcceptClubRules> {
            waitTextedContent().filter { content -> content.text == Messages.ACCEPT_RULES }.first()
            bot.send(
                chatId = it.context,
                entities = Messages.CHAT_RULES_MESSAGE,
                replyMarkup = replyKeyboard(oneTimeKeyboard = true) {
                    add(listOf(SimpleKeyboardButton(Messages.ACCEPT_RULES)))
                }
            )

            FSMState.ShouldAcceptChatRules(it.context)
        }

        strictlyOn<FSMState.ShouldAcceptChatRules> {
            waitTextedContent().filter { content -> content.text == Messages.ACCEPT_RULES }.first()

            bot.send(
                chatId = it.context,
                entities = Messages.invitationLink,
                replyMarkup = inlineKeyboard {
                    add(listOf(urlInlineButton("➡\uFE0F Вступить в чат", chatInviteLink)))
                },
                protectContent = true,
            )

            bot.send(
                chatId = it.context,
                entities = Messages.afterJoinMessage(),
                replyMarkup = replyKeyboard {
                    add(listOf(SimpleKeyboardButton(Messages.rateMe()), SimpleKeyboardButton(Messages.readRules())))
                }
            )

            FSMState.Member.Initial(it.context)
        }

        strictlyOn<FSMState.Member.Initial> {
            val content = waitTextedContent()
                .first().text

            when (content) {
                Messages.rateMe() -> bot.send(it.context, "Пока не поддерживается")
                Messages.readRules() -> bot.send(it.context, Messages.fullRules())
            }

            it
        }

        strictlyOn<FSMState.Admin.Initial> {
            val content = waitTextedContent()
                .first().text.orEmpty()

            when {
                content == Messages.rateMe() -> bot.send(it.context, "Пока не поддерживается")
                content == Messages.readRules() -> bot.send(it.context, Messages.fullRules())
                content.startsWith("/notify") -> {

                }
            }

            it
        }

        launch {
            allUpdatesFlow.onEach { update -> println(update) }.filterIsInstance<CommonChatMemberUpdatedUpdate>()
                .filter { event ->
                    event.data.user.id.chatId == event.data.newChatMemberState.user.id.chatId }
                .filter { event ->
                    event.data.oldChatMemberState is LeftChatMember &&
                        event.data.newChatMemberState is MemberChatMember
                }
                .collect { event ->
                    when (val result = getMemberById.execute(event.data.user.id.chatId.long)) {
                        GetMemberByIdUseCase.Result.NotFound -> {
                            bot.send(
                                event.data.chat,
                                "Данного пользователя нет в системе, если это игрок – он должен войти через данного бота."
                            )
                        }

                        is GetMemberByIdUseCase.Result.Success -> {
                            val userId = UserId(RawChatId(result.member.tgId))
                            bot.promoteChatAdministrator(
                                chatId = event.data.chat.id,
                                userId = userId,
                                isAnonymous = false,
                                canDeleteMessages = false,
                                canChangeInfo = false,
                                canManageChat = false,
                                canInviteUsers = false,
                                canPromoteMembers = false,
                                canRestrictMembers = false,
                                canManageVideoChats = false,
                            )
                            bot.setChatAdministratorCustomTitle(event.data.chat.id, userId, result.member.name)
                            bot.send(
                                event.data.chat,
                                Messages.userJoinedChatMessage(result.member, getBsStats.execute(result.member.tag))
                            )
                        }
                    }
                }
        }
    }.second.join()
}