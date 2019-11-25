package com.modulr.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

public class ModulrApiAuth {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private final String secret;
    private final String apiKey;
    private Date date;
    private Supplier<Date> dateSupplier;

    private String lastUsedNonce;


    public ModulrApiAuth(String apiKey, String secret) {
        this(apiKey, secret, Date::new);
    }

    public ModulrApiAuth(String apiKey, String secret, Supplier<Date> dateSupplier) {
        this.apiKey = setApiKey(apiKey);
        this.secret = setSecret(secret);
        this.dateSupplier = setDateSupplier(dateSupplier);
    }

    public Map<String, String> generateApiAuthHeaders(String nonce) throws SignatureException {
        return buildHeaders(nonce, false);
    }

    public Map<String, String> generateRetryApiAuthHeaders() throws SignatureException {
        return buildHeaders(this.lastUsedNonce, true);
    }

    private Map<String, String> buildHeaders(String nonce, Boolean retry) throws SignatureException {
        final Map<String, String> headerParams = new HashMap<>();
        String hmac = generateHmac(nonce);

        headerParams.put("Authorization", formatAuthHeader(this.apiKey, hmac));
        headerParams.put("Date", getFormattedDate(this.getDate()));
        headerParams.put("x-mod-nonce", nonce);
        headerParams.put("x-mod-retry", String.valueOf(retry));

        this.lastUsedNonce = nonce;

        return headerParams;
    }

    public String generateHmac(String nonce) throws SignatureException {
        this.date = dateSupplier.get();
        String data = String.format("date: %s\nx-mod-nonce: %s", getFormattedDate(this.getDate()), nonce);
        return calculateHmac(data);
    }

    public Date getDate() {
        return date;
    }

    public String getSecret() {
        return secret;
    }

    public String getToken() {
        return token;
    }

    private String formatAuthHeader(String apiKey, String signature) {
        return String.format("Signature keyId=\"%s\",algorithm=\"%s\",headers=\"date x-mod-nonce\",signature=\"%s\"", apiKey, "hmac-sha1", signature);
    }

    private String calculateHmac(final String content) throws SignatureException {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(content.getBytes());

            // base64-encode the hmac
            String hmac = Base64.getEncoder().encodeToString(rawHmac);
            return URLEncoder.encode(hmac, "UTF-8");
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage(), e);
        }
    }

    private String getFormattedDate(Date date) {
        DateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    private Supplier<Date> setDateSupplier(Supplier<Date> dateSupplier) {
        if (dateSupplier == null) {
            throw new IllegalStateException("A date supplier is required for Modulr API Auth");
        }
        return dateSupplier;
    }

    private String setSecret(String secret){
        if (secret == null) {
            throw new IllegalStateException("Secret required for Modulr API Auth");
        }
        return secret.trim();
    }

    private String setApiKey(String apiKey){
        if (apiKey == null){
            return null;
        }
        return apiKey.trim();
    }

}
