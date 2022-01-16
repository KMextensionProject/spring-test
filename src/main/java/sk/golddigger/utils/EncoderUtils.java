package sk.golddigger.utils;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import sk.golddigger.exceptions.ApplicationFailure;

/**
 * This class should provide encoding and hashing algorithms that may be
 * required mainly by the Exchange API/platform or for whatever other needs.
 * 
 * @author mkrajcovic
 */
@Component
public class EncoderUtils {

	private static final Logger logger = Logger.getLogger(EncoderUtils.class);

	public final byte[] decodeBase64(byte[] base64Message) {
		return Base64.getDecoder().decode(base64Message);
	}

	public final byte[] encodeBase64(byte[] messageToEncode) {
		return Base64.getEncoder().encode(messageToEncode);
	}

	public final byte[] encodeHmacSha256(byte[] secretKey, byte[] message) {
		byte[] hmacSha256 = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
			mac.init(secretKeySpec);
			hmacSha256 = mac.doFinal(message);
		} catch (Exception e) {
			String hmacSha256Fail = resolveMessage("hmacSha256Fail", e);
			if (logger.isDebugEnabled()) {
				logger.debug(hmacSha256);
			}
			throw new ApplicationFailure(hmacSha256Fail);
		}
		return hmacSha256;
	}
}