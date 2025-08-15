package com.modulr.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ModulrApiAuth {

    private static final String DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.ENGLISH).withZone(ZoneId.of("GMT"));

    private final String keyId;
    private final String hmacSecret;
    private final Clock clock;
    private final HmacAlgorithm hmacAlgorithm;

    public ModulrApiAuth(String keyId, String hmacSecret) {
        this(keyId, hmacSecret, Clock.systemUTC(), HmacAlgorithm.SHA512);
    }

    public ModulrApiAuth(String keyId, String hmacSecret, Clock clock, HmacAlgorithm hmacAlgorithm) {
        this.keyId = Optional.ofNullable(keyId).orElseThrow(() -> new IllegalArgumentException("KeyId cannot be null"));
        this.hmacSecret = Optional.ofNullable(hmacSecret).orElseThrow(() -> new IllegalArgumentException("HmacSecret cannot be null"));
        this.clock = Optional.ofNullable(clock).orElseThrow(() -> new IllegalArgumentException("Clock cannot be null"));
        this.hmacAlgorithm = Optional.ofNullable(hmacAlgorithm).orElseThrow(() -> new IllegalArgumentException("HmacAlgorithm cannot be null"));
    }

    public Map<String, String> generateApiAuthHeaders(String nonce) throws SignatureException {
        return generateApiAuthHeaders(nonce, false);
    }

    public Map<String, String> generateApiAuthHeaders(String nonce, boolean isReplay) throws SignatureException {
        String formattedDate = DATE_TIME_FORMATTER.format(clock.instant().atZone(ZoneId.of("GMT")));
        String signature = generateSignature(nonce, formattedDate);

        return Map.of("Authorization", formatAuthHeader(keyId, signature, hmacAlgorithm),
                "Date", formattedDate,
                "x-mod-nonce", nonce,
                "x-mod-retry", String.valueOf(isReplay));
    }

    @SuppressWarnings("java:S3457") // System specific line separators are required in the HMAC signature specification
    private String generateSignature(String nonce, String formattedDate) throws SignatureException {
        String data = String.format("date: %s\nx-mod-nonce: %s", formattedDate, nonce);
        return calculateHmac(data);
    }

    private String calculateHmac(String content) throws SignatureException {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), hmacAlgorithm.getJavaAlgorithmName());
            Mac mac = Mac.getInstance(hmacAlgorithm.getJavaAlgorithmName());
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));

            String hmac = Base64.getEncoder().encodeToString(rawHmac);
            return URLEncoder.encode(hmac, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new SignatureException("Failed to generate HMAC: " + e.getMessage(), e);
        }
    }

    private static String formatAuthHeader(String keyId, String signature, HmacAlgorithm algorithm) {
        return String.format("Signature keyId=\"%s\",algorithm=\"%s\",headers=\"date x-mod-nonce\",signature=\"%s\"", keyId, algorithm.getApiName(), signature);
    }

    public enum HmacAlgorithm {
        SHA1("HmacSha1", "hmac-sha1"),
        SHA256("HmacSha256", "hmac-sha256"),
        SHA384("HmacSha384", "hmac-sha384"),
        SHA512("HmacSha512", "hmac-sha512");

        private final String javaAlgorithmName;
        private final String apiName;

        HmacAlgorithm(String javaAlgorithmName, String apiName) {
            this.javaAlgorithmName = javaAlgorithmName;
            this.apiName = apiName;
        }

        public String getJavaAlgorithmName() {
            return javaAlgorithmName;
        }

        public String getApiName() {
            return apiName;
        }

    }

}
