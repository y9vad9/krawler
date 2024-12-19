//package com.y9vad9.bcm.core.usecase.jobs
//
//import com.y9vad9.starix.core.entity.getBsMember
//import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository
//import com.y9vad9.starix.core.telegram.repository.ChatRepository
//import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsClubHistoryRepository
//import com.y9vad9.starix.core.system.repository.UserRepository
//
//class KickNotMembersFromChatUseCase(
//    private val chats: ChatRepository,
//    private val clubs: ClubRepository,
//    private val history: BrawlStarsClubHistoryRepository,
//    private val members: UserRepository,
//    private val brawlStars: BrawlStarsRepository,
//) {
//    suspend fun execute(): Result = try {
//        val latest = history.getLatestSavedAllowedClubs()
//
//        val current = clubs.getAllowedClubs()
//
//        current.forEach { club ->
//            val prev = latest.firstOrNull { club.bs.tag == it.bs.tag } ?: return@forEach
//            val prevMembersTags = prev.bs.members.map { member -> member.tag }
//            val currentMembersTags = club.bs.members.map { member -> member.tag }
//
//            val removed = prevMembersTags.subtract(currentMembersTags)
//                .associateBy { tag -> members.getByTag(tag)!! }
//
//            removed.forEach { (member, tag) ->
//                member.telegramAccount ?: return@forEach
//
//                val memberClubChats = member.bsPlayers!!.map { player ->
//                    current.first { club -> club.bs.tag == player.club.tag }
//                }
//
//                val chatUsed = memberClubChats.count { club.linkedGroupId ==  it.linkedGroupId }
//
//                val message = if(chatUsed == 1) {
//                    chats.kick(club.linkedGroupId, member.telegramAccount.id)
//                    ChatRepository.GroupMessage.LeftClub(
//                        player = brawlStars.getPlayer(club.getBsMember(tag).tag),
//                        club = club,
//                        clubsLeft = null,
//                    )
//                } else {
//                    ChatRepository.GroupMessage.LeftClub(
//                        player = brawlStars.getPlayer(club.getBsMember(tag).tag),
//                        club = club,
//                        clubsLeft = memberClubChats.filter { it != club },
//                    )
//                }
//
//                chats.sendGroupMessage(club.linkedGroupId, message)
//            }
//        }
//        TODO()
//    } catch (e: Exception) {
//        Result.Failure(e)
//    }
//
//    sealed interface Result {
//        data object Success : Result
//        data class Failure(val error: Exception) : Result
//    }
//}