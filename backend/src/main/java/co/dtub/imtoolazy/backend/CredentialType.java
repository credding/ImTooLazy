package co.dtub.imtoolazy.backend;

enum CredentialType {
    FACEBOOK("Facebook"),
    GOOGLE("Google");

    private String simpleName;

    private CredentialType(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getSimpleName() {
        return this.simpleName;
    }
}
