package datalayer.model.SessionLog;

import java.sql.Date;

public class SessionLog {
    private int sessionLogId;
    private int userId;
    private Date startDate;
    private Date endDate;
    private String lastDirectory;

    public SessionLog() {
    }

    public SessionLog(int userId, Date startDate, Date endDate, String lastDirectory) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastDirectory = lastDirectory;
    }

    public int getSessionLogId() {
        return sessionLogId;
    }

    public void setSessionLogId(int sessionLogId) {
        this.sessionLogId = sessionLogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLastDirectory() {
        return lastDirectory;
    }

    public void setLastDirectory(String lastDirectory) {
        this.lastDirectory = lastDirectory;
    }
}
