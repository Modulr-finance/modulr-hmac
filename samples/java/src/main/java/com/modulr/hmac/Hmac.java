package com.modulr.hmac;

import com.modulr.api.ModulrApiAuth;

import java.util.Map;

public class Hmac {
    public static void main(String... args) {
        ModulrApiAuth modulrAuth = new ModulrApiAuth("KNOWN-TOKEN", "SECRET-TOKEN");
        Map<String, String> headers = modulrAuth.generateApiAuthHeaders("NONCE");

        headers.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
