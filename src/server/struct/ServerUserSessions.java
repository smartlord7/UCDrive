package server.struct;

import java.util.HashMap;

/**
 * Class that has the server user sessions methods.
 */
public class ServerUserSessions {

    // region Private properties

    private final HashMap<String, ServerUserSession> sessions;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor Method.
     */
    public ServerUserSessions() {
        this.sessions = new HashMap<>();
    }

    /**
     * Method that creates a user session.
     * @param client is the client in the current user session.
     * @return the created session.
     */
    public synchronized ServerUserSession addSession(String client) {
        ServerUserSession session = new ServerUserSession();
        sessions.put(client, session);

        return session;
    }

    /**
     * Method used to remove the session.
     * @param client is the client in the session to be removed.
     */
    public synchronized void removeSession(String client) {
        sessions.remove(client);
    }

    // endregion Public methods

    // region Getters and Setters
    public synchronized ServerUserSession getSession(String client) {
        return sessions.get(client);
    }

    // endregion Getters and Setters
}
