package com.modulr.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;


import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ModulrApiAuthTest {

    /* Test Data*/
    private static final String DATE_STR = "2016-07-25T16:36:07";
    private static final String API_TOKEN = "KNOWN-TOKEN";
    private static final String HMAC_SECRET = "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=";
    private static final String NONCE = "28154b2-9c62b93cc22a-24c9e2-5536d7d";
    private static final String EXPECTED_HMAC_SIGNATURE = "WBMr%2FYdhysbmiIEkdTrf2hP7SfA%3D";

    private ModulrApiAuth underTest;
    private LocalDateTime dateTime;
    private ZonedDateTime utcDateTime;
    private Date date;

    @Before
    public void setUp() {
        dateTime = LocalDateTime.parse(DATE_STR);
        utcDateTime = ZonedDateTime.of(dateTime,ZoneId.of("Z"));
        date = Date.from(utcDateTime.toInstant());
    }

    @Test
    public void testHmacGeneratorWithNonNullToken() throws SignatureException {
        ModulrApiAuth authApi = spy(new ModulrApiAuth(API_TOKEN, HMAC_SECRET));
        when(authApi.getDate()).thenReturn(date);

        Map<String, String> headers = authApi.generateApiAuthHeaders(NONCE);
        assertEquals(headers.size(), 4);
        assertEquals(NONCE, headers.get("x-mod-nonce"));
        assertEquals(DateTimeFormatter.RFC_1123_DATE_TIME.format(utcDateTime), headers.get("Date"));
        assertEquals("false", headers.get("x-mod-retry"));
        assertEquals("Signature keyId=\""+ API_TOKEN+ "\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"" + EXPECTED_HMAC_SIGNATURE + "\"", headers.get("Authorization"));

        when(authApi.getDate()).thenCallRealMethod();
        Map<String, String> headersWithRetryOn = authApi.generateRetryApiAuthHeaders();
        assertEquals("true", headersWithRetryOn.get("x-mod-retry"));
        assertEquals(headers.get("x-mod-nonce"), headersWithRetryOn.get("x-mod-nonce"));
        assertNotEquals(headers.get("Date"), headersWithRetryOn.get("Date"));
        assertNotEquals(headers.get("Authorization"), headersWithRetryOn.get("Authorization"));
    }

    @Test
    public void testHmacGeneratorWithNullToken() throws SignatureException {
        ModulrApiAuth authApi = spy(new ModulrApiAuth(null, HMAC_SECRET, () -> date));
        assertEquals(EXPECTED_HMAC_SIGNATURE, authApi.generateHmac(NONCE));
    }


    @Test
    public void testHmacGeneratorWithDatSupplier() throws SignatureException {
        Supplier<Date> dateSupplier = (Supplier<Date>) mock(Supplier.class);
        when(dateSupplier.get()).thenReturn(date).thenReturn(new Date());
        ModulrApiAuth authApi = new ModulrApiAuth(API_TOKEN, HMAC_SECRET, dateSupplier);

        Map<String, String> headers = authApi.generateApiAuthHeaders(NONCE);
        assertEquals(headers.size(), 4);
        assertEquals(NONCE, headers.get("x-mod-nonce"));
        assertEquals(DateTimeFormatter.RFC_1123_DATE_TIME.format(utcDateTime), headers.get("Date"));
        assertEquals("false", headers.get("x-mod-retry"));
        assertEquals("Signature keyId=\""+ API_TOKEN+ "\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"" + EXPECTED_HMAC_SIGNATURE + "\"", headers.get("Authorization"));

        Map<String, String> headersWithRetryOn = authApi.generateRetryApiAuthHeaders();
        assertEquals("true", headersWithRetryOn.get("x-mod-retry"));
        assertEquals(headers.get("x-mod-nonce"), headersWithRetryOn.get("x-mod-nonce"));
        assertNotEquals(headers.get("Date"), headersWithRetryOn.get("Date"));
        assertNotEquals(headers.get("Authorization"), headersWithRetryOn.get("Authorization"));
    }
}
