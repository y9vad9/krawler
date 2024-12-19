package com.y9vad9.starix.foundation.time

public interface TimeProvider {
    /**
     * Provides current time in Unix format.
     */
    public fun provide(): UnixTime
}