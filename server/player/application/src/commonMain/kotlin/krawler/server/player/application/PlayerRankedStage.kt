package krawler.server.player.application

/**
 * Represents a ranked stage in the game's competitive ranking system.
 *
 * Each `RankedStage` is identified by its [ordinal], which determines its position in the overall progression,
 * starting from the lowest stage (e.g. Bronze I) to the highest (e.g. Legendary III or beyond).
 *
 * This class implements [Comparable], allowing stages to be compared based on their [ordinal] values.
 */
@JvmInline
value class PlayerRankedStage(val ordinal: Int) : Comparable<PlayerRankedStage> {

    /**
     * Returns true if this RankedStage ordinal is greater than or equal to [min].
     *
     * Useful for checking if player has reached at least a certain rank.
     *
     * @param min The minimum RankedStage to compare against.
     * @return `true` if this stage is at least [min], otherwise `false`.
     */
    fun isAtLeast(min: PlayerRankedStage): Boolean = this.ordinal >= min.ordinal

    /** Constants with constraints and validation */
    companion object {
        /** Bronze tier stage 1 (lowest entry rank). */
        val BRONZE_ONE: PlayerRankedStage = PlayerRankedStage(1)

        /** Bronze tier stage 2. */
        val BRONZE_TWO: PlayerRankedStage = PlayerRankedStage(2)

        /** Bronze tier stage 3 (final Bronze stage before Silver). */
        val BRONZE_THREE: PlayerRankedStage = PlayerRankedStage(3)

        /** Silver tier stage 1. */
        val SILVER_ONE: PlayerRankedStage = PlayerRankedStage(4)

        /** Silver tier stage 2. */
        val SILVER_TWO: PlayerRankedStage = PlayerRankedStage(5)

        /** Silver tier stage 3 (final Silver stage). */
        val SILVER_THREE: PlayerRankedStage = PlayerRankedStage(6)

        /** Gold tier stage 1. */
        val GOLD_ONE: PlayerRankedStage = PlayerRankedStage(7)

        /** Gold tier stage 2. */
        val GOLD_TWO: PlayerRankedStage = PlayerRankedStage(8)

        /** Gold tier stage 3 (final Gold). */
        val GOLD_THREE: PlayerRankedStage = PlayerRankedStage(9)

        /** Diamond tier stage 1. */
        val DIAMOND_ONE: PlayerRankedStage = PlayerRankedStage(10)

        /** Diamond tier stage 2. */
        val DIAMOND_TWO: PlayerRankedStage = PlayerRankedStage(11)

        /** Diamond tier stage 3 (final Diamond before Mythic). */
        val DIAMOND_THREE: PlayerRankedStage = PlayerRankedStage(12)

        /** Mythic tier stage 1. */
        val MYTHIC_ONE: PlayerRankedStage = PlayerRankedStage(13)

        /** Mythic tier stage 2. */
        val MYTHIC_TWO: PlayerRankedStage = PlayerRankedStage(14)

        /** Mythic tier stage 3 (final Mythic before Legendary). */
        val MYTHIC_THREE: PlayerRankedStage = PlayerRankedStage(15)

        /** Legendary tier stage 1. */
        val LEGENDARY_ONE: PlayerRankedStage = PlayerRankedStage(16)

        /** Legendary tier stage 2. */
        val LEGENDARY_TWO: PlayerRankedStage = PlayerRankedStage(17)

        /** Legendary tier stage 3 (final Legendary before Masters). */
        val LEGENDARY_THREE: PlayerRankedStage = PlayerRankedStage(18)

        /** Master tier stage 1 (first Master stage). */
        val MASTER_ONE: PlayerRankedStage = PlayerRankedStage(19)

        /** Master tier stage 2. */
        val MASTER_TWO: PlayerRankedStage = PlayerRankedStage(20)

        /** Master tier stage 3 (highest official ranked stage below Pro). */
        val MASTER_THREE: PlayerRankedStage = PlayerRankedStage(21)
    }

    /**
     * Compares this [PlayerRankedStage] to another based on their ordinal values.
     *
     * @param other The other RankedStage to compare to.
     * @return negative if this < other, zero if equal, positive if this > other.
     */
    override fun compareTo(other: PlayerRankedStage): Int = ordinal.compareTo(other.ordinal)

    /**
     * Returns a human-readable name for this specific stage, matching the constant’s name if defined.
     *
     * For example, returns `"RankedStage.GOLD_TWO"` for `GOLD_TWO`,
     * or `UnknownRankedStage(ordinal)` if the ordinal is not one of the known constants.
     */
    override fun toString(): String = when (this) {
        BRONZE_ONE -> "RankedStage.BRONZE_ONE"
        BRONZE_TWO -> "RankedStage.BRONZE_TWO"
        BRONZE_THREE -> "RankedStage.BRONZE_THREE"
        SILVER_ONE -> "RankedStage.SILVER_ONE"
        SILVER_TWO -> "RankedStage.SILVER_TWO"
        SILVER_THREE -> "RankedStage.SILVER_THREE"
        GOLD_ONE -> "RankedStage.GOLD_ONE"
        GOLD_TWO -> "RankedStage.GOLD_TWO"
        GOLD_THREE -> "RankedStage.GOLD_THREE"
        DIAMOND_ONE -> "RankedStage.DIAMOND_ONE"
        DIAMOND_TWO -> "RankedStage.DIAMOND_TWO"
        DIAMOND_THREE -> "RankedStage.DIAMOND_THREE"
        MYTHIC_ONE -> "RankedStage.MYTHIC_ONE"
        MYTHIC_TWO -> "RankedStage.MYTHIC_TWO"
        MYTHIC_THREE -> "RankedStage.MYTHIC_THREE"
        LEGENDARY_ONE -> "RankedStage.LEGENDARY_ONE"
        LEGENDARY_TWO -> "RankedStage.LEGENDARY_TWO"
        LEGENDARY_THREE -> "RankedStage.LEGENDARY_THREE"
        MASTER_ONE -> "RankedStage.MASTER_ONE"
        MASTER_TWO -> "RankedStage.MASTER_TWO"
        MASTER_THREE -> "RankedStage.MASTER_THREE"
        else -> "RankedStage.UNKNOWN($ordinal)"
    }
}

