package co.dtub.imtoolazy;

public class ITLToken {

    String userId;

    ITLToken(Account account) {
        this.userId = Long.toString(account.getId());
    }

    public String getUserId() {
        return this.userId;
    }
}
