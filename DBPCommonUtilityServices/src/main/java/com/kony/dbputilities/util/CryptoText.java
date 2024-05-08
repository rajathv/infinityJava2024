package com.kony.dbputilities.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES Algo.
 * 
 * @author KH2167
 *
 */
public class CryptoText {

	private static final String ALGO = "AES/GCM/PKCS5Padding";
	private static final int ITERATION_COUNT = 10000;
    private static final int KEY_LENGTH_IN_BITS = 128;
	private static final String key = "TheBestSecretKey";
	private static final byte[] SALT = { (byte) 0xAD, (byte) 0X45, (byte) 0xF1, (byte) 0xA3, (byte) 0xC4, (byte) 0x3E, (byte) 0x6F, (byte) 0xAD };
	private static final int TAG_LENGTH = 128;

	/**
	 * Encrypt a string with AES algorithm.
	 *
	 * @param data is a string
	 * @return the encrypted string
	 * @throws Exception
	 */
	public static String encrypt(String data) throws Exception {
        byte[] stretchedKey = null;
        byte[] ivKey = null;
        try {
            stretchedKey = stretchPassword(key.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH_IN_BITS);
            ivKey = stretchPassword(new String(stretchedKey, StandardCharsets.UTF_8).toCharArray(), SALT,
                    ITERATION_COUNT, KEY_LENGTH_IN_BITS);
            final Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(stretchedKey, "AES"), new GCMParameterSpec(TAG_LENGTH,ivKey));
            byte[] encodedValue = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64URLSafeString(encodedValue);
        } catch (Exception e) {
            throw e;
        } finally {
            if (stretchedKey != null && stretchedKey.length > 0) {
                Arrays.fill(stretchedKey, (byte) 0);
            }

            if (ivKey != null && ivKey.length > 0) {
                Arrays.fill(ivKey, (byte) 0);
            }
        }
    }

	/**
	 * Decrypt a string with AES algorithm.
	 *
	 * @param encryptedData is a string
	 * @return the decrypted string
	 */
	public static String decrypt(String encryptedData) throws Exception {
        byte[] stretchedKey = null;
        byte[] ivKey = null;
        try {
            stretchedKey = stretchPassword(key.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH_IN_BITS);
            ivKey = stretchPassword(new String(stretchedKey, StandardCharsets.UTF_8).toCharArray(), SALT,
                    ITERATION_COUNT, KEY_LENGTH_IN_BITS);
            final Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(stretchedKey, "AES"), new GCMParameterSpec(TAG_LENGTH,ivKey));
            byte[] decodedValue = cipher.doFinal(Base64.decodeBase64(encryptedData));
            return new String(decodedValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw e;
        } finally {
            if (stretchedKey != null && stretchedKey.length > 0) {
                Arrays.fill(stretchedKey, (byte) 0);
            }

            if (ivKey != null && ivKey.length > 0) {
                Arrays.fill(ivKey, (byte) 0);
            }
        }
    }

	public static byte[] stretchPassword(char[] password, byte[] salt, int iterations, int keyLength) throws Exception {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey secretKey = skf.generateSecret(spec);
            byte[] res = secretKey.getEncoded();
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

}
