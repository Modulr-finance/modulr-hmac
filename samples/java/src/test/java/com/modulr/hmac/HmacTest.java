package com.modulr.hmac;

import com.modulr.api.ModulrApiAuth;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HmacTest {
    private ModulrApiAuth modulrAuth;

    @Before
    public void setUp() {
        String dateStr = "2017-08-04T10:10:20";
        LocalDateTime dateTime = LocalDateTime.parse(dateStr);
        Date date = Date.from(dateTime.toInstant(ZoneOffset.UTC));

        modulrAuth = spy(new ModulrApiAuth("KNOWN-TOKEN", "SECRET-TOKEN"));
        when(modulrAuth.getDate()).thenReturn(date);
    }

    @Test
    public void testHmacGenerator() throws SignatureException {
        Map<String, String> headers = modulrAuth.generateApiAuthHeaders("NONCE");
        assertEquals(headers.size(), 4);
        assertEquals("Signature keyId=\"KNOWN-TOKEN\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"G9zfk3yPn861TKddM6wIxu4u0YU%3D\"", headers.get("Authorization"));
        assertEquals("NONCE", headers.get("x-mod-nonce"));
        assertEquals("Fri, 04 Aug 2017 10:10:20 GMT", headers.get("Date"));
        assertEquals("false", headers.get("x-mod-retry"));


        when(modulrAuth.getDate()).thenReturn(new Date());
        Map<String, String> headersWithRetryOn = modulrAuth.generateRetryApiAuthHeaders();
        assertEquals("true", headersWithRetryOn.get("x-mod-retry"));
        assertEquals(headersWithRetryOn.get("x-mod-nonce"), headers.get("x-mod-nonce"));
        assertNotEquals(headersWithRetryOn.get("Date"), headers.get("Date"));
    }
}
