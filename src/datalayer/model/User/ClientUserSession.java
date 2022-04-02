package datalayer.model.User;

/**
 * Class that has the client user session methods.
 */
public class ClientUserSession {

    // region Private properties

    private int userId;
    private String currentDir;

    // endregion Private properties

    // region Public methods

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

    // endregion Public methods

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
