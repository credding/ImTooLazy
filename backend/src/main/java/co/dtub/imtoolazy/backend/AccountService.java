package co.dtub.imtoolazy.backend;

import static co.dtub.imtoolazy.backend.OfyService.ofy;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;

import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.googlecode.objectify.Key;

import javax.inject.Named;

@ApiReference(value = BackendService.class)
public class AccountService {

    @ApiMethod(name = "accountService.signUp")
    public SimpleResponse signUp(
            @Named("name") String name,
            @Named("email") String email,
            @Named("password") String password) {

        if (emailIsInUse(email)) {
            return new SimpleResponse(false, "Email is already in use");
        }
        if (nameIsInUse(name)) {
            return new SimpleResponse(false, "Username is already in use");
        }

        Account account = new Account(name, email, password);
        ofy().save().entity(account).now();

        return new SimpleResponse(true);
    }

    @ApiMethod(name = "accountService.loginWithEmail")
    public SimpleResponse loginWithEmail(
            @Named("email") String email,
            @Named("password") String password) {

        Account account = ofy().load().type(Account.class)
                .filter("email", email).first().now();

        if (account == null) {
            return new SimpleResponse(false, "Could not recognize email");
        }
        if (!account.checkPassword(password)) {
            return new SimpleResponse(false, "Incorrect password");
        }

        return new SimpleResponse(true, Long.toString(account.getId()));
    }

    @ApiMethod(name = "accountService.loginWithFacebook")
    public SimpleResponse loginWithFacebook(
            @Named("facebookId") String facebookId) {
        return loginWithCredential(CredentialType.FACEBOOK, facebookId);
    }

    @ApiMethod(name = "accountService.loginWithGoogle")
    public SimpleResponse loginWithGoogle(
            @Named("googleId") String googleId) {
        return loginWithCredential(CredentialType.GOOGLE, googleId);
    }

    @ApiMethod(name = "accountService.getProfile")
    public AccountProfile getProfile(
            @Named("accountId") Long accountId) {

        Account account = ofy().load().type(Account.class).id(accountId).now();
        AccountProfile result = new AccountProfile(account);

        result.setFacebookLinked(hasCredential(account, CredentialType.FACEBOOK));
        result.setGoogleLinked(hasCredential(account, CredentialType.GOOGLE));

        return result;
    }

    @ApiMethod(name = "accountService.changeName")
    public SimpleResponse changeName(
            @Named("accountId") Long accountId,
            @Named("name") String name) {

        Account account = ofy().load().type(Account.class).id(accountId).now();

        if (nameIsInUse(name, account)) {
            return new SimpleResponse(false, "Username is already in use");
        }

        account.setName(name);
        ofy().save().entity(account).now();

        return new SimpleResponse(true);
    }

    @ApiMethod(name = "accountService.changeEmail")
    public SimpleResponse changeEmail(
            @Named("accountId") Long accountId,
            @Named("email") String email) {

        Account account = ofy().load().type(Account.class).id(accountId).now();

        if (emailIsInUse(email, account)) {
            return new SimpleResponse(false, "Email is already in use");
        }

        account.setEmail(email);
        ofy().save().entity(account).now();

        return new SimpleResponse(true);
    }

    @ApiMethod(name = "accountService.linkFacebookAccount")
    public SimpleResponse linkFacebookAccount(
            @Named("accountId") Long accountId,
            @Named("facebookId") String facebookId) {
        return addCredential(accountId, CredentialType.FACEBOOK, facebookId);
    }
    @ApiMethod(name = "accountService.unlinkFacebookAccount")
    public SimpleResponse unlinkFacebookAccount(
            @Named("accountId") Long accountId) {
        return removeCredential(accountId, CredentialType.FACEBOOK);
    }

    @ApiMethod(name = "accountService.linkGoogleAccount")
    public SimpleResponse linkGoogleAccount(
            @Named("accountId") Long accountId,
            @Named("googleId") String googleId) {
        return addCredential(accountId, CredentialType.GOOGLE, googleId);
    }
    @ApiMethod(name = "accountService.unlinkGoogleAccount")
    public SimpleResponse unlinkGoogleAccount(
            @Named("accountId") Long accountId) {
        return removeCredential(accountId, CredentialType.GOOGLE);
    }

    private SimpleResponse loginWithCredential(CredentialType type, String userId) {

        Credential credential = ofy().load().type(Credential.class)
                .filter("type", type)
                .filter("userId", userId).first().now();

        if (credential == null) {
            return new SimpleResponse(false);
        }

        Account account = credential.getAccount();

        return new SimpleResponse(true, Long.toString(account.getId()));
    }

    private SimpleResponse addCredential(Long accountId, CredentialType type, String userId) {

        Account account = ofy().load().type(Account.class).id(accountId).now();

        if (credentialIsInUse(type, userId, account)) {
            return new SimpleResponse(false,
                    "This " + type.getSimpleName() + " account is connected to another account");
        }

        Credential credential = new Credential(type, userId, account);
        ofy().save().entity(credential).now();

        return new SimpleResponse(true);
    }

    private SimpleResponse removeCredential(Long accountId, CredentialType type) {

        Account account = ofy().load().type(Account.class).id(accountId).now();
        Credential credential = ofy().load().type(Credential.class)
                .filter("type", type)
                .filter("account", account).first().now();

        if (credential != null) {
            ofy().delete().entity(credential);
        }

        return new SimpleResponse(true);
    }

    private boolean hasCredential(Account account, CredentialType type) {
        return ofy().load().type(Credential.class)
                .filter("type", type)
                .filter("account", account).keys().first().now() != null;
    }

    private boolean emailIsInUse(String email) {
        return emailIsInUse(email, null);
    }
    private boolean emailIsInUse(String email, Account exclude) {
        Key<Account> accountKey = ofy().load().type(Account.class)
                .filter("email", email).keys().first().now();
        return accountKey != null && (exclude == null || accountKey != Key.create(exclude));
    }

    private boolean nameIsInUse(String name) {
        return emailIsInUse(name, null);
    }
    private boolean nameIsInUse(String name, Account exclude) {
        Key<Account> accountKey = ofy().load().type(Account.class)
                .filter("name", name).keys().first().now();
        return accountKey != null && (exclude == null || accountKey != Key.create(exclude));
    }

    private boolean credentialIsInUse(CredentialType type, String userId) {
        return credentialIsInUse(type, userId, null);
    }
    private boolean credentialIsInUse(CredentialType type, String userId, Account exclude) {
        Key<Credential> credentialKey = ofy().load().type(Credential.class)
                .filter("type", type)
                .filter("userId", userId)
                .filter("account !=", exclude).keys().first().now();
        return credentialKey != null;
    }
}
