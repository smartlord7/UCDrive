package sync;

import server.UserSession;

import java.util.HashMap;

public class UserSessions {
    private HashMap<String, UserSession> sessions;

    public UserSessions() {
        this.sessions = new HashMap<String, UserSession>();
    }

    public synchronized UserSession addSession(String client) {
        UserSession session = new UserSession();
        sessions.putIfAbsent(client, session);

        return session;
    }

    public synchronized void removeSession(String client) {
        sessions.remove(client);
    }

    public synchronized UserSession getSession(String client) {
        return sessions.get(client);
    }
}
