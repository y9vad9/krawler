package com.y9vad9.bcm.core.common.entity.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class CountryCode private constructor(val value: String) {
    companion object : ValueConstructor<CountryCode, String> {
        const val REQUIRED_SIZE: Int = 2

        override val displayName: String = "CountryCode"

        /**
         * Used to provide information about global instead of localized information.
         */
        val GLOBAL: CountryCode = CountryCode("GLOBAL")

        val UKRAINE: CountryCode = CountryCode("UA")
        val GERMANY: CountryCode = CountryCode("DE")
        val CZECH_REPUBLIC: CountryCode = CountryCode("CZ")
        val SLOVAKIA: CountryCode = CountryCode("SK")
        val AUSTRIA: CountryCode = CountryCode("AT")
        val USA: CountryCode = CountryCode("US")
        val CANADA: CountryCode = CountryCode("CA")
        val BRAZIL: CountryCode = CountryCode("BR")
        val MEXICO: CountryCode = CountryCode("MX")
        val JAPAN: CountryCode = CountryCode("JP")
        val SOUTH_KOREA: CountryCode = CountryCode("KR")
        val INDIA: CountryCode = CountryCode("IN")
        val INDONESIA: CountryCode = CountryCode("ID")
        val PHILIPPINES: CountryCode = CountryCode("PH")
        val THAILAND: CountryCode = CountryCode("TH")
        val VIETNAM: CountryCode = CountryCode("VN")
        val TURKEY: CountryCode = CountryCode("TR")
        val FRANCE: CountryCode = CountryCode("FR")
        val ITALY: CountryCode = CountryCode("IT")
        val SPAIN: CountryCode = CountryCode("ES")
        val POLAND: CountryCode = CountryCode("PL")
        val NETHERLANDS: CountryCode = CountryCode("NL")
        val BELGIUM: CountryCode = CountryCode("BE")
        val SWEDEN: CountryCode = CountryCode("SE")
        val NORWAY: CountryCode = CountryCode("NO")
        val DENMARK: CountryCode = CountryCode("DK")
        val FINLAND: CountryCode = CountryCode("FI")
        val AUSTRALIA: CountryCode = CountryCode("AU")
        val NEW_ZEALAND: CountryCode = CountryCode("NZ")
        val ARGENTINA: CountryCode = CountryCode("AR")
        val CHILE: CountryCode = CountryCode("CL")
        val COLOMBIA: CountryCode = CountryCode("CO")
        val PERU: CountryCode = CountryCode("PE")
        val EGYPT: CountryCode = CountryCode("EG")
        val SAUDI_ARABIA: CountryCode = CountryCode("SA")
        val UNITED_ARAB_EMIRATES: CountryCode = CountryCode("AE")
        val ISRAEL: CountryCode = CountryCode("IL")
        val SINGAPORE: CountryCode = CountryCode("SG")
        val MALAYSIA: CountryCode = CountryCode("MY")

        override fun create(value: String): Result<CountryCode> {
            return when {
                value == GLOBAL.value -> Result.success(GLOBAL)
                value.length == REQUIRED_SIZE -> Result.success(CountryCode(value))
                else -> Result.failure(CreationFailure.ofSizeExact(REQUIRED_SIZE))
            }
        }
    }
}