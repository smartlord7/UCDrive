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

package datalayer.model.SessionLog;

import datalayer.base.IDatabaseEntity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Class that has the Session log methods.
 */

public class SessionLog implements IDatabaseEntity, Serializable {

    // region Private properties

    private int sessionLogId;
    private int userId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String lastDirectory;

    // endregion Private properties

    // region Constructors

    /**
     * Constructor method.
     */
    public SessionLog() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param startDate is the start date of the session.
     * @param endDate is the end data of the session.
     * @param lastDirectory is the last accessed directory.
     */
    public SessionLog(int userId, Timestamp startDate, Timestamp endDate, String lastDirectory) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastDirectory = lastDirectory;
    }

    // endregion Constructors

    // region Public methods

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

    // endregion Public methods

}
