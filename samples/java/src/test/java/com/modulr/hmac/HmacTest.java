package com.modulr.hmac;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class HmacTest {
    private ModulrAuth modulrAuth;

    @Before
    public void setUp() {
        modulrAuth = new ModulrAuth("KNOWN-TOKEN", "SECRET-TOKEN", "NONCE");
    }

    @Test
    public void testHmacGenerator() throws SignatureException {
        String dateStr = "2017-08-04T10:10:20";
        LocalDateTime dateTime = LocalDateTime.parse(dateStr);
        Date date = Date.from(dateTime.toInstant(ZoneOffset.UTC));

        modulrAuth.setDate(date);
        assertEquals("G9zfk3yPn861TKddM6wIxu4u0YU%3D", modulrAuth.generateHmac());

        Map<String, String> headers = modulrAuth.getAuthHeaders();
        assertEquals(headers.size(), 3);
        assertEquals("Signature keyId=\"KNOWN-TOKEN\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"G9zfk3yPn861TKddM6wIxu4u0YU%3D\"", headers.get("Authorization"));
        assertEquals("NONCE", headers.get("x-mod-nonce"));
        assertEquals("Fri, 04 Aug 2017 10:10:20 GMT", headers.get("Date"));
    }
}
