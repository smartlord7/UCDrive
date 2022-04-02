package datalayer.model.Exception;

import datalayer.base.IDatabaseEntity;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

public class Exception implements IDatabaseEntity, Serializable {
    private int exceptionId;
    private Integer userId;
    private String message;
    private String stack;
    private String source;
    private String other;
    private Timestamp createDate;

    public Exception() {
    }

    public Exception(Integer userId, String message, String stack, String source, String other, Timestamp createDate) {
        this.userId = userId;
        this.message = message;
        this.stack = stack;
        this.source = source;
        this.other = other;
        this.createDate = createDate;
    }

    public Exception(java.lang.Exception exc, Integer userId, String source, String other) {
        this.userId = userId;
        this.message = exc.getMessage();
        this.stack = Arrays.toString(exc.getStackTrace());
        this.source = source;
        this.createDate = Timestamp.from(Calendar.getInstance().toInstant());
        this.other = other;
    }

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
}
