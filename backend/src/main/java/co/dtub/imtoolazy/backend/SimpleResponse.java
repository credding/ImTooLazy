package co.dtub.imtoolazy.backend;

import com.google.appengine.repackaged.com.google.gson.GsonBuilder;

public class SimpleResponse {

    private boolean success;
    private String data;

    SimpleResponse(boolean success) {
        this(success, "");
    }
    SimpleResponse(boolean success, String data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
