package com.y9vad9.krawler.user.application.repository

import com.y9vad9.krawler.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.krawler.user.domain.entity.LinkedTelegram
import com.y9vad9.krawler.user.domain.User
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.krawler.user.domain.value.LinkedTelegramChatId
import com.y9vad9.krawler.user.domain.value.LinkedTelegramUserName
import com.y9vad9.krawler.user.domain.value.UserId

/**
 * Repository interface for managing user-related persistence and retrieval.
 *
 * Provides operations to:
 * - Look up a user by their associated [LinkedTelegramChatId].
 * - Create a new user bound to a [LinkedTelegramChatId] with a specified [LinkedTelegramUserName].
 * - Apply updates to an existing [User] through granular, intent-driven operations.
 *
 * Implementations of this interface are responsible for handling the underlying
 * data storage mechanisms (e.g., databases, in-memory stores, etc.).
 */
interface UserRepository {

    /**
     * Retrieves a user associated with the given [telegramChatId], if any.
     *
     * @return [Result] containing the [User] if found, or `null` if no such user exists.
     * A failure result indicates a retrieval error.
     */
    suspend fun getUserByTelegramChatId(telegramChatId: LinkedTelegramChatId): Result<User?>

    /**
     * Retrieves a user associated with the given system [userId].
     *
     * @return [Result] containing the [User] if found, or `null` if no such user exists.
     * A failure result indicates a retrieval error.
     */
    suspend fun getUserBySystemId(userId: UserId): Result<User?>

    /**
     * Creates a new user and links them to the given [telegramChatId] and [name].
     *
     * @return [Result] containing the created [User], or a failure if creation failed.
     */
    suspend fun createUser(
        telegramChatId: LinkedTelegramChatId,
        name: LinkedTelegramUserName,
    ): Result<User>

    /**
     * Updates the display name of the user with [userId].
     */
    suspend fun updateUserName(
        userId: UserId,
        newName: LinkedTelegramUserName,
    ): Result<Unit>

    /**
     * Updates the Telegram identity (ID and/or name) linked to the user with [userId].
     */
    suspend fun updateLinkedTelegram(
        userId: UserId,
        newTelegram: LinkedTelegram,
    ): Result<Unit>

    /**
     * Adds a new [entity.BrawlStarsPlayer] to the user with [userId].
     */
    suspend fun addLinkedPlayer(
        userId: UserId,
        player: BrawlStarsPlayer,
    ): Result<Unit>

    /**
     * Refreshes an already-linked [BrawlStarsPlayer] for the user with [userId].
     */
    suspend fun refreshLinkedPlayer(
        userId: UserId,
        player: BrawlStarsPlayer,
    ): Result<Unit>

    /**
     * Removes the player with [playerTag] from the user with [userId].
     */
    suspend fun removeLinkedPlayer(
        userId: UserId,
        playerTag: BrawlStarsPlayerTag,
    ): Result<Unit>

    /**
     * Removes all linked players from the user with [userId].
     */
    suspend fun removeAllLinkedPlayers(
        userId: UserId,
    ): Result<Unit>
}
