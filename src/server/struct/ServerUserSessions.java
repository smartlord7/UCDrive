package server.struct;

import java.util.HashMap;

public class ServerUserSessions {
    private final HashMap<String, ServerUserSession> sessions;

    public ServerUserSessions() {
        this.sessions = new HashMap<>();
    }

    public synchronized ServerUserSession addSession(String client) {
        ServerUserSession session = new ServerUserSession();
        sessions.put(client, session);

        return session;
    }

    public synchronized void removeSession(String client) {
        sessions.remove(client);
    }

    public synchronized ServerUserSession getSession(String client) {
        return sessions.get(client);
    }
}
