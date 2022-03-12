package datalayer.model.User;

import microsoft.sql.DateTimeOffset;

public class User {
    private int userId;
    private String userName;
    private String password;
    private DateTimeOffset createDate;

    public User() {
    }

    public User(int userId, String userName, String pasword, DateTimeOffset createDate) {
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

    public DateTimeOffset getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTimeOffset createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
