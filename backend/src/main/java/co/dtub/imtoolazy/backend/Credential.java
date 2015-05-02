package co.dtub.imtoolazy.backend;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
@Index
class Credential {

    @Id
    private Long id;
    private CredentialType type;
    private String userId;
    @Load
    private Ref<Account> account;

    Credential() {}
    Credential(CredentialType type, String userId, Account account) {
        this.type = type;
        this.userId = userId;
        this.account = Ref.create(account);
    }

    Account getAccount() {
        return account.get();
    }
}
