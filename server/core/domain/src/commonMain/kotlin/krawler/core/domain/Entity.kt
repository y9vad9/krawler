package krawler.core.domain

/**
 * Marks a class as an **Entity** in the sense of Domain-Driven Design (DDD).
 *
 * An Entity represents a domain concept that is defined by its **identity**
 * rather than its attributes. Its state may change over time, but its identity
 * remains constant throughout its lifecycle.
 *
 * Common examples include `User`, `Order`, or `Account`.
 *
 * @see ValueObject for identity-less types defined by their attributes
 */
@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.SOURCE)
public annotation class Entity