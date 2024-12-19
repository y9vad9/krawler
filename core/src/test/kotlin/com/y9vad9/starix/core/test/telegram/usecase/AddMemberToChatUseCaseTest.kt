package com.y9vad9.starix.core.test.telegram.usecase

import com.y9vad9.starix.foundation.time.TimeProvider
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.brawlstars.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.telegram.repository.ChatRepository
import com.y9vad9.bcm.core.telegram.usecase.AddMemberToChatUseCase
import com.y9vad9.bcm.core.system.repository.UserRepository
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import io.mockative.Mockable
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Mockable(UserRepository::class, BrawlStarsRepository::class, SettingsRepository::class, ChatRepository::class, TimeProvider::class, )
@OptIn(ValidationDelicateApi::class)
class AddMemberToChatUseCaseTest {
    private val userRepository: UserRepository = mock()
    private val brawlStarsRepository: BrawlStarsRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val chatRepository: ChatRepository = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val checkClubsUsecase: CheckClubsAvailabilityUseCase = mockk()

    private val useCase: AddMemberToChatUseCase = mockk()

    private val tag = PlayerTag.createUnsafe("#9V8LCUC0G")

    @Test
    fun `UserRepository#getByTag returns exception test`(): Unit = runTest {
        val throwable = mockk<Throwable>()
        coEvery { userRepository.getByTag(any()) } returns Result.success(mockk())

        assertEquals(
            expected = AddMemberToChatUseCase.Result.Failure(throwable),
            actual = useCase.execute(tag, TelegramUserId.createUnsafe(mockk()))
        )
    }
}