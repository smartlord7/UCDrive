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

package datalayer.model.Exception;

import datalayer.base.IDatabaseEntity;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

/**
 *Class that has the exception methods and implements it.
 */

public class Exception implements IDatabaseEntity, Serializable {

    // region Private properties

    private int exceptionId;
    private Integer userId;
    private String message;
    private String stack;
    private String source;
    private String other;
    private Timestamp createDate;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public Exception() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param message is the exception message string.
     * @param stack is the stack.
     * @param source is the exception source.
     * @param other is any other optional information.
     * @param createDate is the exception occurrence date.
     */
    public Exception(Integer userId, String message, String stack, String source, String other, Timestamp createDate) {
        this.userId = userId;
        this.message = message;
        this.stack = stack;
        this.source = source;
        this.other = other;
        this.createDate = createDate;
    }

    /**
     * Constructor method.
     * @param exc is the exception.
     * @param userId is the user id.
     * @param source is the exception source.
     * @param other is any other optional information.
     */
    public Exception(java.lang.Exception exc, Integer userId, String source, String other) {
        this.userId = userId;
        this.message = exc.getMessage();
        this.stack = Arrays.toString(exc.getStackTrace());
        this.source = source;
        this.createDate = Timestamp.from(Calendar.getInstance().toInstant());
        this.other = other;
    }

    // endregion Public methods

    // region Getters and Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(int exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    // endregion Getters and Setters

}
