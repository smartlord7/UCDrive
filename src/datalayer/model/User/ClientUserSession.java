package datalayer.model.User;

public class ClientUserSession {
    private int userId;
    private String currentDir;

    /**
     * Constructor method.
     */
    public ClientUserSession() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param lastSessionDir is the last session directory.
     */
    public ClientUserSession(int userId, String lastSessionDir) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
    }

    // region Getters and Setters

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

    // endregion Getters and Setters

}
