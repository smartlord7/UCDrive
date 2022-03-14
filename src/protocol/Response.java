package protocol;

import java.util.HashMap;

public class Response {
    private ResponseStatusEnum status;
    private HashMap<String, String> errors;
    private String data;

    public Response() {
    }

    public Response(ResponseStatusEnum status, HashMap<String, String> errors, String data) {
        this.status = status;
        this.errors = errors;
        this.data = data;
    }

    public ResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusEnum status) {
        this.status = status;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
