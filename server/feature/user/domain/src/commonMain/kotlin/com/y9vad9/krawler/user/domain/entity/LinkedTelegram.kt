package com.y9vad9.krawler.user.domain.entity

import com.y9vad9.krawler.user.domain.value.LinkedTelegramChatId
import com.y9vad9.krawler.user.domain.value.LinkedTelegramUserName
import com.y9vad9.valdi.domain.DomainEntity

/**
 * Represents a linked Telegram user in the system.
 *
 * @property id Unique identifier for the linked Telegram user.
 * @property name Display name or username associated with the Telegram user.
 */
@DomainEntity
public class LinkedTelegram(
    @DomainEntity.Id
    public val id: LinkedTelegramChatId,
    public val name: LinkedTelegramUserName,
) {
    /**
     * Returns a copy of this entity with the updated [id].
     */
    public fun withNewId(newId: LinkedTelegramChatId): LinkedTelegram = LinkedTelegram(newId, name)
    /**
     * Returns a copy of this entity with the updated [newName].
     */
    public fun withNewName(newName: LinkedTelegramUserName): LinkedTelegram = LinkedTelegram(id, newName)


    override fun equals(other: Any?): Boolean =
        this === other || (other is LinkedTelegram && id == other.id && name == other.name)
    override fun hashCode(): Int = 31 * id.hashCode() + name.hashCode()
    override fun toString(): String = "LinkedTelegram(id=$id, name=$name)"
}
