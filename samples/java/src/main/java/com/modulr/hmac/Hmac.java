package com.modulr.hmac;

import com.modulr.api.ModulrApiAuth;

import java.util.Map;
import java.security.SignatureException;
import java.util.logging.Logger;

public class Hmac {

    private static final Logger logger = Logger.getLogger(Hmac.class.getName());

    public static void main(String... args) throws SignatureException {
        ModulrApiAuth modulrAuth = new ModulrApiAuth("KNOWN-TOKEN", "SECRET-TOKEN");

        /* Generate required headers for the given API key, hmac secret and nonce*/
        Map<String, String> headers = modulrAuth.generateApiAuthHeaders("NONCE");

        headers.forEach((key, value) -> logger.info(key + ": " + value));

        /* Generate required headers for the given API key, hmac secret and last used nonce*/
        Map<String, String> retryHeaders = modulrAuth.generateApiAuthHeaders("NONCE", true);

        retryHeaders.forEach((key, value) -> logger.info(key + ": " + value));
    }
}
