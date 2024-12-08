package com.y9vad9.bcm.foundation.validation.reflection

import com.y9vad9.bcm.foundation.validation.SafeConstructor

@Suppress("UnusedReceiverParameter")
public inline fun <reified T> SafeConstructor<T, *>.wrapperTypeName(): Lazy<String> =
    lazy { T::class.simpleName!! }