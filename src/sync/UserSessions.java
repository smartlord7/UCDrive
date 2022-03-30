package sync;

import server.ServerUserSession;

import java.util.HashMap;

public class UserSessions {
    private HashMap<String, ServerUserSession> sessions;

    public UserSessions() {
        this.sessions = new HashMap<String, ServerUserSession>();
    }

    public synchronized ServerUserSession addSession(String client) {
        ServerUserSession session = new ServerUserSession();
        sessions.putIfAbsent(client, session);

        return session;
    }

    public synchronized void removeSession(String client) {
        sessions.remove(client);
    }

    public synchronized ServerUserSession getSession(String client) {
        return sessions.get(client);
    }
}
