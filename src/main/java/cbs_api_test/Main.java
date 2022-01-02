package cbs_api_test;

import static cbs_api_test.EncodeUtils.decodeBase64;
import static cbs_api_test.EncodeUtils.encodeBase64;
import static cbs_api_test.EncodeUtils.encodeHmacSha256;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Main {

	public static void main(String[] args) throws Exception {	

		String apiKey = System.getenv("COINBASE-VIEW-API-KEY");
		String secret = System.getenv("COINBASE-VIEW-API-SECRET");
		String passPhrase = System.getenv("COINBASE-VIEW-API-PASSPHRASE");

		String requestMethod = "GET";
		String requestPath = "/accounts/86...";
		Instant date = ZonedDateTime.now(ZoneId.of("UTC")).toInstant();
		String signature = computeSignature(secret.getBytes(), date, requestMethod, requestPath);

//		****************************************************************************
//		*******   THERE IS ONLY 30 SECONDS LONG WINDOW TO FIRE THE REQUEST   *******
//		****************************************************************************
		System.out.println("public key: " + apiKey);
		System.out.println("pass phrase: " + passPhrase);
		System.out.println("signature: " + signature);
		System.out.println("timestamp: " + date.getEpochSecond());

		System.exit(1);

		HttpGetRequest request = new HttpGetRequest();
		String url = "https://api.exchange.coinbase.com/accounts";
		String[] headers = new String[] {
			"Accept", "application/json",
			"cb-access-key", apiKey,
			"cb-access-passphrase", passPhrase,
			"cb-access-timestamp", String.valueOf(date.getEpochSecond()),
			"cb-access-sign", signature
		};
		String json = request.get(url, headers);
		System.out.println(json);
	}

	private static final String computeSignature(final byte[] secretKey, final Instant date, final String httpMethod, final String requestPath) {
		byte[] decodedSecretKey = decodeBase64(secretKey);
		String method = httpMethod.toUpperCase();
		long seconds = date.getEpochSecond();

		String preHash = seconds + method + requestPath;
		byte[] signature = encodeHmacSha256(decodedSecretKey, preHash.getBytes());

		return new String(encodeBase64(signature));
	}
}
