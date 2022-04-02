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
