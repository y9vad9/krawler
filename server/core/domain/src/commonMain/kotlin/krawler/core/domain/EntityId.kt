package krawler.core.domain

/**
 * Marks a property or type as the **unique identifier** of an [Entity].
 *
 * An Entity ID is used to distinguish one entity instance from another
 * and remains constant throughout the entity's lifecycle.
 *
 * Typically applied to primitive types or value objects that represent
 * the entity's identity, e.g., `UUID`, `Long`, or a custom identifier class.
 *
 * @see Entity
 */
@Target(allowedTargets = [AnnotationTarget.PROPERTY])
@Retention(value = AnnotationRetention.SOURCE)
public annotation class EntityId