package datalayer.model.User;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String userName;
    private String password;
    private String newPassword;
    private Timestamp createDate;
    private boolean isAuth;

    public User() {
    }

    public User(int userId, String userName, String password, Timestamp createDate) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
    }

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
}
