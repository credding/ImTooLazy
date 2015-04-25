package co.dtub.imtoolazy.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

@Api(name = "accountService", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.imtoolazy.dtub.co", ownerName = "backend.imtoolazy.dtub.co", packagePath = ""))
public class AccountService {

    @ApiMethod(name = "loginWithNative")
    public String loginWithNative(
            @Named("email") String email,
            @Named("password") String password) {

    }
}
