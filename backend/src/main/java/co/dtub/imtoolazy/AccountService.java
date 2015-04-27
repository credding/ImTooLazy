package co.dtub.imtoolazy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import javax.inject.Named;

@Api(name = "backend", version = "v1", namespace = @ApiNamespace(
        ownerDomain = "imtoolazy.dtub.co",
        ownerName = "DevTub"))
public class AccountService {

    @ApiMethod(name = "signUpWithNative")
    public ITLToken signUpWithNative(
            @Named("email") String email,
            @Named("password") String password) {

        Key<NativeUser> userKey = ObjectifyService.ofy().load().type(NativeUser.class)
                .filter("email ==", email).keys().first().now();
        if (userKey == null) {
            NativeUser user = new NativeUser(email, password);
            ObjectifyService.ofy().save().entity(user).now();
            return login(CredentialType.NATIVE, Long.toString(user.getId()));
        } else {
            return null;
        }
    }

    @ApiMethod(name = "loginWithNative")
    public ITLToken loginWithNative(
            @Named("email") String email,
            @Named("password") String password) {

        NativeUser user = ObjectifyService.ofy().load().type(NativeUser.class)
                .filter("email ==", email).first().now();
        if (user != null && user.checkPassword(password)) {
            return login(CredentialType.NATIVE, Long.toString(user.getId()));
        } else {
            return null;
        }
    }

    @ApiMethod(name = "loginWithFacebook")
    public ITLToken loginWithFacebook(@Named("userId") String userId) {
        return login(CredentialType.FACEBOOK, userId);
    }

    @ApiMethod(name = "loginWithGoogle")
    public ITLToken loginWithGoogle(@Named("userId") String userId) {
        return login(CredentialType.GOOGLE, userId);
    }

    @ApiMethod(name = "updateUser")
    public ITLSuccess updateUserName(ITLToken token, @Named("userName") String userName) {
        Key<Account> accountKey = ObjectifyService.ofy().load().type(Account.class)
                .filter("name ==", userName).keys().first().now();
        if (accountKey == null) {
            Account account = ObjectifyService.ofy().load().key(Key.create(Account.class, token.userId)).now();
            account.setName(userName);
            return new ITLSuccess(true);
        } else {
            return new ITLSuccess(false, "Name is already in use");
        }
    }

    private ITLToken login(CredentialType type, String userId) {
        Credential credential = ObjectifyService.ofy().load().type(Credential.class)
                .filter("type ==", type)
                .filter("userId ==", userId).first().now();
        Account account;
        if (credential != null) {
            account = ObjectifyService.ofy().load().type(Account.class)
                    .filter("credentials ==", credential).first().now();
        } else {
            credential = new Credential(type, userId);
            ObjectifyService.ofy().save().entity(credential).now();
            account = new Account(credential);
            ObjectifyService.ofy().save().entity(account).now();
        }
        return new ITLToken(account);
    }
}
