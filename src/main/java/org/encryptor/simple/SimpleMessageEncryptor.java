package org.encryptor.simple;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ilya Tkachuk
 */
public class SimpleMessageEncryptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMessageEncryptor.class);
    private static final String CHARSET_NAME = "UTF-8";

    private final String encAlgorithm;
    private final String encKey;

    public SimpleMessageEncryptor(final String encAlgorithm, String encKey) {
        this.encAlgorithm = encAlgorithm;
        this.encKey = encKey;
    }

    public String encryptMessage(String message) {
        return encryptMessageWithKey(message, encKey);
    }

    public String encryptMessageWithKey(String message, String encKey) {
        try {
            byte[] data = message.getBytes(CHARSET_NAME);
            Cipher cipher = Cipher.getInstance(encAlgorithm);
            SecretKeySpec key = new SecretKeySpec(encKey.getBytes(), encAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data);

            // the base64 encoding is used here to be able to save bytes into any storage even without utf-8 support
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            LOGGER.error("Error while encoding password", e);
            throw new RuntimeException("Cannot encrypt message: " + e.getMessage());
        }
    }

    public String decryptMessage(String message) {
        return decryptMessageWithKey(message, encKey);
    }

    public String decryptMessageWithKey(String message, String encKey) {
        try {
            // the encoded message is save as base64 string previously
            byte[] encryptedData = Base64.decodeBase64(message);
            Cipher c = Cipher.getInstance(encAlgorithm);
            SecretKeySpec k = new SecretKeySpec(encKey.getBytes(), encAlgorithm);
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] data = c.doFinal(encryptedData);

            return new String(data, CHARSET_NAME);
        } catch (Exception e) {
            LOGGER.error("Error while encoding password", e);
            throw new RuntimeException("Cannot decrypt message: " + e.getMessage());
        }
    }
}
