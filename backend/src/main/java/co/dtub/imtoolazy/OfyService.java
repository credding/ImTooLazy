package co.dtub.imtoolazy;

import com.googlecode.objectify.ObjectifyService;

class OfyService extends ObjectifyService {

    static {
        register(NativeUser.class);
        register(Credential.class);
        register(Account.class);
    }
}
