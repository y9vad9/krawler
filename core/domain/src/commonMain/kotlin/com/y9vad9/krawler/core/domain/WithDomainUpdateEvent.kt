package com.y9vad9.krawler.core.domain

@MustUseReturnValue
public data class WithDomainUpdateEvent<TReturn : Any, TEvent>(
    public val returning: TReturn,
    public val event: TEvent,
)
