package com.modulr.hmac;

import java.util.Map;

public class Hmac {
    public static void main(String... args) {
        ModulrAuth modulrAuth = new ModulrAuth("KNOWN-TOKEN", "SECRET-TOKEN", "NONCE");
        Map<String, String> headers = modulrAuth.getAuthHeaders();

        headers.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
