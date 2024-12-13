package com.y9vad9.bcm.foundation.validation

import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi

/**
 * Abstraction for factories that construct value objects.
 * Next pattern should be applied to the factories:
 * - Factory should be in companion object that does only one thing – constructing.
 * - Validation information (like sizes or patterns) should be on the top of
 * the factories in order to better readability.
 * - After validation information comes [create] and, if needed, constants
 * with messages below the method.
 *
 * **You should always implement a constructor for value objects, even if
 * there is no actual restrictions on given type. It will help to minimize
 * possible changes and support existing code style rules.**
 */
public interface ValueConstructor<Type, WrappedType> {
    /**
     * Name of the class what is validated. Used to display for API
     * responses.
     */
    public val displayName: String


    /**
     * Instantiates the entity of given type [Type].
     *
     * **Shouldn't throw anything, but instantiate object of type [Type] if possible**
     */
    public fun create(
        value: WrappedType,
    ): Result<Type>
}

public inline fun <T, W> ValueConstructor<T, W>.createOr(
    value: W,
    otherwise: (Throwable) -> T,
): T {
    return create(value).getOrElse(otherwise)
}


@ValidationDelicateApi
@Throws(CreationFailure::class)
public fun <T, W> ValueConstructor<T, W>.createUnsafe(value: W): T {
    return create(value).getOrThrow()
}