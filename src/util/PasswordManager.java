package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class PasswordManager {

	public class HashedPassword {
		private String hash;
		private String salt;

		public HashedPassword(String hash, String salt) {
			this.hash = hash;
			this.salt = salt;
		}

		public String getHash() {
			return hash;
		}

		public String getSalt() {
			return salt;
		}
	}

	private HashMap<String, HashedPassword> passwords = new HashMap<String, HashedPassword>();
	private final static int ITERATION_NUMBER = 1000;

	public PasswordManager() {

	}

	public byte[] getHash(int iterationNb, String password, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}

	public void createUser(String login, String password) {
		if (login != null && password != null && login.length() <= 100) {
			// Uses a secure Random not a simple Random
			SecureRandom random;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");

				// Salt generation 64 bits long
				byte[] bSalt = new byte[8];
				random.nextBytes(bSalt);

				// Digest computation
				byte[] bDigest = getHash(ITERATION_NUMBER, password, bSalt);
				String sHashDigest = byteToBase64(bDigest);
				String sSalt = byteToBase64(bSalt);

				passwords.put(login, new HashedPassword(sHashDigest, sSalt));

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean authenticate(String username, String password) {
		try {
			boolean userExist = true;
			// INPUT VALIDATION
			if (username == null || password == null) {
				// TIME RESISTANT ATTACK
				// Computation time is equal to the time needed by a legitimate
				// user
				userExist = false;
				username = "";
				password = "";
			}

			String digest, salt;
			if (passwords.containsKey(username)) {
				digest = passwords.get(username).getHash();
				salt = passwords.get(username).getSalt();
			} else {
				// User doesn't exist...
				// TIME RESISTANT ATTACK (Even if the user does not exist the
				// Computation time is equal to the time needed for a legitimate
				// user
				digest = "000000000000000000000000000=";
				salt = "00000000000=";
				userExist = false;
			}

			byte[] bDigest = base64ToByte(digest);
			byte[] bSalt = base64ToByte(salt);

			// Compute the new DIGEST
			byte[] proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);

			return Arrays.equals(proposedDigest, bDigest) && userExist;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkPassword(String username, byte[] hash) {
		return passwords.get(username).getHash().equals(hash);
	}

	public String getSalt(String username) {
		return passwords.get(username).getSalt();
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}
}
