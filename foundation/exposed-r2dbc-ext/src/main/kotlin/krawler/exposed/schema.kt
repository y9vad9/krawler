package krawler.exposed

import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction

/**
 * Creates a PostgreSQL enum type based on a Kotlin `Enum` class.
 *
 * This function will attempt to create a database enum type with the same name as the Kotlin enum
 * and with values corresponding to the enum constants. If the type already exists, the error is
 * ignored.
 *
 * Example usage:
 * ```
 * enum class PlayerStatus { ACTIVE, INACTIVE, BANNED }
 * transaction.createEnumTypeIgnoring<PlayerStatus>()
 * ```
 *
 * @throws IllegalStateException if the Kotlin enum class does not have a simple name.
 */
public suspend inline fun <reified T : Enum<T>> R2dbcTransaction.createEnumTypeIgnoring() {
    val enumName = T::class.simpleName?.lowercase() ?: error("Enum must have a name")
    val enumValues = enumValues<T>().joinToString(",") { "'${it.name}'" }

    try {
        exec("CREATE TYPE $enumName AS ENUM ($enumValues)")
    } catch (_: Exception) {
        // swallow "type already exists" error
    }
}
