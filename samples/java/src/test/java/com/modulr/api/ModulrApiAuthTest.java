package com.modulr.api;

import java.security.SignatureException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import com.modulr.api.ModulrApiAuth.HmacAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModulrApiAuthTest {

    private static final String API_TOKEN = "KNOWN-TOKEN";
    private static final String HMAC_SECRET = "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=";

    @Test
    void shouldReturnHeadersWithValidSignatureAndNoRetryWhenReplayNotSet() throws SignatureException {
        LocalDateTime dateTime = LocalDateTime.parse("2016-09-25T16:36:07");
        Clock clock = Clock.fixed(dateTime.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        ModulrApiAuth underTest = new ModulrApiAuth(API_TOKEN, HMAC_SECRET, clock, HmacAlgorithm.SHA1);

        Map<String, String> result = underTest.generateApiAuthHeaders("28154b2-9c62b93cc22a-24c9e2-5536d7d");
        assertEquals(4, result.size());
        assertEquals("28154b2-9c62b93cc22a-24c9e2-5536d7d", result.get("x-mod-nonce"));
        assertEquals("Sun, 25 Sep 2016 16:36:07 GMT", result.get("Date"));
        assertEquals("false", result.get("x-mod-retry"));
        assertEquals("Signature keyId=\"" + API_TOKEN + "\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"z1X8UZJq9jwLPi9peycdHfy3SIY%3D\"", result.get("Authorization"));
    }

    @Test
    void shouldReturnHeadersWithValidSignatureAndRetryWhenReplaySet() throws SignatureException {
        LocalDateTime dateTime = LocalDateTime.parse("2025-08-15T10:30:00");
        Clock clock = Clock.fixed(dateTime.atZone(ZoneId.of("America/New_York")).toInstant(), ZoneId.of("America/New_York"));

        ModulrApiAuth underTest = new ModulrApiAuth(API_TOKEN, HMAC_SECRET, clock, HmacAlgorithm.SHA256);

        String nonce = "abcd-efgh";
        Map<String, String> result = underTest.generateApiAuthHeaders(nonce, true);
        assertEquals(4, result.size());
        assertEquals(nonce, result.get("x-mod-nonce"));
        assertEquals("Fri, 15 Aug 2025 14:30:00 GMT", result.get("Date"));
        assertEquals("true", result.get("x-mod-retry"));
        assertEquals("Signature keyId=\"" + API_TOKEN + "\",algorithm=\"hmac-sha256\",headers=\"date x-mod-nonce\",signature=\"RXqxOPB29c7ezqhYfCe6oeSiHM5xtZDa%2Bxg9PfRWByI%3D\"", result.get("Authorization"));
    }

    @Test
    void shouldReturnHeadersWithValidFormattedDateForSeptember() throws SignatureException {
        LocalDateTime dateTime = LocalDateTime.parse("2023-09-25T16:36:07");
        Clock clock = Clock.fixed(dateTime.atZone(ZoneId.of("GMT")).toInstant(), ZoneId.of("GMT"));

        ModulrApiAuth underTest = new ModulrApiAuth(API_TOKEN, HMAC_SECRET, clock, HmacAlgorithm.SHA512);

        Map<String, String> result = underTest.generateApiAuthHeaders("28154b2-9c62b93cc22a-24c9e2-5536d7d");
        assertEquals("Mon, 25 Sep 2023 16:36:07 GMT", result.get("Date"));
    }

}