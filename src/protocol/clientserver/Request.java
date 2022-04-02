/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package protocol.clientserver;

import datalayer.model.User.ClientUserSession;

import java.io.Serializable;

/**
 * Class that holds a request data.
 */
public class Request implements Serializable {

    // region Private properties

    private RequestMethodEnum method;
    private String content;
    private ClientUserSession session;

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
    public Request(RequestMethodEnum method, String content, ClientUserSession session) {
        this.method = method;
        this.content = content;
        this.session = session;
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

    public ClientUserSession getSession() {
        return session;
    }

    public void setSession(ClientUserSession session) {
        this.session = session;
    }

    // endregion Getters and Setters

}
