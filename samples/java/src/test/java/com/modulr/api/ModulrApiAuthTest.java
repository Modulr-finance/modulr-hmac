package com.modulr.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;


import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ModulrApiAuthTest {

    /* Test Data*/
    private static final String DATE_STR = "2016-09-25T16:36:07";
    private static final String API_TOKEN = "KNOWN-TOKEN";
    private static final String HMAC_SECRET = "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=";
    private static final String NONCE = "28154b2-9c62b93cc22a-24c9e2-5536d7d";
    private static final String EXPECTED_HMAC_SIGNATURE = "z1X8UZJq9jwLPi9peycdHfy3SIY%3D";

    @Mock
    Supplier<Date> dateSupplier;

    private ModulrApiAuth underTest;

    @Test
    public void testHmacGeneratorWithNonNullToken() throws SignatureException {
        underTest = spy(new ModulrApiAuth(API_TOKEN, HMAC_SECRET));

        LocalDateTime dateTime = LocalDateTime.parse(DATE_STR);
        ZonedDateTime utcDateTime = ZonedDateTime.of(dateTime,ZoneId.of("Z"));

        Date date = Date.from(utcDateTime.toInstant());

        /* Generate headers and assert they are as expected */
        when(underTest.getDate()).thenReturn(date);
        Map<String, String> headers = underTest.generateApiAuthHeaders(NONCE);
        assertEquals(headers.size(), 4);
        assertEquals(NONCE, headers.get("x-mod-nonce"));
        assertEquals(DateTimeFormatter.RFC_1123_DATE_TIME.format(utcDateTime), headers.get("Date"));
        assertEquals("false", headers.get("x-mod-retry"));
        assertEquals("Signature keyId=\""+ API_TOKEN+ "\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"" + EXPECTED_HMAC_SIGNATURE + "\"", headers.get("Authorization"));

        /* Generate retry headers and assert they are as expected
         * x-mod-retry should be true
         * Date should not be same as previous
         * Nonce should be same as previous
         * Signature will be different because the date has changed */
        when(underTest.getDate()).thenCallRealMethod();
        Map<String, String> headersWithRetryOn = underTest.generateRetryApiAuthHeaders();
        assertEquals("true", headersWithRetryOn.get("x-mod-retry"));
        assertEquals(headers.get("x-mod-nonce"), headersWithRetryOn.get("x-mod-nonce"));
        assertNotEquals(headers.get("Date"), headersWithRetryOn.get("Date"));
        assertNotEquals(headers.get("Authorization"), headersWithRetryOn.get("Authorization"));
    }

    @Test
    public void testHmacGeneratorWithNullToken() throws SignatureException {
        LocalDateTime dateTime = LocalDateTime.parse(DATE_STR);
        ZonedDateTime utcDateTime = ZonedDateTime.of(dateTime,ZoneId.of("Z"));

        Date date = Date.from(utcDateTime.toInstant());
        underTest = spy(new ModulrApiAuth(null, HMAC_SECRET, () -> date));

        assertEquals(EXPECTED_HMAC_SIGNATURE, underTest.generateHmac(NONCE));
    }


    @Test
    public void testHmacGeneratorWithDatSupplier() throws SignatureException {

        LocalDateTime dateTime = LocalDateTime.parse(DATE_STR);
        ZonedDateTime utcDateTime = ZonedDateTime.of(dateTime,ZoneId.of("Z"));

        Date date = Date.from(utcDateTime.toInstant());
        when(dateSupplier.get()).thenReturn(date).thenReturn(new Date());

        underTest = new ModulrApiAuth(API_TOKEN, HMAC_SECRET, dateSupplier);

        /* Generate headers and assert they are as expected */
        Map<String, String> headers = underTest.generateApiAuthHeaders(NONCE);
        assertEquals(headers.size(), 4);
        assertEquals(NONCE, headers.get("x-mod-nonce"));
        assertEquals(DateTimeFormatter.RFC_1123_DATE_TIME.format(utcDateTime), headers.get("Date"));
        assertEquals("false", headers.get("x-mod-retry"));
        assertEquals("Signature keyId=\""+ API_TOKEN+ "\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"" + EXPECTED_HMAC_SIGNATURE + "\"", headers.get("Authorization"));

        /* Generate retry headers and assert they are as expected
         * x-mod-retry should be true
         * Date should not be same as previous
         * Nonce should be same as previous
         * Signature will be different because the date has changed */
        Map<String, String> headersWithRetryOn = underTest.generateRetryApiAuthHeaders();
        assertEquals("true", headersWithRetryOn.get("x-mod-retry"));
        assertEquals(headers.get("x-mod-nonce"), headersWithRetryOn.get("x-mod-nonce"));
        assertNotEquals(headers.get("Date"), headersWithRetryOn.get("Date"));
        assertNotEquals(headers.get("Authorization"), headersWithRetryOn.get("Authorization"));
    }
}