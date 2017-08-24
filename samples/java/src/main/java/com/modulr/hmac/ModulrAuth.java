package com.modulr.hmac;

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

public class ModulrAuth {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private String secret;
    private String token;
    private String nonce;
    private Date date;

    public ModulrAuth(String token, String secret, String nonce) {
        this.token = token.trim();
        this.secret = secret.trim();
        this.nonce = nonce.trim();
        this.date = new Date();
    }

    public Map<String, String> getAuthHeaders() {
        final Map<String, String> headerParams = new HashMap<>();
        try {
            String hmac = generateHmac();
            String authorizationHeader = formatAuthHeader(this.token, hmac);
            headerParams.put("Authorization", authorizationHeader);
            headerParams.put("Date", getFormattedDate(this.date));
            headerParams.put("x-mod-nonce", this.nonce);
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return headerParams;
    }

    public String generateHmac() throws SignatureException {
        validateFields();
        String data = String.format("date: %s nx-mod-nonce: %s", getFormattedDate(this.date), this.nonce);
        return calculateHmac(data);
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Date getDate() {
        return date;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private String formatAuthHeader(String token, String signature) {
        return String.format("Signature keyId=\"%s\",algorithm=\"%s\",headers=\"date x-mod-nonce\",signature=\"%s\"", token, "hmac-sha1", signature);
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
            e.printStackTrace();
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage(), e);
        }
    }

    private String getFormattedDate(Date date) {
        DateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    private void validateFields() {
        if (this.token == null) {
            throw new IllegalStateException("Token required for Modulr API Auth");
        }

        if (this.secret == null) {
            throw new IllegalStateException("Secret required for Modulr API Auth");
        }
    }

}
