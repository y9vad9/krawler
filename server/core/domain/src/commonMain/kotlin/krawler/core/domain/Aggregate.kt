package krawler.core.domain

/**
 * Marks a class as an **Aggregate** in the sense of Domain-Driven Design (DDD).
 *
 * An Aggregate represents a **cluster of associated Entities and Value Objects**
 * that are treated as a single unit for data changes. It defines a **consistency boundary**
 * within which all business invariants must be maintained.
 *
 * Each Aggregate has exactly one **Aggregate Root**, which controls all access to the
 * internal objects and ensures consistency across the Aggregate.
 *
 * Common examples include `Order` as an aggregate containing multiple `OrderLine` entities,
 * or `Customer` containing `Address` value objects.
 *
 * @see AggregateRoot
 * @see Entity
 * @see ValueObject
 */
@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.SOURCE)
public annotation class Aggregate
