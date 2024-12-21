@file:Suppress("DuplicatedCode")

package com.y9vad9.starix.core.system.usecase.settings.admin.club

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.common.entity.value.CustomMessage
import com.y9vad9.starix.core.system.entity.LocalizableEntity
import com.y9vad9.starix.core.system.entity.isAdminIn
import com.y9vad9.starix.core.system.entity.isClubAllowed
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId

class ChangeChatRulesSettingUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(
        id: TelegramUserId,
        clubTag: ClubTag,
        message: CustomMessage,
        languageCode: LanguageCode? = null,
    ): Result {
        if (message.value.isBlank())
            return Result.ShouldNotBeEmpty
        val settings = settingsRepository.getSettings()
        if (!settings.isAdminIn(clubTag, id))
            return Result.NoPermission

        if (!settings.isClubAllowed(clubTag))
            return Result.ClubNotFound

        val clubSettings = settings.allowedClubs[clubTag]!!

        val language = languageCode ?: clubSettings.defaultLanguage

        val rules = clubSettings.chatRules.asMap().toMutableMap().apply {
            put(language, message)
        }.let { LocalizableEntity(it) }

        settingsRepository.setSettings(
            settings.copy(
                allowedClubs = settings.allowedClubs.toMutableMap().apply {
                    put(clubTag, settings.allowedClubs[clubTag]!!.copy(chatRules = rules))
                }
            )
        )

        return Result.Success
    }

    sealed interface Result {
        data object ClubNotFound : Result
        data object NoPermission : Result
        data object ShouldNotBeEmpty : Result
        data object Success : Result
    }
}