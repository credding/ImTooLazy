package co.dtub.imtoolazy.backend;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Util {

    // Password Hashing Parameters
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 20000;

    private static SecureRandom random = new SecureRandom();
    private static Base64.Encoder encoder = Base64.getEncoder();

    public static String newId() {
        return encoder.encodeToString(
                new BigInteger(258, random).toByteArray());
    }

    public static byte[] genSalt() {
        try {
            byte[] salt = new byte[KEY_SIZE / 8];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
            return salt;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] genHash(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_SIZE);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
