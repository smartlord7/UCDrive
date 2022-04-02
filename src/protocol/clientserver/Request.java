package protocol.clientserver;

import java.io.Serializable;

/**
 * Class that has the request methods.
 */
public class Request implements Serializable {

    // region Private properties

    private RequestMethodEnum method;
    private String content;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public Request() {
    }

    /**
     * Constructor method.
     * @param method is the method requested to be executed.
     * @param content is the string content.
     */
    public Request(RequestMethodEnum method, String content) {
        this.method = method;
        this.content = content;
    }

    // endregion Public methods

    // region Getters and Setters

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

    // endregion Getters and Setters

}
