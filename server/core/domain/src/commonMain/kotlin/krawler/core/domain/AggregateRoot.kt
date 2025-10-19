package krawler.core.domain

/**
 * Marks a class as an **Aggregate Root** in the sense of Domain-Driven Design (DDD).
 *
 * The Aggregate Root is the **entry point** to an [Aggregate]. It is responsible
 * for enforcing all business invariants and ensuring the consistency of the entire
 * Aggregate. External objects may only hold references to, and interact with,
 * the Aggregate through its root.
 *
 * The Aggregate Root is always an [Entity] and controls access to all other
 * Entities and Value Objects inside the Aggregate boundary.
 *
 * Common examples include `Order` as the root of an aggregate of `OrderLine` entities,
 * or `Customer` as the root of a set of related `Address` value objects.
 *
 * @see Aggregate
 * @see Entity
 * @see ValueObject
 */
@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.SOURCE)
public annotation class AggregateRoot
