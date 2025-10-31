package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.EnemyBotsLevelId
import krawler.server.player.application.battle.isAtLeastInsane
import krawler.server.player.application.battle.isAtLeastMaster
import krawler.server.player.application.battle.isAtMostExpert
import krawler.server.player.application.battle.isHard
import krawler.server.player.application.battle.isInsane3
import krawler.server.player.application.battle.isInsaneTier
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnemyBotsLevelIdTest {

    @Test
    fun `GIVEN valid raw values WHEN create THEN returns correct EnemyBotsLevelId`() {
        // GIVEN
        val raw = 3

        // WHEN
        val level = EnemyBotsLevelId.create(raw).getOrThrow()

        // THEN
        assertEquals(EnemyBotsLevelId.MASTER.rawInt, level.rawInt)
    }

    @Test
    fun `GIVEN invalid raw value WHEN create THEN returns failure`() {
        // GIVEN
        val raw = 0

        // WHEN / THEN
        val result = EnemyBotsLevelId.create(raw)
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN invalid raw value WHEN createOrNull THEN returns null`() {
        // GIVEN
        val raw = -1

        // WHEN
        val level = EnemyBotsLevelId.createOrNull(raw)

        // THEN
        assertEquals(null, level)
    }

    @Test
    fun `GIVEN invalid raw value WHEN createOrThrow THEN throws exception`() {
        // GIVEN
        val raw = -10

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            val _ = EnemyBotsLevelId.createOrThrow(raw)
        }
    }

    @Test
    fun `specific level checks work correctly`() {
        // GIVEN
        val hard = EnemyBotsLevelId.HARD
        val insane3 = EnemyBotsLevelId.INSANE_3

        // THEN
        assertTrue(hard.isHard())
        assertFalse(hard.isInsane3())

        assertTrue(insane3.isInsane3())
        assertFalse(insane3.isHard())
    }

    @Test
    fun `tier and range checks work correctly`() {
        // GIVEN
        val expert = EnemyBotsLevelId.EXPERT
        val master = EnemyBotsLevelId.MASTER
        val insane2 = EnemyBotsLevelId.INSANE_2

        // THEN
        assertTrue(expert.isAtMostExpert())
        assertFalse(master.isAtMostExpert())

        assertTrue(master.isAtLeastMaster())
        assertFalse(expert.isAtLeastMaster())

        assertTrue(insane2.isAtLeastInsane())
        assertTrue(insane2.isInsaneTier())
        assertFalse(master.isInsaneTier()) // master is below INSANE
    }

    @Test
    fun `comparison works correctly`() {
        // GIVEN
        val hard = EnemyBotsLevelId.HARD
        val insane = EnemyBotsLevelId.INSANE

        // THEN
        assertTrue(hard < insane)
        assertTrue(insane > hard)
        assertEquals(0, hard.compareTo(EnemyBotsLevelId.HARD))
    }
}
