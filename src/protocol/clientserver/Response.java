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

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class that has the response methods.
 */
public class Response implements Serializable {

    // region Private properties

    private ResponseStatusEnum status;
    private HashMap<String, String> errors;
    private String content;
    private boolean isValid;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public Response() {
    }

    /**
     * Constructor method.
     * @param status is the response status.
     * @param errors are the occurred errors.
     * @param content is the content of the string.
     */
    public Response(ResponseStatusEnum status, HashMap<String, String> errors, String content) {
        this.status = status;
        this.errors = errors;
        this.content = content;
    }

    // endregion Public methods

    // region Getters and Setters

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    // endregion Getters and Setters

}
