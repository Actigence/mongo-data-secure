package com.actigence.mongo.secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by Actigence on 11/22/2015.
 */
public class FieldEncryptionUtil {

    private static final Logger log = LoggerFactory.getLogger(FieldEncryptionUtil.class);

    private static final String ENCRYPTION_KEY_ENV_VARIABLE = "PHMS_ENCRYPTION_KEY";
    private static final String ENCRYPTION_KEY_DEFAULT = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String ALGORITHM = "AES";

    private static String PASSWORD;

    private static Cipher encryptor;

    private static Cipher decryptor;

    static {
        try {
            //setup cipher password
            PASSWORD = setupCipherPassword();
            encryptor = Cipher.getInstance(ALGORITHM);
            encryptor.init(Cipher.ENCRYPT_MODE, generateKey());

            decryptor = Cipher.getInstance(ALGORITHM);
            decryptor.init(Cipher.DECRYPT_MODE, generateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String setupCipherPassword() {
        //trying to get encoding password from environment variables
        String passwordTemp = System.getenv(ENCRYPTION_KEY_ENV_VARIABLE);

        //if password not set then search in system properties.
        if (passwordTemp == null || passwordTemp.equals("")) {
            passwordTemp = System.getProperty(ENCRYPTION_KEY_ENV_VARIABLE);
        } else {
            log.debug("Encryption key read from environment variables.");
        }

        //if still not found use the default hard-coded value.
        if (passwordTemp == null || passwordTemp.equals("")) {
            passwordTemp = ENCRYPTION_KEY_DEFAULT;
            log.debug("Encryption key set to default value.");
        } else {
            log.debug("Encryption key read from System variables.");
        }
        return passwordTemp;
    }

    public static String encrypt(String valueToEnc) {
        try {
            return new BASE64Encoder().encode(encryptor.doFinal(valueToEnc.getBytes()));
        } catch (Exception e) {
            log.error("{} encrypting value.{}", e.getClass().getName(), valueToEnc);
            log.error(e.getMessage());
            return valueToEnc;
        }
    }

    public static String decrypt(String encryptedValue) {
        try {
            byte[] decValue = decryptor.doFinal(new BASE64Decoder().decodeBuffer(encryptedValue));
            return new String(decValue);
        } catch (Exception e) {
            log.error("{} decrypting value.{}", e.getClass().getName(), encryptedValue);
            log.error(e.getMessage());
            return encryptedValue;
        }
    }

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(PASSWORD.getBytes(), ALGORITHM);
    }
}
