package co.dtub.imtoolazy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashSet;
import java.util.Set;

@Entity
@Index
class Account {

    @Id
    private Long id;
    private Set<Key<Credential>> credentials = new HashSet<>();

    private String name;

    Account() {}
    Account(Credential credential) {
        this.credentials.add(Key.create(credential));
    }

    Long getId() {
        return this.id;
    }

    String getName() {
        return this.name;
    }
    void setName(String name) {
        this.name = name;
    }
}
