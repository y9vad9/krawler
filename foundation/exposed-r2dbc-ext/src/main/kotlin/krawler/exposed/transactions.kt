@file:Suppress("detekt.ForbiddenMethodCall") // we allow suspendTransaction here
package krawler.exposed

import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.transactions.transactionManager

/**
 * Executes the given [statement] in a read-only transaction on the specified [database].
 *
 * @param R The type returned by the transaction lambda.
 * @param database The [R2dbcDatabase] on which the transaction will run.
 * @param statement The suspending lambda containing the transaction logic.
 * @return The result of executing [statement].
 * @throws IllegalStateException If the database does not have a default isolation level set.
 */
public suspend fun <R> suspendReadTransaction(
    database: R2dbcDatabase,
    statement: suspend R2dbcTransaction.() -> R,
): R = suspendTransaction(
    transactionIsolation = database.transactionManager.defaultIsolationLevel
        ?: error("A default isolation level for this transaction has not been set"),
    db = database,
    readOnly = true,
    statement = statement,
)

/**
 * Executes the given [statement] in a write-enabled transaction on the specified [database].
 *
 * @param R The type returned by the transaction lambda.
 * @param database The [R2dbcDatabase] on which the transaction will run.
 * @param statement The suspending lambda containing the transaction logic.
 * @return The result of executing [statement].
 * @throws IllegalStateException If the database does not have a default isolation level set.
 */
public suspend fun <R> suspendWriteTransaction(
    database: R2dbcDatabase,
    statement: suspend R2dbcTransaction.() -> R,
): R = suspendTransaction(
    transactionIsolation = database.transactionManager.defaultIsolationLevel
        ?: error("A default isolation level for this transaction has not been set"),
    db = database,
    readOnly = false,
    statement = statement,
)
