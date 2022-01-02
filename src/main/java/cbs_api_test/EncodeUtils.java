package cbs_api_test;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncodeUtils {

	public static final byte[] decodeBase64(byte[] base64Message) {
		return Base64.getDecoder().decode(base64Message);
	}

	public static final byte[] encodeBase64(byte[] messageToEncode) {
		return Base64.getEncoder().encode(messageToEncode);
	}

	public static final byte[] encodeHmacSha256(byte[] secretKey, byte[] message) {
		byte[] hmacSha256 = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
			mac.init(secretKeySpec);
			hmacSha256 = mac.doFinal(message);
		} catch (Exception e) {
			throw new RuntimeException("Failed to calculate hmac-sha256", e);
		}
		return hmacSha256;
	}
}
