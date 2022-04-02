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

package datalayer.model.User;

import datalayer.base.IDatabaseEntity;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Class that has the user methods.
 */
public class User implements IDatabaseEntity, Serializable {

    // region Private properties

    private int userId;
    private String userName;
    private String password;
    private String newPassword;
    private Timestamp createDate;
    private boolean isAuth;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public User() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param userName is the user login name.
     * @param password is the user password.
     * @param createDate is the user register date.
     */
    public User(int userId, String userName, String password, Timestamp createDate) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
    }

    // endregion Public methods

    // region Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    // endregion Getters and Setters

}
