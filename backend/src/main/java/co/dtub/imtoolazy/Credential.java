package co.dtub.imtoolazy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
class Credential {

    @Id
    private Long id;
    private CredentialType type;
    private String userId;

    Credential() {}
    Credential(CredentialType type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    Long getId() {
        return this.id;
    }
}
