package com.y9vad9.starix.core.system.repository

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.system.entity.User
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId

/**
 * The repository that is responsible for retrieving information from both Brawl Stars,
 * local storage and Telegram.
 */
interface UserRepository {
    /**
     * Gets [User] by linked telegram id. Automatically creates the account
     * in the system if does not exist.
     *
     * @param id Telegram Chat ID associated with [User].
     * @return [User] if there's any associated with [id].
     */
    suspend fun getById(id: TelegramUserId): Result<User>

    /**
     * Returns whether player is in the system.
     */
    suspend fun isInSystem(id : TelegramUserId): Result<Boolean>

    /**
     * Returns whether player is in the system.
     */
    suspend fun isInSystem(tag: PlayerTag): Result<Boolean>

    /**
     * Gets [User] by linked brawl stars player's tag. Unlike [getById],
     * this method does not create the account if it does not exist.
     *
     * @param tag Brawl Stars player's tag associated with [User].
     * @return [User] if there's any associated with [id].
     */
    suspend fun getByTag(tag: PlayerTag): Result<User?>

    /**
     * Gets the list of current club members in Brawl Stars.
     *
     * @param tag Club's tag from which it will pull.
     *
     * @return [List] of [User]s within the club, if exists.
     */
    suspend fun getFromClub(tag: ClubTag): List<User>?

    /**
     * Associates the Brawl Stars account with the Telegram.
     *
     * @param tag Brawl Stars player's tag associated with [User].
     * @param account Account that will be associated with [User]
     *
     * @return [User] or null, if a player or telegram account does not exist.
     */
    suspend fun link(tag: PlayerTag, id: TelegramUserId, time: UnixTime): Result<User?>

    /**
     * Unlinks player in the system.
     *
     * @param tag Brawl Stars player's tag associated with User.
     */
    suspend fun unlink(tag: PlayerTag): Result<Unit>
//    /**
//     * Associates the Telegram account with the Brawl Stars.
//     *
//     * @param id Telegram's id associated with [User].
//     * @param account Account that will be associated with [User]
//     *
//     * @return [User] or null, if a player or telegram account does not exist.
//     */
//    suspend fun linkBrawlStars(id: TelegramUserId, account: BrawlStarsPlayer): User?
}