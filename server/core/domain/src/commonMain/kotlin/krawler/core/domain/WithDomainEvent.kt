package krawler.core.domain

@MustUseReturnValue
public data class WithDomainEvent<TReturn : Any, TEvent>(
    public val returning: TReturn,
    public val event: TEvent,
)