// --- Bronze ---
/** Returns `true` if this ranked stage is Bronze I. */
val PlayerRankedStage.isBronzeOne: Boolean
    get() = this == PlayerRankedStage.BRONZE_ONE

/** Returns `true` if this ranked stage is Bronze II. */
val PlayerRankedStage.isBronzeTwo: Boolean
    get() = this == PlayerRankedStage.BRONZE_TWO

/** Returns `true` if this ranked stage is Bronze III. */
val PlayerRankedStage.isBronzeThree: Boolean
    get() = this == PlayerRankedStage.BRONZE_THREE

/** Returns `true` if this ranked stage is within any Bronze tier (Bronze I to Bronze III). */
val PlayerRankedStage.isBronze: Boolean
    get() = this.ordinal in PlayerRankedStage.BRONZE_ONE.ordinal..PlayerRankedStage.BRONZE_THREE.ordinal


// --- Silver ---
/** Returns `true` if this ranked stage is Silver I. */
val PlayerRankedStage.isSilverOne: Boolean
    get() = this == PlayerRankedStage.SILVER_ONE

/** Returns `true` if this ranked stage is Silver II. */
val PlayerRankedStage.isSilverTwo: Boolean
    get() = this == PlayerRankedStage.SILVER_TWO

/** Returns `true` if this ranked stage is Silver III. */
val PlayerRankedStage.isSilverThree: Boolean
    get() = this == PlayerRankedStage.SILVER_THREE

/** Returns `true` if this ranked stage is within any Silver tier (Silver I to Silver III). */
val PlayerRankedStage.isSilver: Boolean
    get() = this.ordinal in PlayerRankedStage.SILVER_ONE.ordinal..PlayerRankedStage.SILVER_THREE.ordinal


// --- Gold ---
/** Returns `true` if this ranked stage is Gold I. */
val PlayerRankedStage.isGoldOne: Boolean
    get() = this == PlayerRankedStage.GOLD_ONE

