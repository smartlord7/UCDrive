package datalayer.model.User;

public class ClientUserSession {
    private int userId;
    private String currentDir;

    public ClientUserSession() {
    }

    public ClientUserSession(int userId, String lastSessionDir) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }
}
