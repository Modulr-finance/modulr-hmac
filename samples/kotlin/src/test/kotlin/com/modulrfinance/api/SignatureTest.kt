package com.modulrfinance.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class SignatureTest {

    private val signature = Signature(API_KEY, API_SECRET, Clock.fixed(Instant.parse("2025-09-19T09:00:00Z"), ZoneId.of("GMT")), Signature.HmacAlgorithm.HMAC_SHA512)

    @Test
    fun testHmacGeneration() {
        val headers = signature.generateApiAuthHeaders()

        assertNotNull(headers["Authorization"])
        assertNotNull(headers["Date"])
        assertNotNull(headers["x-mod-nonce"])
    }

    @Test
    fun testHmacGenerationWithKnownNonceAndRetry() {
        val headers = signature.generateApiAuthHeaders("28154b2-9c62b93cc22a-24c9e2-5536d7d", true)

        assertEquals("Signature keyId=\"KNOWN-TOKEN\",algorithm=\"hmac-sha512\",headers=\"date x-mod-nonce\",signature=\"gzOURk9%2FR%2FiYnK9yqjBMqEK%2BLVrK7u3lplG1KGfpIZQLpJuWZJABuHRmK82P%2BmC4fnEbGgx8ls8ejKdR%2Fxbasw%3D%3D\"", headers["Authorization"])
        assertEquals("Fri, 19 Sep 2025 09:00:00 GMT", headers["Date"])
        assertEquals("28154b2-9c62b93cc22a-24c9e2-5536d7d", headers["x-mod-nonce"])
        assertEquals("true", headers["x-mod-retry"])
    }

    companion object {

        private const val API_KEY = "KNOWN-TOKEN"
        private const val API_SECRET = "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI="
    }
}