/** Returns `true` if this ranked stage is Gold II. */
val PlayerRankedStage.isGoldTwo: Boolean
    get() = this == PlayerRankedStage.GOLD_TWO

/** Returns `true` if this ranked stage is Gold III. */
val PlayerRankedStage.isGoldThree: Boolean
    get() = this == PlayerRankedStage.GOLD_THREE

/** Returns `true` if this ranked stage is within any Gold tier (Gold I to Gold III). */
val PlayerRankedStage.isGold: Boolean
    get() = this.ordinal in PlayerRankedStage.GOLD_ONE.ordinal..PlayerRankedStage.GOLD_THREE.ordinal


// --- Diamond ---
/** Returns `true` if this ranked stage is Diamond I. */
val PlayerRankedStage.isDiamondOne: Boolean
    get() = this == PlayerRankedStage.DIAMOND_ONE

/** Returns `true` if this ranked stage is Diamond II. */
val PlayerRankedStage.isDiamondTwo: Boolean
    get() = this == PlayerRankedStage.DIAMOND_TWO

/** Returns `true` if this ranked stage is Diamond III. */
val PlayerRankedStage.isDiamondThree: Boolean
    get() = this == PlayerRankedStage.DIAMOND_THREE

/** Returns `true` if this ranked stage is within any Diamond tier (Diamond I to Diamond III). */
val PlayerRankedStage.isDiamond: Boolean
    get() = this.ordinal in PlayerRankedStage.DIAMOND_ONE.ordinal..PlayerRankedStage.DIAMOND_THREE.ordinal


// --- Mythic ---
/** Returns `true` if this ranked stage is Mythic I. */
val PlayerRankedStage.isMythicOne: Boolean
    get() = this == PlayerRankedStage.MYTHIC_ONE

/** Returns `true` if this ranked stage is Mythic II. */
val PlayerRankedStage.isMythicTwo: Boolean
    get() = this == PlayerRankedStage.MYTHIC_TWO

/** Returns `true` if this ranked stage is Mythic III. */
val PlayerRankedStage.isMythicThree: Boolean
    get() = this == PlayerRankedStage.MYTHIC_THREE

/** Returns `true` if this ranked stage is within any Mythic tier (Mythic I to Mythic III). */
val PlayerRankedStage.isMythic: Boolean
    get() = this.ordinal in PlayerRankedStage.MYTHIC_ONE.ordinal..PlayerRankedStage.MYTHIC_THREE.ordinal

// --- Legendary ---
/** Returns `true` if this ranked stage is Legendary I. */
val PlayerRankedStage.isLegendaryOne: Boolean
    get() = this == PlayerRankedStage.LEGENDARY_ONE

/** Returns `true` if this ranked stage is Legendary II. */
val PlayerRankedStage.isLegendaryTwo: Boolean
    get() = this == PlayerRankedStage.LEGENDARY_TWO

/** Returns `true` if this ranked stage is Legendary III. */
val PlayerRankedStage.isLegendaryThree: Boolean
    get() = this == PlayerRankedStage.LEGENDARY_THREE

/** Returns `true` if this ranked stage is within any Legendary tier (Legendary I to Legendary III). */
val PlayerRankedStage.isLegendary: Boolean
    get() = this.ordinal in PlayerRankedStage.LEGENDARY_ONE.ordinal..PlayerRankedStage.LEGENDARY_THREE.ordinal


// --- Master ---
/** Returns `true` if this ranked stage is Master I. */
val PlayerRankedStage.isMasterOne: Boolean
    get() = this == PlayerRankedStage.MASTER_ONE

/** Returns `true` if this ranked stage is Master II. */
val PlayerRankedStage.isMasterTwo: Boolean
    get() = this == PlayerRankedStage.MASTER_TWO

/** Returns `true` if this ranked stage is Master III. */
val PlayerRankedStage.isMasterThree: Boolean
    get() = this == PlayerRankedStage.MASTER_THREE

/** Returns `true` if this ranked stage is within any Master tier (Master I to Master III). */
val PlayerRankedStage.isMaster: Boolean
    get() = this.ordinal in PlayerRankedStage.MASTER_ONE.ordinal..PlayerRankedStage.MASTER_THREE.ordinal

// Bronze

