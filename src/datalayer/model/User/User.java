package datalayer.model.User;

import java.sql.Date;

public class User {
    private int userId;
    private String userName;
    private String password;
    private String newPassword;
    private Date createDate;
    private boolean isAuth;

    public User() {
    }

    public User(int userId, String userName, String pasword, Date createDate) {
        this.userId = userId;
        this.userName = userName;
        this.password = pasword;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }
}
