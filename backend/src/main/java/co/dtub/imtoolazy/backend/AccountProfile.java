package co.dtub.imtoolazy.backend;

public class AccountProfile {

    private String name;
    private String email;
    private boolean facebookLinked;
    private boolean googleLinked;

    AccountProfile(Account account) {
        this.name = account.getName();
        this.email = account.getEmail();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFacebookLinked() {
        return facebookLinked;
    }
    public void setFacebookLinked(boolean facebookLinked) {
        this.facebookLinked = facebookLinked;
    }

    public boolean isGoogleLinked() {
        return googleLinked;
    }
    public void setGoogleLinked(boolean googleLinked) {
        this.googleLinked = googleLinked;
    }
}
