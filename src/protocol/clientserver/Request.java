package protocol.clientserver;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestMethodEnum method;
    private String content;

    public Request() {
    }

    public Request(RequestMethodEnum method, String content) {
        this.method = method;
        this.content = content;
    }

    public RequestMethodEnum getMethod() {
        return method;
    }

    public void setMethod(RequestMethodEnum method) {
        this.method = method;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
