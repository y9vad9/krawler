package com.y9vad9.starix.data

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.data.database.StatesTable
import dev.inmo.micro_utils.fsm.common.managers.DefaultStatesManagerRepo
import dev.inmo.tgbotapi.types.IdChatIdentifier
import io.github.reactivecircus.cache4k.Cache
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes

class StatesManagerRepoImpl(
    private val statesTable: StatesTable,
    private val json: Json,
) : DefaultStatesManagerRepo<FSMState<*>> {
    private val cache = Cache.Builder<IdChatIdentifier, FSMState<*>>()
        .expireAfterAccess(10.minutes)
        .maximumCacheSize(200)
        .build()

    override suspend fun set(state: FSMState<*>) {
        cache.put(state.context, state)
    }

    override suspend fun getContextState(context: Any): FSMState<*>? {
        return try {
            context as IdChatIdentifier
            cache.get(context) {
                json.decodeFromString(statesTable.getJson(context.chatId.long)!!)
            }
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getStates(): List<FSMState<*>> {
        return cache.asMap().values.toList()
    }

    override suspend fun removeState(state: FSMState<*>) {
        statesTable.remove(state.context.chatId.long)
        cache.invalidate(state.context)
    }
}