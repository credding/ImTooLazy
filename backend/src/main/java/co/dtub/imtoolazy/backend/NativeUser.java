package co.dtub.imtoolazy.backend;

import com.google.appengine.api.datastore.ShortBlob;

import java.util.Arrays;

public class NativeUser {

    private String email;
    private ShortBlob salt;
    private ShortBlob password;

    public NativeUser() {}
    public NativeUser(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return Arrays.equals(
                Util.genHash(password, this.salt.getBytes()),
                this.password.getBytes());
    }
    public void setPassword(String password) {
        byte[] salt = Util.genSalt();
        byte[] hash = Util.genHash(password, salt);
        this.salt = new ShortBlob(salt);
        this.password = new ShortBlob(hash);
    }
}