/** Returns true if this stage is at least Bronze I. */
val PlayerRankedStage.isAtLeastBronzeOne: Boolean
    get() = isAtLeast(PlayerRankedStage.BRONZE_ONE)

/** Returns true if this stage is at least Bronze II. */
val PlayerRankedStage.isAtLeastBronzeTwo: Boolean
    get() = isAtLeast(PlayerRankedStage.BRONZE_TWO)

/** Returns true if this stage is at least Bronze III. */
val PlayerRankedStage.isAtLeastBronzeThree: Boolean
    get() = isAtLeast(PlayerRankedStage.BRONZE_THREE)


// Silver

/** Returns true if this stage is at least Silver I. */
val PlayerRankedStage.isAtLeastSilverOne: Boolean
    get() = isAtLeast(PlayerRankedStage.SILVER_ONE)

/** Returns true if this stage is at least Silver II. */
val PlayerRankedStage.isAtLeastSilverTwo: Boolean
    get() = isAtLeast(PlayerRankedStage.SILVER_TWO)

/** Returns true if this stage is at least Silver III. */
val PlayerRankedStage.isAtLeastSilverThree: Boolean
    get() = isAtLeast(PlayerRankedStage.SILVER_THREE)


// Gold

/** Returns true if this stage is at least Gold I. */
val PlayerRankedStage.isAtLeastGoldOne: Boolean
    get() = isAtLeast(PlayerRankedStage.GOLD_ONE)

/** Returns true if this stage is at least Gold II. */
val PlayerRankedStage.isAtLeastGoldTwo: Boolean
    get() = isAtLeast(PlayerRankedStage.GOLD_TWO)

/** Returns true if this stage is at least Gold III. */
val PlayerRankedStage.isAtLeastGoldThree: Boolean
    get() = isAtLeast(PlayerRankedStage.GOLD_THREE)

// Diamond

/** Returns true if this stage is at least Diamond I. */
val PlayerRankedStage.isAtLeastDiamondOne: Boolean
    get() = isAtLeast(PlayerRankedStage.DIAMOND_ONE)

/** Returns true if this stage is at least Diamond II. */
val PlayerRankedStage.isAtLeastDiamondTwo: Boolean
    get() = isAtLeast(PlayerRankedStage.DIAMOND_TWO)

/** Returns true if this stage is at least Diamond III. */
val PlayerRankedStage.isAtLeastDiamondThree: Boolean get() = isAtLeast(PlayerRankedStage.DIAMOND_THREE)


// Mythic

/** Returns true if this stage is at least Mythic I. */
val PlayerRankedStage.isAtLeastMythicOne: Boolean
    get() = isAtLeast(PlayerRankedStage.MYTHIC_ONE)

/** Returns true if this stage is at least Mythic II. */
val PlayerRankedStage.isAtLeastMythicTwo: Boolean
    get() = isAtLeast(PlayerRankedStage.MYTHIC_TWO)

/** Returns true if this stage is at least Mythic III. */
val PlayerRankedStage.isAtLeastMythicThree: Boolean
    get() = isAtLeast(PlayerRankedStage.MYTHIC_THREE)


// Legendary

/** Returns true if this stage is at least Legendary I. */
val PlayerRankedStage.isAtLeastLegendaryOne: Boolean get() = isAtLeast(PlayerRankedStage.LEGENDARY_ONE)

/** Returns true if this stage is at least Legendary II. */
val PlayerRankedStage.isAtLeastLegendaryTwo: Boolean get() = isAtLeast(PlayerRankedStage.LEGENDARY_TWO)

/** Returns true if this stage is at least Legendary III. */
val PlayerRankedStage.isAtLeastLegendaryThree: Boolean get() = isAtLeast(PlayerRankedStage.LEGENDARY_THREE)


// Master

/** Returns true if this stage is at least Master I. */
val PlayerRankedStage.isAtLeastMasterOne: Boolean
    get() = isAtLeast(PlayerRankedStage.MASTER_ONE)

/** Returns true if this stage is at least Master II. */
val PlayerRankedStage.isAtLeastMasterTwo: Boolean get() = isAtLeast(PlayerRankedStage.MASTER_TWO)
