package krawler.server.auth.application

import krawler.server.auth.domain.ChallengedBrawlStarsPlayerTag
import krawler.server.auth.domain.OwnershipTaskBattle

interface BattleRepository {
    suspend fun getLastBattle(tag: ChallengedBrawlStarsPlayerTag): Result<OwnershipTaskBattle>
}
