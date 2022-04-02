package datalayer.model.SessionLog;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Class that has the Session log methods.
 */
public class SessionLog {

    // region Private properties

    private int sessionLogId;
    private int userId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String lastDirectory;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public SessionLog() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param startDate is the first Session Log date.
     * @param endDate is the last Session Log date.
     * @param lastDirectory is the last accessed directory.
     */
    public SessionLog(int userId, Timestamp startDate, Timestamp endDate, String lastDirectory) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastDirectory = lastDirectory;
    }

    // endregion Public methods

    // region Getters and Setters

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

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getLastDirectory() {
        return lastDirectory;
    }

    public void setLastDirectory(String lastDirectory) {
        this.lastDirectory = lastDirectory;
    }

    // endregion Getters and Setters

}
