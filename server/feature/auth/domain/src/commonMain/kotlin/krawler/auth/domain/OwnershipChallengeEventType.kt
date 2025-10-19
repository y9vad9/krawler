package krawler.auth.domain

import krawler.core.domain.ValueObject

/**
 * The list of brawl stars event types for an ownership challenge.
 *
 * We accept only those, because they're classic and, therefore,
 * not a subject to be removed from friendly battles.
 *
 * We can't really understand dynamically which game modes are available for
 * friendly battles.
 */
@ValueObject
public enum class OwnershipChallengeEventType {
    BRAWL_BALL,
    SOLO_SHOWDOWN,
    DUO_SHOWDOWN,
    GEM_GRAB,
    KNOCKOUT,
}
