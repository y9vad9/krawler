package com.y9vad9.starix.foundation.validation.reflection

import com.y9vad9.starix.foundation.validation.ValueConstructor

@Suppress("UnusedReceiverParameter")
public inline fun <reified T> ValueConstructor<T, *>.wrapperTypeName(): Lazy<String> =
    lazy { T::class.simpleName!! }