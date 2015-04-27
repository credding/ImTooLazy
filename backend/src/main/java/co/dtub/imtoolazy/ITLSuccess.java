package co.dtub.imtoolazy;

public class ITLSuccess {

    private boolean success;
    private String message;

    ITLSuccess(boolean success) {
        this(success, "");
    }
    ITLSuccess(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccessful() {
        return this.success;
    }
    public String getMessage() {
        return this.message;
    }
}
