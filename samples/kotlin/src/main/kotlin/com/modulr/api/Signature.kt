package com.modulr.api

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Signature(
        private val apiKey: String,
        private val apiSecret: String
) {

    fun calculate(nonce: String = UUID.randomUUID().toString(), date: Date = Date()): Result {
        val timestamp = formatDate(date)
        val message = "date: ${timestamp}\nx-mod-nonce: $nonce"

        val signature = try {
            val signingKey = SecretKeySpec(apiSecret.toByteArray(), HMAC_SHA1_ALGORITHM)
            val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
            mac.init(signingKey)

            // compute the hmac on input data bytes
            val rawHmac = mac.doFinal(message.toByteArray())

            // base64-encode the hmac
            val hmac = Base64.getEncoder().encodeToString(rawHmac)
            URLEncoder.encode(hmac, StandardCharsets.UTF_8.name())
        } catch (e: Exception) {
            throw RuntimeException("Failed to generate HMAC signature", e)
        }

        return Result(apiKey, nonce, signature, timestamp)
    }

    companion object {

        private const val DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z"
        private const val HMAC_SHA1_ALGORITHM = "HmacSHA1"

        private fun formatDate(date: Date): String {
            val sdf = SimpleDateFormat(DATE_PATTERN, Locale.UK)
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            return sdf.format(date)
        }
    }
}

data class Result(
        private val apiKey: String,
        val nonce: String,
        val signature: String,
        val timestamp: String
) {

    val headers: Map<String, String>
        get() = mapOf(
                "Authorization" to "Signature keyId=\"$apiKey\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"$signature\"",
                "Date" to timestamp,
                "x-mod-nonce" to nonce
        )
}