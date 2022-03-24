package protocol;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestMethodEnum method;
    private String data;

    public Request() {
    }

    public Request(RequestMethodEnum method, String data) {
        this.method = method;
        this.data = data;
    }

    public RequestMethodEnum getMethod() {
        return method;
    }

    public void setMethod(RequestMethodEnum method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
