package sync;

public class SyncObj {
    private boolean active;

    public SyncObj() {
    }

    public SyncObj(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
