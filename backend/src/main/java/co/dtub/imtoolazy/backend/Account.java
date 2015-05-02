package co.dtub.imtoolazy.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Mapify;
import com.googlecode.objectify.annotation.Unindex;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Entity
@Index
class Account {

    // Password Hashing Parameters
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 20000;
    private static final String RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String SECRET_ALGORITHM = "PBKDF2WithHmacSHA1";

    @Id
    private Long id;
    private String name;
    private String email;
    @Unindex
    private byte[] salt;
    @Unindex
    private byte[] password;

    Account() {}
    Account(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.setPassword(password);
    }

    Long getId() {
        return this.id;
    }

    String getName() {
        return this.name;
    }
    void setName(String name) {
        this.name = name;
    }

    String getEmail() {
        return email;
    }
    void setEmail(String email) {
        this.email = email;
    }

    boolean checkPassword(String password) {
        return Arrays.equals(genHash(password, this.salt), this.password);
    }
    void setPassword(String password) {
        this.salt = genSalt();
        this.password = genHash(password, salt);
    }

    public static byte[] genSalt() {
        try {
            byte[] salt = new byte[KEY_SIZE / 8];
            SecureRandom.getInstance(RANDOM_ALGORITHM).nextBytes(salt);
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

            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_ALGORITHM);
            return factory.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
