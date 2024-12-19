package com.y9vad9.starix.data

import com.y9vad9.starix.foundation.time.TimeProvider
import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.telegram.entity.LinkedTelegramAccount
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.system.entity.User
import com.y9vad9.bcm.core.system.entity.value.MemberDisplayName
import com.y9vad9.bcm.core.system.repository.UserRepository
import com.y9vad9.bcm.data.database.UserBSAccountsTable
import com.y9vad9.bcm.data.database.UsersTable
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import kotlin.uuid.Uuid

class UserRepositoryImpl(
    private val usersTable: UsersTable,
    private val userBsAccounts: UserBSAccountsTable,
    private val bsRepository: BrawlStarsRepository,
    private val timeProvider: TimeProvider,
) : UserRepository {

    @OptIn(ValidationDelicateApi::class)
    override suspend fun getById(id: TelegramUserId): Result<User> = runCatching {
        val user = usersTable.get(id.value) ?: return run {
            usersTable.createIfNotExists(
                id = Uuid.random(),
                tgId = id.value,
                displayName = null,
                creationTime = timeProvider.provide().inMilliseconds,
            )
            getById(id)
        }

        // TODO: make the database cache in case of API errors to avoid 'missing accounts' problem
        val bsAccounts = userBsAccounts.getListOfPlayerTags(user.id)
            .mapNotNull { bsRepository.getPlayer(PlayerTag.createUnsafe(it)).getOrThrow() }

        return@runCatching User(
            uuid = user.id,
            displayName = user.displayName?.let { MemberDisplayName.createUnsafe(it) },
            telegramAccount = LinkedTelegramAccount(id),
            bsPlayers = bsAccounts,
        )
    }

    override suspend fun isInSystem(id: TelegramUserId): Result<Boolean> = runCatching {
        usersTable.get(id.value) != null
    }

    override suspend fun isInSystem(tag: PlayerTag): Result<Boolean> = runCatching {
        userBsAccounts.getPlayerByTag(tag.toString()) != null
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun getByTag(tag: PlayerTag): Result<User?> = runCatching {
        val personUuid = userBsAccounts.getPlayerByTag(tag.toString())
            ?: return@runCatching null

        // TODO: make the database cache in case of API errors to avoid 'missing accounts' problem
        val bsAccounts = userBsAccounts.getListOfPlayerTags(personUuid)
            .mapNotNull { bsRepository.getPlayer(PlayerTag.createUnsafe(it)).getOrThrow() }

        val userEntity = usersTable.get(personUuid) ?: return@runCatching null

        return@runCatching User(
            uuid = userEntity.id,
            displayName = userEntity.displayName?.let { MemberDisplayName.createUnsafe(it) },
            telegramAccount = LinkedTelegramAccount(TelegramUserId.createUnsafe(userEntity.telegramId)),
            bsPlayers = bsAccounts,
        )
    }

    override suspend fun getFromClub(tag: ClubTag): List<User>? {
        return bsRepository.getClub(tag).getOrNull()?.members?.mapNotNull { member ->
            getByTag(member.tag).getOrNull()
        }
    }

    override suspend fun link(
        tag: PlayerTag,
        id: TelegramUserId,
        time: UnixTime,
    ): Result<User?> = runCatching {
        val player = bsRepository.getPlayer(tag).getOrThrow() ?: return@runCatching null
        val user = getById(id).getOrThrow()

        userBsAccounts.removeLinkageIfExists(tag.toString())
        userBsAccounts.create(user.uuid, tag.toString(), time.inMilliseconds)

        return@runCatching user.copy(
            bsPlayers = user.bsPlayers.orEmpty().toMutableList().apply {
                add(player)
            }.toList()
        )
    }
}