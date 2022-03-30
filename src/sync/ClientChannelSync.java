package sync;

import java.util.HashMap;

public class ClientChannelSync {
    private HashMap<String, SyncObj> clientsSyncObjs;

    public ClientChannelSync() {
        this.clientsSyncObjs = new HashMap<String, SyncObj>();
    }

    public synchronized SyncObj addClientSyncObj(String client) {
        SyncObj obj = new SyncObj();
        clientsSyncObjs.putIfAbsent(client, obj);

        return obj;
    }

    public synchronized void removeClientSyncObj(String client) {
        clientsSyncObjs.remove(client);
    }

    public synchronized SyncObj getClientSyncObj(String client) {
        return clientsSyncObjs.get(client);
    }

    public void wait(String client, boolean inverted) throws InterruptedException {
        SyncObj obj = getClientSyncObj(client);

        synchronized (obj) {
            if (inverted) {
                while (!obj.isActive()) {
                    obj.wait();
                }
            } else {
                while (obj.isActive()) {
                    obj.wait();
                }
            }
        }
    }
}
