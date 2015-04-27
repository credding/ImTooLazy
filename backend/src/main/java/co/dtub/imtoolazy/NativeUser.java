package co.dtub.imtoolazy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Arrays;

@Entity
class NativeUser {

    @Id
    private Long id;
    @Index
    private String email;
    private byte[] salt;
    private byte[] password;

    NativeUser() {}
    NativeUser(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    Long getId() {
        return this.id;
    }

    String getEmail() {
        return email;
    }
    void setEmail(String email) {
        this.email = email;
    }

    boolean checkPassword(String password) {
        return Arrays.equals(Hash.genHash(password, this.salt), this.password);
    }
    void setPassword(String password) {
        this.salt = Hash.genSalt();
        this.password = Hash.genHash(password, salt);
    }
}
