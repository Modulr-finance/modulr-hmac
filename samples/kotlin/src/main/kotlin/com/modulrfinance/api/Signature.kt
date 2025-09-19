package com.modulrfinance.api

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.SignatureException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import java.util.UUID
import javax.crypto.spec.SecretKeySpec

class Signature(
    private val keyId: String,
    private val keySecret: String,
    private val clock: Clock = Clock.systemUTC(),
    private val hmacAlgorithm: HmacAlgorithm = HmacAlgorithm.HMAC_SHA512
) {

    fun generateApiAuthHeaders(
        nonce: String = UUID.randomUUID().toString(),
        retry: Boolean = false
    ): Map<String, String> {
        val timestamp = formatDate(clock.instant())
        val message = "date: ${timestamp}\nx-mod-nonce: $nonce"

        val signature = try {
            val signingKey = SecretKeySpec(
                keySecret.toByteArray(StandardCharsets.UTF_8),
                hmacAlgorithm.javaName
            )
            val mac = javax.crypto.Mac.getInstance(hmacAlgorithm.javaName)
            mac.init(signingKey)

            // compute the hmac on input data bytes
            val rawHmac = mac.doFinal(message.toByteArray(StandardCharsets.UTF_8))

            // base64-encode the hmac
            val hmac = Base64.getEncoder().encodeToString(rawHmac)
            URLEncoder.encode(hmac, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw SignatureException("Failed to generate HMAC signature", e)
        }

        return mapOf(
            "Authorization" to "Signature keyId=\"$keyId\",algorithm=\"${hmacAlgorithm.apiName}\",headers=\"date x-mod-nonce\",signature=\"$signature\"",
            "x-mod-nonce" to nonce,
            "Date" to timestamp,
            "x-mod-retry" to retry.toString())
    }

    companion object {

        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
            ZoneId.of("GMT"))

        private fun formatDate(instant: Instant): String {
            return DATE_TIME_FORMATTER.format(instant)
        }
    }

    enum class HmacAlgorithm(val javaName: String, val apiName: String) {
        HMAC_SHA1("HmacSHA1", "hmac-sha1"),
        HMAC_SHA256("HmacSHA256", "hmac-sha256"),
        HMAC_SHA384("HmacSHA384", "hmac-sha384"),
        HMAC_SHA512("HmacSHA512", "hmac-sha512")
    }
}