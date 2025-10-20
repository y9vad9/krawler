package krawler.server.auth.application

import krawler.server.auth.domain.ChallengedPlayerTag
import krawler.server.auth.domain.OwnershipTaskBattle

interface BattleRepository {
    suspend fun getLastBattle(tag: ChallengedPlayerTag): Result<OwnershipTaskBattle>
}
