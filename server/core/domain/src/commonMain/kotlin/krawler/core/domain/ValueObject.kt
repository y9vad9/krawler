package krawler.core.domain

/**
 * Marks a class as a **Value Object** in the sense of Domain-Driven Design (DDD).
 *
 * A Value Object is an immutable type that is identified **only by its attributes**,
 * not by an identity or lifecycle. Two Value Objects are considered equal
 * when all their defining properties are equal.
 *
 * Common examples include types like `Money`, `DateRange`, or `Address`.
 *
 * @see Entity for objects with identity and lifecycle
 */
@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.SOURCE)
public annotation class ValueObject