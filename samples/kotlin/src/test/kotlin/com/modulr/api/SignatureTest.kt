package com.modulr.api

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SignatureTest {

    private val signature = Signature(API_KEY, API_SECRET)

    @Test
    fun testHmacGeneration() {
        val result = signature.calculate()

        assertNotNull(result.nonce)
        assertNotNull(result.signature)
        assertNotNull(result.timestamp)

        val headers = result.headers

        assertNotNull(headers["Authorization"])
        assertNotNull(headers["Date"])
        assertNotNull(headers["x-mod-nonce"])
    }

    @Test
    fun testHmacGenerationWithKnownNonceAndDate() {
        val result = signature.calculate("28154b2-9c62b93cc22a-24c9e2-5536d7d", toDate("2016-09-25T16:36:07"))

        assertEquals("28154b2-9c62b93cc22a-24c9e2-5536d7d", result.nonce)
        assertEquals("z1X8UZJq9jwLPi9peycdHfy3SIY%3D", result.signature)
        assertEquals("Sun, 25 Sep 2016 16:36:07 GMT", result.timestamp)

        val headers = result.headers

        assertEquals("Signature keyId=\"${API_KEY}\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"z1X8UZJq9jwLPi9peycdHfy3SIY%3D\"", headers["Authorization"])
        assertEquals("Sun, 25 Sep 2016 16:36:07 GMT", headers["Date"])
        assertEquals("28154b2-9c62b93cc22a-24c9e2-5536d7d", headers["x-mod-nonce"])
    }

    private fun toDate(str: String): Date {
        val dateTime = LocalDateTime.parse(str)
        val utcDateTime = ZonedDateTime.of(dateTime, ZoneId.of("Z"))

        return Date.from(utcDateTime.toInstant())
    }

    companion object {

        private const val API_KEY = "KNOWN-TOKEN"
        private const val API_SECRET = "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI="
    }
}