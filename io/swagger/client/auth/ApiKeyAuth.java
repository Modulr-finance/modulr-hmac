package io.swagger.client.auth;

import java.net.URLEncoder;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.swagger.client.Pair;

public class ApiKeyAuth implements Authentication {

	public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private final String location;
	private final String paramName;

	private String apiKey;
	private String apiKeyPrefix;

	public ApiKeyAuth(String location, String paramName) {
		this.location = location;
		this.paramName = paramName;
	}

	public String getLocation() {
		return location;
	}

	public String getParamName() {
		return paramName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKeyPrefix() {
		return apiKeyPrefix;
	}

	public void setApiKeyPrefix(String apiKeyPrefix) {
		this.apiKeyPrefix = apiKeyPrefix;
	}

	@Override
	public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
		if (apiKey == null) {
			throw new IllegalStateException("apiKey required for Modulr API Auth");
		}
		
		if(apiKeyPrefix == null) {
			throw new IllegalStateException("apiKeyPrefix required for Modulr API Auth");
		}
		
		if (location != "header") {
			throw new IllegalStateException(location + " invalid location for Modulr API Auth");
		} else {
			try {
				String nonce = UUID.randomUUID().toString();
				Date now = new Date();
				DateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				String nowString = sdf.format(now);
				String data = String.format("date: %s\nx-mod-nonce: %s", nowString, nonce);
				String signature = calculateHMAC(data, apiKeyPrefix);
				
				String auth = String.format("Signature keyId=\"%s\",algorithm=\"%s\",headers=\"date x-mod-nonce\",signature=\"%s\"", apiKey, "hmac-sha1", signature);
	
				headerParams.put(paramName, auth);
				headerParams.put("Date", nowString);
				headerParams.put("x-mod-nonce", nonce);
			} catch (SignatureException e) {
				e.printStackTrace();
				throw new IllegalStateException("Failed to build Auth headers for Modulr API Auth");
			}
		}
	}

	protected String calculateHMAC(String data, String key) throws SignatureException {
		try {
			key = key.trim();

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			String hmac = Base64.getEncoder().encodeToString(rawHmac);
			return URLEncoder.encode(hmac, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage(), e);
		}
	}
}
