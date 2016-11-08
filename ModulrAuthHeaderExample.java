import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ModulrAuthHeaderExample {
	public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static void main(String[] args) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		Map<String, String> headers  = genHeaders("TOKEN-123-456-789", "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=");
		for(String key : headers.keySet()) {
			System.out.println(key + " = " + headers.get(key));
		}
	}

	public static Map<String, String> genHeaders(String token, String hmac) {
		Map<String, String> headerParams = new HashMap<>();
		if (token == null) {
			throw new IllegalStateException("token required for Modulr API Auth");
		}
		
		if(hmac == null) {
			throw new IllegalStateException("hmac required for Modulr API Auth");
		}
		
		try {
			String nonce = UUID.randomUUID().toString();
			Date now = new Date();
			DateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String nowString = sdf.format(now);
			String data = String.format("date: %s\nx-mod-nonce: %s", nowString, nonce);
			String signature = calculateHMAC(data, hmac);
			
			String auth = String.format("Signature keyId=\"%s\",algorithm=\"%s\",headers=\"date x-mod-nonce\",signature=\"%s\"", token, "hmac-sha1", signature);

			headerParams.put("Authorization", auth);
			headerParams.put("Date", nowString);
			headerParams.put("x-mod-nonce", nonce);
			return headerParams;
		} catch (SignatureException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to build Auth headers for Modulr API Auth");
		}
	}

	protected static String calculateHMAC(String data, String key) throws SignatureException {
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
